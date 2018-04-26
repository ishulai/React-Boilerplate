package sparks.dataflow.protocols.groupby

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import sparks.dataflow.protocols.Auxiliaries.Field
import sparks.dataflow.protocols.ContextJsonSupport
import sparks.dataflow.protocols.processmodel.{Activity, RunningContext}

case class GroupContext(groups: Seq[Field], aggregations: Seq[Field]) {

  def run(activity: Activity, rc: RunningContext) = {

    import org.apache.spark.sql.functions._

    implicit class FieldExtension(f: Field) {
      def toExpr = expr(s"${f.aggfunc}(${f.name}) as ${if (f.alias.nonEmpty) f.alias else f.name}")
    }

    val inbound = activity.getInboundActivity
    if (inbound != null) {

      val df = rc.dataFrames(inbound.id)
      val aggs = aggregations.filter(_.selected).splitAt(1)
      rc.dataFrames(activity.id) =
        df
          .groupBy(groups.filter(_.selected).map(f => df.col(f.name)): _*)
          .agg(aggs._1.head.toExpr, aggs._2.map(_.toExpr): _*)

    }
  }

}

object GroupContext extends ContextJsonSupport {

  implicit val groupContextEncoder: Encoder[GroupContext] = deriveEncoder
  implicit val groupContextDecoder: Decoder[GroupContext] = deriveDecoder

  def fromJson(json: String): GroupContext = {
    val obj = decode[GroupContext](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}
