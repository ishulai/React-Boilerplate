package sparks.dataflow.connector.json

import io.circe.syntax._
import sparks.dataflow.connector.DataSource._
import sparks.dataflow.connector.{ConnectorDef, ConnectorProxy}
import sparks.dataflow.protocols.Connector.ConnectorProtocol
import sparks.dataflow.protocols.json.JSONParameters

import scala.collection.mutable.ListBuffer
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("sparks.dataflow.connector.json.JSONProxy")
case class JSONProxy(connector: ConnectorDef) extends ConnectorProxy {

  val requiresSelector = false
  override val canImport = false

  var filename: String = ""
  var isRemote: Boolean = false
  var url: String = ""
  var filepath: String = ""

  override def editor = JSONEditor(this)

  def toProtocol =
    ConnectorProtocol(
      connector.spec.jarDesc,
      JSONParameters(filename, isRemote, url, filepath).asJson.noSpaces
    )

  def name = s"$filename(${connector.spec.id})"

  def locationallyEquals(p: ConnectorProxy) =
    p match {
      case that: JSONProxy => filepath.equalsIgnoreCase(that.filepath)
      case _ => false
    }

  override def getObjectsToLoad =
    DataSource(
      this,
      (ListBuffer.newBuilder[Database] +=
        Database(
          if (isRemote) "Remote" else "Local",
          (ListBuffer.newBuilder[Entity] += Entity("", filename, filename)).result()
      )).result()
    )

}
