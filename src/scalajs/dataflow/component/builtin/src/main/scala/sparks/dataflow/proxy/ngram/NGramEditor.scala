package sparks.dataflow.proxy.ngram

import org.scalajs.dom.raw.HTMLTableRowElement
import sparks.dataflow.protocols.Auxiliaries.Field
import sparks.dataflow.proxy.ProxyEditor
import sparks.dataflow.proxy.controls.SchemaGrid.GridSwitches
import sparks.webui.entity.validators.RangeValidator
import sparks.webui.entity.{FieldSpec, FieldType}

case class NGramEditor(proxy: NGramProxy) extends ProxyEditor {

  val title = "N-gram"
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

  grid.defaultCaptions(2) = "Transform To"
  grid.onRowChecked = (row: HTMLTableRowElement, checked: Boolean) => {
    if (checked && row.cells(2).textContent.isEmpty)
      row.cells(2).textContent = row.cells(1).textContent + "_ngram"
  }

  override val fieldSpecs = Seq(
    FieldSpec("n", "N", FieldType.Text, validator = RangeValidator(2, 99)),
    SchemaGridFieldSpec(
      "columns",
      "Select columns to transform",
      grid,
      validationRule = () => grid.checkedCount > 0
    )
  )

  override def fill() ={
    super.fill()
    form.setValue("n", proxy.n.toString)
  }

  override def collect() = {
    super.collect()
    proxy.n = form.valueOf("n").toInt

    proxy.node.schema = proxy.node.schema.join(grid.collect())
    proxy.node.outPort.schema =
      proxy.node.schema.clearAlias.selectAll ++ proxy.node.schema.selectedFields.map(f => Field(f.alias, "Array[String]"))
  }
}
