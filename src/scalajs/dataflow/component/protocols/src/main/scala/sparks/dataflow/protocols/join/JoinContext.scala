package sparks.dataflow.protocols.join

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import sparks.dataflow.protocols.Auxiliaries.Field
import sparks.dataflow.protocols.ContextJsonSupport
import sparks.dataflow.protocols.join.JoinType.JoinType
import sparks.dataflow.protocols.processmodel.{Activity, RunningContext}


case class JoinContext(
                      joinType: JoinType,
                      keys: Seq[KeyMapping],
                      columns: Seq[Field]
                      ) {

  def run(activity: Activity, rc: RunningContext) = {

    def makeDataFrame(activity: Activity, left: Boolean) = {
      val aliases = columns.filter(f => (left && f.left || !left && !f.left) && f.alias.nonEmpty)
      var df = rc.dataFrames(activity.id)
      if (aliases.nonEmpty) aliases.foreach(f => df = df.withColumnRenamed(f.name, f.alias))
      df
    }

    val left = activity.getInboundActivity(0)
    val right = activity.getInboundActivity(1)

    if (left != null && right != null) {

      val leftDF = makeDataFrame(left, true)
      val rightDF = makeDataFrame(right, false)

      var newDF = if (joinType == JoinType.Cross)
        leftDF.join(rightDF)
      else {

        import JoinOperator._
        val exprs = keys.map(f => f.operator match {
          case EqualTo => leftDF.col(f.leftName) === rightDF.col(f.rightName)
          case LessThan => leftDF.col(f.leftName) < rightDF.col(f.rightName)
          case LessThanOrEqualTo => leftDF.col(f.leftName) <= rightDF.col(f.rightName)
          case GreaterThan => leftDF.col(f.leftName) > rightDF.col(f.rightName)
          case GreaterThanOrEqualTo => leftDF.col(f.leftName) >= rightDF.col(f.rightName)
        }).reduce(_ && _)

        leftDF.join(
          rightDF,
          exprs,
          joinType match {
            case JoinType.Inner => "inner"
            case JoinType.LeftOuter => "left_outer"
            case JoinType.RightOuter => "right_outer"
            case JoinType.Outer => "outer"
          }
        )
      }

      if (columns.exists(!_.selected)) {
        val selected = columns.filter(_.selected)
        if (selected.nonEmpty)
          newDF = newDF.select(selected.map(f => newDF.col(f.name)):_*)
      }

      rc.dataFrames(activity.id) = newDF
    }
    else
      null
  }

}

object JoinContext extends ContextJsonSupport {

  implicit val JoinOperatorEncoder: Encoder[JoinOperator.Value] = Encoder.enumEncoder(JoinOperator)
  implicit val JoinOperatorDecoder: Decoder[JoinOperator.Value] = Decoder.enumDecoder(JoinOperator)
  implicit val JoinTypeEncoder: Encoder[JoinType.Value] = Encoder.enumEncoder(JoinType)
  implicit val JoinTypeDecoder: Decoder[JoinType.Value] = Decoder.enumDecoder(JoinType)
  implicit val KeyMappingEncoder: Encoder[KeyMapping] = deriveEncoder
  implicit val KeyMappingDecoder: Decoder[KeyMapping] = deriveDecoder
  implicit val joinContextEncoder: Encoder[JoinContext] = deriveEncoder
  implicit val joinContextDecoder: Decoder[JoinContext] = deriveDecoder

  def fromJson(json: String): JoinContext = {
    val obj = decode[JoinContext](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}

