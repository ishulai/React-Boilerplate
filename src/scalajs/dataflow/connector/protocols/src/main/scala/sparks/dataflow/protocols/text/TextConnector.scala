package sparks.dataflow.protocols.text

import java.net.{HttpURLConnection, URL}

import org.apache.spark.sql.SparkSession
import sparks.dataflow.protocols.Auxiliaries.{DbEntity, Field}
import sparks.dataflow.protocols.BaseConnector

class TextConnector extends BaseConnector {

  def load(spark: SparkSession, args: Any*) =
    if (fileInfo.isRemote)
      new Loader().loadRemoteFile(spark, fileInfo.path)
    else
      new Loader().loadLocalFile(fileInfo.path)

  private class Loader extends SparkLoader

  var fileInfo: TextParameters = _
  def init(context: String) = {
    fileInfo = TextParameters.fromJson(context)
    this
  }

  def testConnection(): String = {
    if (fileInfo.isRemote) {
      if (fileInfo.path.nonEmpty) {
        var conn: HttpURLConnection = null
        try {
          conn = new URL(fileInfo.path).openConnection().asInstanceOf[HttpURLConnection]
          conn.setRequestMethod("HEAD")
          conn.setConnectTimeout(20000)
          conn.setReadTimeout(20000)
          conn.getResponseCode match {
            case HttpURLConnection.HTTP_OK => ""
            case _ => conn.getResponseMessage
          }
        }
        catch {
          case e: Exception => e.getMessage
        }
        finally {
          if (conn != null) conn.disconnect()
        }
      }
      else "Not a valid URL."
    }
    else "Not Implemented yet."
  }

  def listColumns(databaseName: String, schema: String, tableName: String): Array[Field] = Array(Field("value", "String"))

  def getCacheKey(catalog: String, table: String) = ???
  def listDatabases(): Array[String] = ???
  def listTables(databaseName: String): Array[DbEntity] = ???
  def previewTable(databaseName: String, tableName: String): String = ???
}
