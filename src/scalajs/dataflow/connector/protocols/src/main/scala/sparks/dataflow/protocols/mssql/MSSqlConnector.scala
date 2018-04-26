package sparks.dataflow.protocols.mssql

import java.sql.{Connection, DriverManager, ResultSet, ResultSetMetaData}

import org.apache.spark.sql.{DataFrame, SparkSession}
import sparks.dataflow.connector.DatabaseConnector.DatabaseConnector
import sparks.dataflow.protocols.Auxiliaries.{DbEntity, EntityType, Field, Schema}

import scala.collection.mutable.ListBuffer

class MSSqlConnector extends DatabaseConnector {

  implicit class FieldExtension(f: Field) {
    def toSQL =
      if (f.alias.isEmpty && f.newType.isEmpty)
        s"[${f.name}]"
      else if (f.alias.nonEmpty)
        s"[${f.name}] as [${f.alias}]"
      else
        s"[${f.name}]" // todo: Temp
  }

  def load(spark: SparkSession, args: Any*) =
    if (args.lengthCompare(4) != 0) throw new Exception("Invalid loader arguments.")
    else
      new Loader().load(
        spark,
        args(0).toString,
        args(1).toString,
        args(2).toString,
        args(3).asInstanceOf[Schema]
      )

  private class Loader extends SparkLoader {
    override def load(spark: SparkSession, databaseName: String, schema: String, tableName: String, columns: Schema): DataFrame = {

      val table = s"[$schema].[$tableName]"
      val src = if (columns.allSelected)
        table
      else
        StringBuilder.newBuilder
          .append("(select ")
          .append(columns.selectedFields.map(_.toSQL).mkString(","))
          .append(s" from $table) as query")
          .toString()

      spark.read.format("jdbc")
        .option("url", s"jdbc:sqlserver://${serverInfo.hostName}")
        .option("databaseName", databaseName)
        .option("dbtable", src)
        .option("username", serverInfo.userName)
        .option("password", serverInfo.password)
        .load()
    }
  }

  var serverInfo: MSSqlParameters = _
  def init(context: String) = {
    serverInfo = MSSqlParameters.fromJson(context)
    this
  }

  def getCacheKey(catalog: String, table: String) = s"${serverInfo.hostName}.${serverInfo.userName}.$catalog.$table"

  def connectionUrl = {
    val db = if (serverInfo.databaseName.nonEmpty) s"databaseName=${serverInfo.databaseName}" else ""
    s"jdbc:sqlserver://${serverInfo.hostName}:${serverInfo.port};user=${serverInfo.userName};password=${serverInfo.password};$db"
  }

  def getConnection: Connection = {
    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
    DriverManager.getConnection(connectionUrl)
  }

  def listDatabases(): Array[String] = {
    var cn: Connection = null
    var rs: ResultSet = null
    val databases = new ListBuffer[String]()
    try {
      cn = getConnection
      rs = cn.getMetaData.getCatalogs
      while(rs.next()) {
        databases += rs.getString("TABLE_CAT")
      }
      databases.toArray
    }
    finally {
      closeResultSet(cn, rs)
    }
  }

  def listTables(databaseName: String): Array[DbEntity] = {
    var cn: Connection = null
    var rs: ResultSet = null
    val tables = new ListBuffer[DbEntity]()
    try {
      cn = getConnection
      rs = cn.getMetaData.getTables(databaseName, null, null, Array("TABLE"))
      while(rs.next()) {
        val schema = rs.getString("TABLE_SCHEM")
        val name = rs.getString("TABLE_NAME")
        tables += DbEntity(
          databaseName,
          schema,
          name,
          "[%s].[%s]".format(schema, name),
          EntityType.parse(rs.getString("TABLE_TYPE"))
        )
      }
      tables.toArray
    }
    finally {
      closeResultSet(cn, rs)
    }
  }

  // todo: Temp implementation
  private val typeMap = Map(
    "int" -> "Int",
    "smallint" -> "Int",
    "int identity" -> "Int",
    "nchar" -> "String",
    "nvarchar" -> "String",
    "bit" -> "Boolean",
    "varbinary" -> "Binary",
    "datetime" -> "Date",
    "datetime2" -> "Date",
    "float" -> "Float",
    "money" -> "BigDecimal",
    "decimal" -> "BigDecimal"
  )

  def listColumns(databaseName: String, schema: String, tableName: String): Array[Field] = {
    var cn: Connection = null
    var rs: ResultSet = null
    val columns = new ListBuffer[Field]()
    try {
      cn = getConnection
      rs = cn.getMetaData.getColumns(databaseName, schema, tableName, null)
      while(rs.next()) {
        val typeName = rs.getString("TYPE_NAME")
        columns += Field(
          name = rs.getString("COLUMN_NAME"),
          dataType = if (typeMap.contains(typeName)) typeMap(typeName) else typeName,
          nullable = rs.getInt("NULLABLE") == ResultSetMetaData.columnNullable
        )
      }
      columns.toArray
    }
    finally {
      closeResultSet(cn, rs)
    }
  }

  def previewTable(databaseName: String, tableName: String): String = {
    serverInfo = serverInfo.changeDatabase(databaseName)
    var cn: Connection = null
    var rs: ResultSet = null
    try {
      cn = getConnection
      val stmt = cn.createStatement()
      rs = stmt.executeQuery(getPreviewTableSql(tableName))
      serializePreview(rs)
    }
    finally {
      closeResultSet(cn, rs)
    }
  }

  def getPreviewTableSql(tableName: String): String = "select top 20 * from %s".format(tableName)
}