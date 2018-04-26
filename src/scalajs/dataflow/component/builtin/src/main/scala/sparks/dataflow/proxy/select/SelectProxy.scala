package sparks.dataflow.proxy.select

import io.circe.syntax._
import sparks.dataflow.protocols.Component.Component
import sparks.dataflow.protocols.processmodel.ActivityContext
import sparks.dataflow.protocols.select.SelectContext
import sparks.dataflow.proxy.BaseProxy

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("sparks.dataflow.proxy.select.SelectProxy")
case class SelectProxy(spec: Component) extends BaseProxy {

  override def editor = SelectEditor(this)

  def validate() = node.inputSchema != null && node.isValid

  def toProtocol: ActivityContext =
    ActivityContext(
      spec.jarDesc,
      SelectContext(node.schema.selectedFields).asJson.noSpaces
    )
}
