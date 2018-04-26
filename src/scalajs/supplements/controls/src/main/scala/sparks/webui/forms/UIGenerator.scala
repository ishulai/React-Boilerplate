package sparks.webui.forms

import org.scalajs.dom.raw._
import sparks.webui.base.DomFunction
import sparks.webui.entity.{Entity, FieldSpec, FieldType}

class UIGenerator(form: EntityForm, entity: Entity) extends DomFunction {
  def run(): HTMLElement = run(entity.fieldSpecs: _*)

  def run(specs: Any*): HTMLElement = run("fsRow", specs: _*)

  def run(t: String, specs: Any*): HTMLElement = {
    val container = Tags.div
    container.classList.add(t)
    val opposite = t match {
      case "fsRow" => "fsColumn"
      case "fsColumn" => "fsRow"
    }
    specs.foreach(spec => spec match {
      case s: FieldSpec => generate(container, s)
      case s: List[Any] => container.appendChild(run(opposite, s: _*))
    })
    container
  }

  def generate(container: HTMLElement, spec: FieldSpec, display: String = null): Int = {
    println(spec)
    
    var rows = 0
    var firstRow: HTMLElement = null
    var firstImg: HTMLImageElement = null

    if(spec.label.nonEmpty && FieldType.hasIndependentLabel(spec.fieldType)) {
      firstRow = Tags.div
      if(display != null) firstRow.style.display = display
      if(spec.fieldType == FieldType.Collapsable) {
        firstImg = Tags.img
        firstImg.src = "/images/icons/expand.png"
        firstRow.appendChild(firstImg)
        firstRow.appendChild(Tags.spacer(1))
        firstRow.appendChild(Tags.span(spec.label))
      } else {
        firstRow.textContent = spec.label
      }
      container.appendChild(firstRow)
      rows += 1
    }

    val ctrl: HTMLElement = spec.fieldType match {
      case FieldType.Text => Tags.input
      case FieldType.Password => Tags.password
      case FieldType.TextArea => Tags.textArea
      case FieldType.RadioButton => Tags.radioButton(spec.label, spec.userData.toString, form.uniqueId(spec.name))
      case FieldType.RadioGroup | FieldType.RadioGroupWithInput =>
        spec.userData match {
          case e: Enumeration =>
            import Tags._
            val lastCell =
              td(d => {
                d.style.width = "100%"
                if (spec.fieldType == FieldType.RadioGroupWithInput) {
                  val inp = input
                  inp.id = form.uniqueId(s"${spec.name}_input")
                  inp
                }
                else null
              })
            Tags.table(
              tr(e.values.toSeq.map(v => td(radioButton(v.toString, v.toString, form.uniqueId(spec.name)))) :+ lastCell: _*))
          case _ => throw new Exception("The type of user data is not supported.")
        }
      case FieldType.Checkbox => Tags.checkbox(spec.label, spec.name)
      case FieldType.Select =>
        spec.userData match {
          case e: Enumeration => Tags.select(0, e)
          case _ =>
            // Pattern matching cannot match Seq[(String, String)] (non-variable type)
            try {
              Tags.select(0, spec.userData.asInstanceOf[Seq[(String, String)]]: _*)
            }
            catch {
              case _: Exception => throw new Exception("The type of user data is not supported.")
            }
        }
      case FieldType.UploadArea =>
        Tags.div(d => {
          d.style.textAlign = "center"
          d.style.lineHeight = "100px"
          d.style.height = "100px"
          d.style.border = "dashed 2px lightsteelblue"
          d.textContent = "Drop file here to upload or "
          Tags.upload("uploadFile")
        })

      case FieldType.Custom =>
        if (spec.customRenderer != null)
          spec.customRenderer(spec)
        else
          Tags.span("Custom renderer needed for this spec.")

      case _ => null
    } 

    if(ctrl != null) {
      val secondRow = Tags.tr
      if(display != null) secondRow.style.display = display
      
      if (FieldType.needsId(spec.fieldType))
        ctrl.id = form.uniqueId(spec.name)
      else if (spec.fieldType == FieldType.Checkbox)
        ctrl.firstChild.firstChild.firstChild.asInstanceOf[HTMLElement].id = form.uniqueId(spec.name)

      if (spec.width > 0)
        ctrl.style.width = spec.width + "px"
      else
        ctrl.style.width = "99%"

      if (spec.height > 0) ctrl.style.height = spec.height + "px"

      if (spec.validator != null) spec.validator.element = ctrl

      secondRow.appendChild(ctrl)
      container.appendChild(secondRow)
      rows += 1 
    }

    if(spec.childSpecs != null) {
      var childRows = 0
      spec.childSpecs.foreach {
        childRows += generate(
          container,
          _,
          if(spec.fieldType == FieldType.Collapsable) "none" else null
        )
      }

      
      if (spec.fieldType == FieldType.Collapsable) {
        firstRow.setAttribute("expanded", "0")
        firstRow.onclick = (e: MouseEvent) => {
          val expanded = firstRow.getAttribute("expanded") == "1"
          if (expanded) {
            firstImg.src = "/images/icons/expand.png"
            var current = firstRow
            for (i <- 0 until childRows) {
              current = current.nextSibling.asInstanceOf[HTMLTableRowElement]
              current.style.display = "none"
            }
            firstRow.setAttribute("expanded", "0")
          }
          else {
            firstImg.src = "/images/icons/collapse.png"
            var current = firstRow
            for (i <- 0 until childRows) {
              current = current.nextSibling.asInstanceOf[HTMLTableRowElement]
              current.style.display = "table-row"
            }
            firstRow.setAttribute("expanded", "1")
          }

          if (form.resize != null) form.resize()
        }
      }
    }
    rows
  }
}

object UIGenerator {
  def apply(form: EntityForm, entity: Entity) = new UIGenerator(form, entity)
}
