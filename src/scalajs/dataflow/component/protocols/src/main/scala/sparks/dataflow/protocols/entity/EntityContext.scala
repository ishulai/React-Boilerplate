package sparks.dataflow.protocols.entity

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import org.apache.spark.sql.functions._
import sparks.dataflow.protocols.Auxiliaries.Schema
import sparks.dataflow.protocols.Connector.ConnectorProtocol
import sparks.dataflow.protocols.ContextJsonSupport
import sparks.dataflow.protocols.processmodel.{Activity, CodeType, RunningContext}

case class EntityContext(
                          cp: ConnectorProtocol,
                          databaseName: String,
                          schema: String,
                          entityName: String,
                          columns: Schema
                        ) {

  def run(activity: Activity, rc: RunningContext) = {

    val df = activity.helper.getConnector(cp).load(rc.spark, databaseName, schema, entityName, columns)

    // Register UDFs
    columns
      .selectedFields
      .filter(_.userfunc.nonEmpty)
      .foreach(f =>
        if (rc.codeExists(f.userfunc, CodeType.Function))
          rc.registerCode(f.userfunc, CodeType.Function)
      )

    val exprs = columns.selectedFields.map(c =>
      if (c.userfunc.nonEmpty) {
        val alias = if (c.alias.nonEmpty) c.alias else s"${c.name}_${c.userfunc}"
        expr(s"${c.userfunc}(${c.name}) as $alias")
      }
      else if (c.alias.nonEmpty) df.col(c.name).alias(c.alias)
      else df.col(c.name)
    )


    rc.dataFrames(activity.id) = df.select(exprs:_*)

  }

}

object EntityContext extends ContextJsonSupport {

  implicit val entityContextEncoder: Encoder[EntityContext] = deriveEncoder
  implicit val entityContextDecoder: Decoder[EntityContext] = deriveDecoder

  def fromJson(json: String): EntityContext = {
    val obj = decode[EntityContext](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }
}