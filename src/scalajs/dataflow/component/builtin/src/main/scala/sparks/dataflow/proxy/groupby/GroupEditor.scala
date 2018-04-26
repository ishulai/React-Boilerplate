package sparks.dataflow.proxy.groupby

import sparks.dataflow.protocols.Auxiliaries.Schema
import sparks.dataflow.proxy.ProxyEditor
import sparks.dataflow.proxy.controls.SchemaGrid._
import sparks.webui.base.AjaxClient

case class GroupEditor(proxy: GroupProxy) extends ProxyEditor with AjaxClient {

  val title = "Group"
  override val fixedUIWidth = 600

  if (proxy.node.schema == null) {
    proxy.node.schema = proxy.node.inputSchema
    proxy.groups = proxy.node.schema.unselectAll
    proxy.aggregations = Schema(proxy.node.schema.fields.filter(f => f.dataType != "String" && f.dataType != "Date")).unselectAll
  }

  private val gg = SchemaGrid(proxy.groups, GridSwitches(alias = false, newType = false, nullable = false, side = false, userfunc = false, aggfunc = false))
  private val ag = SchemaGrid(
    proxy.aggregations,
    GridSwitches(newType = false, nullable = false, side = false),
    GridSwitches(name = false, dataType = false, newType = false, nullable = false, side = false, userfunc = false)
  )

  override val fieldSpecs =
    Seq(
      SchemaGridFieldSpec("", "Group by", gg, height = 180, validationRule = ()=>gg.checkedCount > 0),
      SchemaGridFieldSpec("", "Aggregations", ag, height = 180, validationRule = ()=>ag.checkedCount > 0)
    )

  override def collect() = {
    super.collect()
    proxy.groups = gg.collect()
    proxy.aggregations = ag.collect()
    proxy.node.outPort.schema = proxy.groups.selected.join(proxy.aggregations.selected)
  }

}
