package sparks.dataflow.proxy.groupby

import io.circe.syntax._
import sparks.dataflow.protocols.Auxiliaries.Schema
import sparks.dataflow.protocols.Component.Component
import sparks.dataflow.protocols.groupby.GroupContext
import sparks.dataflow.protocols.processmodel.ActivityContext
import sparks.dataflow.proxy.BaseProxy

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("sparks.dataflow.proxy.groupby.GroupProxy")
case class GroupProxy(spec: Component) extends BaseProxy {

  var groups: Schema = _
  var aggregations: Schema = _

  override def editor = GroupEditor(this)

  def validate() = node.inputSchema != null && node.isValid

  def toProtocol: ActivityContext =
    ActivityContext(
      spec.jarDesc,
      GroupContext(groups.selectedFields, aggregations.selectedFields).asJson.noSpaces
    )

}