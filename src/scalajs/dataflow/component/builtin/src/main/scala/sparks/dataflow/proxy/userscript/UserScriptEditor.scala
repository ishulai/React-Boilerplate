package sparks.dataflow.proxy.userscript

import ace._
import sparks.dataflow.implicits._
import sparks.dataflow.protocols.userscript.ScriptLanguage
import sparks.dataflow.proxy.ProxyEditor
import sparks.dataflow.proxy.controls.SchemaEditor
import sparks.dataflow.proxy.controls.SchemaGrid.GridSwitches
import sparks.webui.entity.validators.CustomValidator
import sparks.webui.entity.{FieldSpec, FieldType}

case class UserScriptEditor(proxy: UserScriptProxy) extends ProxyEditor {

  if (proxy.node.schema == null && proxy.node.inputSchema != null)
    proxy.node.schema = proxy.node.inputSchema.unselectAll

  private var scriptEditor: Editor = _
  private val schemaEditor: SchemaEditor =
    SchemaEditor(
      proxy.node.schema,
      GridSwitches(
        alias = false,
        newType = false
      )
    )

  val title = "User script"
  override val fixedUIWidth = 800
  override val fieldSpecs = Seq(
    FieldSpec("language", "Language", FieldType.Select, userData = ScriptLanguage),
    FieldSpec(
      "script",
      "",
      FieldType.Custom,
      customRenderer = (spec: FieldSpec) =>
        Tags.div(d => {
          d.style.border = "solid 1px #bbbbbb"
          d.style.width = "100%"
          d.style.height = "250px"
          scriptEditor = Ace.edit(d)
          scriptEditor.setTheme("ace/theme/tomorrow")
          scriptEditor.session.setMode("ace/mode/scala")
          null
        }),
      validator = CustomValidator(_ => {
        val text = scriptEditor.getValue()
        text != null && text.nonEmpty
      })
    ),
    SchemaEditorFieldSpec("out", "Output schema", schemaEditor, 150, validationRule = () => schemaEditor.nonEmpty)
  )

  override def fill() = {
    super.fill()
    form.setValue("language", proxy.language.toString)
    scriptEditor.setValue(proxy.script)
  }

  override def collect() = {
    super.collect()
    proxy.language = ScriptLanguage.withName(form.valueOf("language"))
    proxy.script = scriptEditor.getValue()
    proxy.node.schema = schemaEditor.collect()
    proxy.node.outPort.schema = proxy.node.schema.selectAll
  }

}
