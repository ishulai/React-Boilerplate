package sparks.dataflow.proxy.ngram

import io.circe.syntax._
import sparks.dataflow.protocols.Component.Component
import sparks.dataflow.protocols.ngram.NGramContext
import sparks.dataflow.protocols.processmodel.ActivityContext
import sparks.dataflow.proxy.BaseProxy

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("sparks.dataflow.proxy.ngram.NGramProxy")
case class NGramProxy(spec: Component) extends BaseProxy {

  var n: Int = 2

  override def editor = NGramEditor(this)

  def validate() = node.inputSchema != null && node.isValid

  def toProtocol: ActivityContext =
    ActivityContext(
      spec.jarDesc,
      NGramContext(n, node.schema.fields).asJson.noSpaces
    )
}
