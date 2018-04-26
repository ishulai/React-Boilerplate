package sparks.dataflow.proxy.select

import sparks.dataflow.protocols.Auxiliaries.Schema
import sparks.dataflow.proxy.ProxyEditor
import sparks.dataflow.proxy.controls.SchemaGrid._
import sparks.webui.base.AjaxClient

case class SelectEditor(proxy: SelectProxy) extends ProxyEditor with AjaxClient {

  val title = "Select data"
  override val fixedUIWidth = 600

  if (proxy.node.schema == null) proxy.node.schema = proxy.node.inputSchema

  private val grid = SchemaGrid(proxy.node.schema, GridSwitches(side = false, aggfunc = false))

  override val fieldSpecs = Seq(SchemaGridFieldSpec("schema", "Output schema", grid, validationRule = () => grid.checkedCount > 0))

  override def collect() = {
    super.collect()
    proxy.node.schema = grid.collect()
    proxy.node.outPort.schema = Schema(proxy.node.schema.selectedFields)
  }

}
