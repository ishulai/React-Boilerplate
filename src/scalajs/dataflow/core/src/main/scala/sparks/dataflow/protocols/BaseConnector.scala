package sparks.dataflow.protocols

import org.apache.spark.sql.{DataFrame, SparkSession}
import sparks.dataflow.protocols.Auxiliaries.{DbEntity, Field, Schema}

import scala.io.Source

trait BaseConnector {

  def getCacheKey(catalog: String, table: String): String

  def init(context: String): BaseConnector

  def testConnection(): String
  def listDatabases(): Array[String]
  def listTables(databaseName: String): Array[DbEntity]
  def listColumns(databaseName: String, schema: String, tableName: String): Array[Field]
  def previewTable(databaseName: String, tableName: String): String

  def load(spark: SparkSession, args: Any*): DataFrame

  trait SparkLoader {
    def load(spark: SparkSession, databaseName: String, schema: String, tableName: String, columns: Schema): DataFrame = ???
    def loadRemoteFile(spark: SparkSession, fileName: String): DataFrame = {
      import spark.implicits._
      spark
        .sparkContext
        .parallelize(Source.fromURL(fileName).mkString.split("\n").filter(_.nonEmpty))
        .toDF()
    }
    def loadLocalFile(fileName: String): DataFrame = ???
  }

}
