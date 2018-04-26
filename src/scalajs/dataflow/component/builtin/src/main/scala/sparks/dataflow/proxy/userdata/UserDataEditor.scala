package sparks.dataflow.proxy.userdata

import ace.{Ace, Editor}
import sparks.dataflow.protocols.userdata.Delimiter
import sparks.dataflow.proxy.ProxyEditor
import sparks.dataflow.proxy.controls.SchemaEditor
import sparks.dataflow.proxy.controls.SchemaGrid.GridSwitches
import sparks.webui.entity.validators.CustomValidator
import sparks.webui.entity.{FieldSpec, FieldType}
import uifrwk.ClientToolbar.{Button, Seperator}

import scala.scalajs.js

case class UserDataEditor(proxy: UserDataProxy) extends ProxyEditor {

  val title = "User data"
  override val fixedUIWidth = 640

  private var dataEditor: Editor = _
  private val schemaEditor: SchemaEditor =
    SchemaEditor(
      proxy.node.schema,
      GridSwitches(
        alias = false,
        newType = false
      ),
      Seq(
        Seperator(),
        Button("syncstru.png", "Extract columns from data", () => extractColumns())
      )
    )

  override val fieldSpecs = Seq(
    FieldSpec(
      "delimiter",
      "Delimiter",
      FieldType.RadioGroupWithInput,
      userData = Delimiter,
      validator = CustomValidator(_ => delimiter.nonEmpty)
    ),
    FieldSpec("header", "First line is header", FieldType.Checkbox),
    FieldSpec(
      "data",
      "",
      FieldType.Custom,
      customRenderer = _ =>
        // Need an outer div to avoid changing style of the editor container
        Tags.div(_ => {
          Tags.div(d => {
            d.style.border = "solid 1px #bbbbbb"
            d.style.width = "100%"
            d.style.height = "250px"
            dataEditor = Ace.edit(d)
            dataEditor.setTheme("ace/theme/tomorrow")
            dataEditor.session.setMode("ace/mode/scala")
            null
          })
        }),
      validator = CustomValidator(_ => {
        val s = dataEditor.getValue().trim()
        if (s.nonEmpty) {
          val lines = dataEditor.getValue().split(System.lineSeparator).map(_.split(delimiter))
          if (lines.nonEmpty) {
            var isValid = true
            val c = lines(0).length
            for (i <- lines.indices)
              if (lines(i).lengthCompare(c) != 0) {
                isValid = false
                dataEditor.session.setBreakpoint(i)
              }
            isValid
          }
          else false
        }
        else false
      })
    ),
    SchemaEditorFieldSpec("output", "Output schema", schemaEditor, 150, validationRule = () => schemaEditor.nonEmpty)
  )

  override def fill() = {
    super.fill()
    form.setValue("delimiter", proxy.delimiter.toString)
    form.setValue("delimiter_input", proxy.userDelimiter)
    form.setValue("header", proxy.hasHeader)
    dataEditor.setValue(proxy.userData)
    dataEditor.clearSelection()
    form.error.clearHandler = () => dataEditor.session.clearBreakpoints()
  }

  override def collect() = {
    super.collect()
    proxy.delimiter = Delimiter.withName(form.valueOf("delimiter"))
    proxy.userDelimiter = form.valueOf("delimiter_input")
    proxy.hasHeader = form.valueOf("header").toBoolean
    proxy.userData = dataEditor.getValue()
    proxy.node.schema = schemaEditor.collect()
    proxy.node.outPort.schema = proxy.node.schema.selectAll
  }

  private def delimiter = {
    val a = Delimiter.withName(form.valueOf("delimiter"))
    val b = form.valueOf("delimiter_input")

    import Delimiter._
    a match {
      case Tab => "\\t"
      case Semicolon => ";"
      case Comma => ","
      case Space => " "
      case Other => b
    }
  }

  private def extractColumns() = {

    def getType(s: String) = {
      val ts = s.trim.replaceAll("/\\$|,/g", "")
      val c = ts.count(_ == '.')
      if (c == 0 && ts.forall(Character.isDigit)) "Int"
      else if (c == 1 && ts.replace(".", "").forall(Character.isDigit)) "Double"
      else if ((s.count(_ == '-') >= 2 || s.count(_ == '/') >= 2) && !js.Date.parse(s).isNaN) "Date"
      else "String"
    }

    if (!validate("output"))
      form.messageBox.showError("Please correct all inconsistant lines.")
    else {
      val lines = dataEditor.getValue().split(System.lineSeparator)
      if (lines.nonEmpty) {
        schemaEditor.clear()

        var header: Array[String] = null
        var data: Array[String] = null
        val hasHeader = form.valueOf("header").toBoolean
        if (hasHeader) {
          val t = lines.splitAt(1)
          header = t._1(0).split(delimiter)
          data = t._2
        }
        else
          data = lines

        var c = 0
        var types: Array[String] = null
        data
          .sample(50)
          .map(_.split(delimiter))
          .foreach(line => {
            if (types == null) {
              c = line.length
              types = Array.fill(c)("")
            }

            for (i <- 0 until c) {
              val t = getType(line(i))
              if (types(i).isEmpty || (t == "String" && types(i) != "String")) types(i) = t
              //if (t == "String" && types(i) != "String") types(i) = t
            }
          })

        if (hasHeader)
          for (i <- 0 until c) schemaEditor.addNewColumn(header(i), types(i))
        else
          for (i <- 0 until c) schemaEditor.addNewColumn(s"C${i + 1}", types(i))
      }
    }
  }

  implicit class StringArrayExtension(a: Array[String]) {

    def sample(num: Int) = {
      val len = a.length
      val interval = len / num
      if (interval < 1) a else (for (i <- a.indices by interval) yield a(i)).toArray
    }

  }

}
