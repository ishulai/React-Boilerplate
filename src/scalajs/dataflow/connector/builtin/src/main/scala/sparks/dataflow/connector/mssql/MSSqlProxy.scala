package sparks.dataflow.connector.mssql

import io.circe.syntax._
import sparks.dataflow.connector.{ConnectorDef, ConnectorProxy}
import sparks.dataflow.protocols.Connector.ConnectorProtocol
import sparks.dataflow.protocols.mssql.MSSqlParameters

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("sparks.dataflow.connector.mssql.MSSqlProxy")
case class MSSqlProxy(connector: ConnectorDef) extends ConnectorProxy {

  val requiresSelector = true

  var hostName = "localhost"
  var port = 1433
  var userName = ""
  var password = ""
  var timeout = 120
  var properties = ""

  override def editor = MSSqlEditor(this)

  def toProtocol =
    ConnectorProtocol(
      connector.spec.jarDesc,
      MSSqlParameters(hostName, port, userName, password, timeout, properties).asJson.noSpaces
    )

  def name = s"$hostName(${connector.spec.id})"

  def locationallyEquals(p: ConnectorProxy) =
    p match {
      case that: MSSqlProxy => hostName.equalsIgnoreCase(that.hostName) && port == that.port
      case _ => false
    }

}
