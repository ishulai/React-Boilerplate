package sparks.dataflow.proxy.split

import org.scalajs.dom.raw.HTMLTableRowElement
import sparks.dataflow.implicits._
import sparks.dataflow.protocols.Auxiliaries.Field
import sparks.dataflow.protocols.split.SplitBy
import sparks.dataflow.proxy.ProxyEditor
import sparks.dataflow.proxy.controls.SchemaGrid.GridSwitches
import sparks.webui.entity.validators.CustomValidator
import sparks.webui.entity.{FieldSpec, FieldType}

case class SplitEditor(proxy: SplitProxy) extends ProxyEditor {

  val title = "Split"
  override val fixedUIWidth = 480

  if (proxy.node.schema == null) proxy.node.schema = proxy.node.inputSchema.unselectAll

  private val grid = SchemaGrid(
    proxy.node.schema,
    GridSwitches(
      dataType = false,
      newType = false,
      nullable = false,
      side = false,
      aggfunc = false
    )
  )

  grid.defaultCaptions(2) = "Split To"
  grid.onRowChecked = (row: HTMLTableRowElement, checked: Boolean) => {
    if (checked && row.cells(2).textContent.isEmpty)
      row.cells(2).textContent = row.cells(1).textContent + "_Split"
  }

  override val fieldSpecs = Seq(
    FieldSpec("sptype", "Use default splitting logic", FieldType.RadioButton, userData = "Default"),
    FieldSpec("sptype", "Use regular expression", FieldType.RadioButton, userData = "Regex"),
    FieldSpec("regex", "", FieldType.Text, validator = CustomValidator(v => if (form.valueOf("sptype") == "Regex") v.nonEmpty else true)),
    FieldSpec("sptype", "Use delimiter list", FieldType.RadioButton, userData = "Delimiters"),
    FieldSpec("delimiters", "", FieldType.Text, validator = CustomValidator(v => if (form.valueOf("sptype") == "Delimiters") v.nonEmpty else true)),
    SchemaGridFieldSpec(
      "columns",
      "Select columns to split",
      grid,
      height = 200,
      validationRule = () => grid.checkedCount > 0
    )
  )

  override def fill() ={
    super.fill()
    form.setValue("sptype", proxy.splitBy.toString)
    form.setValue("regex", proxy.regex)
    form.setValue("delimiters", proxy.delimiters)
  }

  override def collect() = {
    super.collect()
    proxy.splitBy = SplitBy.withName(form.valueOf("sptype"))
    proxy.regex = form.valueOf("regex")
    proxy.delimiters = form.valueOf("delimiters")

    proxy.node.schema = proxy.node.schema.join(grid.collect())
    proxy.node.outPort.schema =
      proxy.node.schema.clearAlias.selectAll ++ proxy.node.schema.selectedFields.map(f => Field(f.alias, "Array[String]"))
  }

}