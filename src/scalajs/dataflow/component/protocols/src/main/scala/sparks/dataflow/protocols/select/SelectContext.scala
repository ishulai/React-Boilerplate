package sparks.dataflow.protocols.select

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import sparks.dataflow.protocols.Auxiliaries.Field
import sparks.dataflow.protocols.ContextJsonSupport
import sparks.dataflow.protocols.processmodel.{Activity, RunningContext}

case class SelectContext(columns: Seq[Field]) {

  def run(activity: Activity, rc: RunningContext) = {
    val inbound = activity.getInboundActivity
    if (inbound != null) {
      val src = rc.dataFrames(inbound.id)
      rc.dataFrames(activity.id) =
        src.select(columns.map(c => if (c.alias.nonEmpty) src.col(c.name).alias(c.alias) else src.col(c.name)):_*)
    }
  }

}

object SelectContext extends ContextJsonSupport {

  implicit val selectContextEncoder: Encoder[SelectContext] = deriveEncoder
  implicit val selectContextDecoder: Decoder[SelectContext] = deriveDecoder

  def fromJson(json: String): SelectContext = {
    val obj = decode[SelectContext](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}
