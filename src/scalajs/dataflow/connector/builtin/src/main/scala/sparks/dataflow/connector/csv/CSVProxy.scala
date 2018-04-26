package sparks.dataflow.connector.csv

import io.circe.syntax._
import sparks.dataflow.connector.DataSource._
import sparks.dataflow.connector.{ConnectorDef, ConnectorProxy}
import sparks.dataflow.protocols.Connector.ConnectorProtocol
import sparks.dataflow.protocols.csv.CSVParameters

import scala.collection.mutable.ListBuffer
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("sparks.dataflow.connector.csv.CSVProxy")
case class CSVProxy(connector: ConnectorDef) extends ConnectorProxy {

  val requiresSelector = false
  override val canImport = false

  var filename: String = ""
  var isRemote: Boolean = false
  var url: String = ""
  var filepath: String = ""

  var sep : String = ","
  var encoding : String = "UTF-8"
  var quote : String = "\""
  var header : Boolean = true
  var inferSchema : Boolean = true
  var timestampFormat : String = "yyyy-MM-dd"

  override def editor = CSVEditor(this)

  def toProtocol =
    ConnectorProtocol(
      connector.spec.jarDesc,
      CSVParameters(filename, isRemote, url, filepath, sep, encoding, quote, header, inferSchema, timestampFormat).asJson.noSpaces
    )

  def name = s"$filename(${connector.spec.id})"

  def locationallyEquals(p: ConnectorProxy) =
    p match {
      case that: CSVProxy => filepath.equalsIgnoreCase(that.filepath)
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
