package sparks.dataflow.proxy.split

import io.circe.syntax._
import sparks.dataflow.protocols.Component.Component
import sparks.dataflow.protocols.processmodel.ActivityContext
import sparks.dataflow.protocols.split.{SplitBy, SplitContext}
import sparks.dataflow.proxy.BaseProxy

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("sparks.dataflow.proxy.split.SplitProxy")
case class SplitProxy(spec: Component) extends BaseProxy {

  var splitBy = SplitBy.Default
  var regex = ""
  var delimiters = ""

  override def editor = SplitEditor(this)

  def validate() = node.inputSchema != null && node.isValid

  def toProtocol: ActivityContext =
    ActivityContext(
      spec.jarDesc,
      SplitContext(splitBy, regex, delimiters, node.schema.fields).asJson.noSpaces
    )
}
