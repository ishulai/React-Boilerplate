package sparks.dataflow.proxy.userscript

import io.circe.syntax._
import sparks.dataflow.protocols.Component.Component
import sparks.dataflow.protocols.processmodel.ActivityContext
import sparks.dataflow.protocols.userscript.{ScriptLanguage, UserScriptContext}
import sparks.dataflow.proxy.BaseProxy

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("sparks.dataflow.proxy.userscript.UserScriptProxy")
case class UserScriptProxy(spec: Component) extends BaseProxy {

  var language = ScriptLanguage.Scala
  var script = ""

  override def editor = UserScriptEditor(this)

  //def validate() = node.inPorts.isValid && node.outPorts.isValid
  def validate() = true

  def toProtocol: ActivityContext =
    ActivityContext(
      spec.jarDesc,
      UserScriptContext(language, script).asJson.noSpaces
    )
}
