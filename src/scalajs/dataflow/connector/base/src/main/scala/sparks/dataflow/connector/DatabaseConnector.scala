package sparks.dataflow.connector

import java.sql.{Connection, ResultSet, SQLException}

import sparks.dataflow.protocols.BaseConnector

object DatabaseConnector {

  case class ServerInfo(var host: String, var port: Int, var databaseName: String, var userName: String, var password: String) {

    def isEmpty = this.equals(ServerInfo.empty)

    override def equals(obj: scala.Any) = {
      obj match {
        case s: ServerInfo =>
          host == s.host &&
            port == s.port &&
            databaseName == s.databaseName &&
            userName == s.userName &&
            password == s.password
        case _ => false
      }
    }
  }

  object ServerInfo {
    def empty: ServerInfo = ServerInfo("", 0, "", "", "")
  }

  trait DatabaseConnector extends BaseConnector {

    def connectionUrl: String
    def getConnection: Connection

    def testConnection(): String = {
      var cn: Connection = null
      try {
        cn = getConnection
        ""
      }
      catch {
        case e: SQLException => e.getMessage
      }
      finally {
        closeConnection(cn)
      }
    }

    def getPreviewTableSql(tableName: String): String

    protected def serializePreview(rs: ResultSet): String = {

      val sb = StringBuilder.newBuilder
      sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
      sb.append("<preview><columns>")

      val md = rs.getMetaData
      val cc = md.getColumnCount
      for (i <- 1 to cc) {
        // todo: Need to escape columnName
        sb.append("<column>%s</column>".format(md.getColumnName(i)))
      }

      sb.append("</columns><data>")

      while (rs.next()) {
        sb.append("<row>")
        for (i <- 1 to cc) {
          val cv = rs.getObject(i)
          sb.append("<cell><![CDATA[%s]]></cell>".format(if (rs.wasNull) "null" else cv.toString))
        }
        sb.append("</row>")
      }
      sb.append("</data></preview>")
      sb.toString
    }

    protected def closeConnection(cn: Connection) = if (cn != null && !cn.isClosed) cn.close()
    protected def closeResultSet(cn: Connection, rs: ResultSet) = {
      if (rs != null && !rs.isClosed) rs.close()
      closeConnection(cn)
    }

  }

}
