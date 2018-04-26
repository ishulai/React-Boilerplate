package sparks.dataflow.connector.text

import io.circe.syntax._
import sparks.dataflow.connector.DataSource._
import sparks.dataflow.connector.{ConnectorDef, ConnectorProxy}
import sparks.dataflow.protocols.Connector.ConnectorProtocol
import sparks.dataflow.protocols.text.TextParameters

import scala.collection.mutable.ListBuffer
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("sparks.dataflow.connector.text.TextProxy")
case class TextProxy(connector: ConnectorDef) extends ConnectorProxy {

  val requiresSelector = false
  override val canImport = false

  var isRemote: Boolean = true
  var path: String = ""

  def fileName = path.split("\\/|\\\\").last

  override def editor = TextEditor(this)

  def toProtocol =
    ConnectorProtocol(
      connector.spec.jarDesc,
      TextParameters(isRemote, path).asJson.noSpaces
    )

  def name = "Text files"

  def locationallyEquals(p: ConnectorProxy) =
    p match {
      case that: TextProxy => isRemote == that.isRemote && path.equalsIgnoreCase(that.path)
      case _ => false
    }

  override def getObjectsToLoad =
    DataSource(
      this,
      (ListBuffer.newBuilder[Database] +=
        Database(
          if (isRemote) "Remote" else "Local",
          (ListBuffer.newBuilder[Entity] += Entity("", fileName, fileName)).result()
      )).result()
    )

}
