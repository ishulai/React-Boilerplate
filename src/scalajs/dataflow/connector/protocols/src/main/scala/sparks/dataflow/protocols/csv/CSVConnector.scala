package sparks.dataflow.protocols.csv

import java.net.{HttpURLConnection, URL}

import org.apache.spark.sql.{DataFrame, SparkSession}
import sparks.dataflow.protocols.Auxiliaries.{DbEntity, Field}
import sparks.dataflow.protocols.BaseConnector

import scala.collection.mutable.ListBuffer
import scala.io._
import scala.util.parsing.json._

class CSVConnector extends BaseConnector {

  def load(spark: SparkSession, args: Any*) =
    if (fileInfo.isRemote)
      new Loader().loadRemoteFile(spark, fileInfo.url)
    else
      new Loader().loadLocalFile(fileInfo.filepath)

  private class Loader extends SparkLoader {
    override def loadLocalFile(filepath: String): DataFrame = {
      val spark: SparkSession = SparkSession.builder.master("local").getOrCreate
      spark
        .read
        .option("header", fileInfo.header.toString)
        .option("inferSchema", fileInfo.inferSchema.toString)
        .option("timestampFormat", fileInfo.timestampFormat)
        .csv(filepath)
    }
    override def loadRemoteFile(spark: SparkSession, filepath: String): DataFrame = {
      spark
        .read
        .option("header", fileInfo.header.toString)
        .option("inferSchema", fileInfo.inferSchema.toString)
        .option("timestampFormat", fileInfo.timestampFormat)
        .csv(filepath)
    }
  }

  var fileInfo: CSVParameters = _
  def init(context: String) = {
    fileInfo = CSVParameters.fromJson(context)
    this
  }

  def testConnection(): String = {
    if (fileInfo.isRemote) {
      if (fileInfo.url.nonEmpty) {
        val scheme = new java.net.URI(fileInfo.url).getScheme().toLowerCase
        if(Array("s3a", "s3n", "hdfs").contains(scheme)) {
          var conn: HttpURLConnection = null
          try {
            conn = new URL(fileInfo.url).openConnection().asInstanceOf[HttpURLConnection]
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
        } else {
          "Only s3a and s3n protocols are supported."
        }
      }
      else "Not a valid URL."
    }
    else {
      if (fileInfo.filepath.nonEmpty) {
        try {
          val source = Source.fromFile(fileInfo.filepath)
          val lines = try source.mkString finally source.close()
          val result = JSON.parseFull(lines)

          result match {
            case Some(e) => ""
            case None => ""
          }
        }
        catch {
          case e: Exception => e.getMessage
        }
      }
      else "Something went wrong with the file upload. Please try again."
    }
  }


  // todo: Temp implementation
  private val typeMap = Map(
    "ByteType" -> "Byte",
    "ShortType" -> "Short",
    "IntegerType" -> "Int",
    "LongType" -> "Long",
    "FloatType" -> "Float",
    "DoubleType" -> "Double",
    "DecimalType" -> "BigDecimal",
    "StringType" -> "String",
    "BinaryType" -> "Array[Byte]",
    "BooleanType" -> "Boolean",
    "TimestampType" -> "Timestamp",
    "DateType" -> "Date",
    "ArrayType" -> "Seq",
    "MapType" -> "Map",
    "StructType" -> "Row",
    "StructField" -> "String" // Should be value of given field
  )

  def listColumns(databaseName: String, schema: String, tableName: String): Array[Field] = {
    val arr = new ListBuffer[Field]()
    var file = if (fileInfo.isRemote) fileInfo.url else fileInfo.filepath
    try {
      val spark: SparkSession = SparkSession.builder.master("local").getOrCreate
      val df = spark
        .read
        .option("",1)
        .option("header", fileInfo.header.toString)
        .option("inferSchema", fileInfo.inferSchema.toString)
        .option("timestampFormat", fileInfo.timestampFormat)
        .csv(fileInfo.filepath)
      val cols = df.schema.fields.toSeq
      for (c <- cols) arr += Field(
        name = c.name,
        dataType = if (typeMap.contains(c.dataType.typeName)) typeMap(c.dataType.typeName) else c.dataType.typeName
      )
    }
    catch {
      case e: Exception => Array(Field("value", "String"))
    }
    arr.toArray
  }

  def getCacheKey(catalog: String, table: String) = ???
  def listDatabases(): Array[String] = Array[String](fileInfo.filename)
  def listTables(databaseName: String): Array[DbEntity] = ???
  def previewTable(databaseName: String, tableName: String): String = ???

}
