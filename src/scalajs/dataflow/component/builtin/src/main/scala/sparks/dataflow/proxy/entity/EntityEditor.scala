package sparks.dataflow.proxy.entity

import sparks.dataflow.proxy.ProxyEditor
import sparks.dataflow.proxy.controls.SchemaGrid._
import sparks.webui.base.AjaxClient

case class EntityEditor(proxy: EntityProxy) extends ProxyEditor with AjaxClient {

  val title = "Entity settings"
  override val fixedUIWidth = 500

  private val grid = SchemaGrid(proxy.node.schema, GridSwitches(side = false, aggfunc = false))

  override val fieldSpecs = Seq(SchemaGridFieldSpec("schema", "Output schema", grid, validationRule = () => grid.checkedCount > 0))

  override def collect() = {
    super.collect()
    proxy.node.schema = grid.collect()
    proxy.node.outPort.schema = proxy.node.schema.finalSchema
  }
}
