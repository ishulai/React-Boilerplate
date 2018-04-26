package sparks.dataflow.proxy.userdata

import io.circe.syntax._
import sparks.dataflow.protocols.Component.Component
import sparks.dataflow.protocols.processmodel.ActivityContext
import sparks.dataflow.protocols.userdata.{Delimiter, UserDataContext}
import sparks.dataflow.proxy.BaseProxy

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("sparks.dataflow.proxy.userdata.UserDataProxy")
case class UserDataProxy(spec: Component) extends BaseProxy {

  var userData = ""
  var delimiter = Delimiter.Tab
  var userDelimiter = ""
  var hasHeader = false

  override def editor = UserDataEditor(this)

  def validate() = node.isValid

  def toProtocol: ActivityContext =
    ActivityContext(
      spec.jarDesc,
      UserDataContext(
        userData
          .split(System.lineSeparator)
          .splitAt(if (hasHeader) 1 else 0)
          ._2
          .map(
            _.split(
              delimiter match {
                case Delimiter.Tab => "\\t"
                case Delimiter.Comma => ","
                case Delimiter.Semicolon => ";"
                case Delimiter.Space => " "
                case Delimiter.Other => userDelimiter
              }
            )
          ),
        node.schema.fields).asJson.noSpaces
    )
}

