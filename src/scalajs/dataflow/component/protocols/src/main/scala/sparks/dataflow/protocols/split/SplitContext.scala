package sparks.dataflow.protocols.split

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import org.apache.spark.ml.feature.{RegexTokenizer, Tokenizer}
import sparks.dataflow.protocols.ContextJsonSupport
import sparks.dataflow.protocols.Auxiliaries.Field
import sparks.dataflow.protocols.processmodel.{Activity, RunningContext}
import sparks.dataflow.protocols.split.SplitBy._


case class SplitContext(
                       splitBy: SplitBy,
                       regex: String,
                       delimiters: String,
                       columns: Seq[Field]
                       ) {

  def run(activity: Activity, rc: RunningContext) = {

    import rc.spark.implicits._

    val inbound = activity.getInboundActivity
    if (inbound != null) {

      var df = rc.dataFrames(inbound.id)

      splitBy match {
        case Default =>
          val tokenizer = new Tokenizer()
          columns
            .filter(_.selected)
            .foreach(f =>
              df = tokenizer
                .setInputCol(f.name)
                .setOutputCol(if (f.alias.nonEmpty) f.alias else f.name + "_Split")
                .transform(df)
            )

        case RegEx =>
          val tokenizer = new RegexTokenizer()
          columns
            .filter(_.selected)
            .foreach(f =>
              df = tokenizer
                .setInputCol(f.name)
                .setOutputCol(if (f.alias.nonEmpty) f.alias else f.name + "_Split")
                .setPattern(regex)
                .transform(df)
            )
      }

      rc.dataFrames(activity.id) = df

    }

    /*
    val defaultLogic = """([\p{Punct}])\s*"""
    val inbound = activity.getInboundActivity
    if (inbound != null) {
      val splitter: String=>Seq[String] = (str: String) => {
        val s = splitBy match {
          case Default => str.replace(defaultLogic, "\\s")
          case RegEx => str.replace(regex, "\\s")
          case Delimiters => str.replace(defaultLogic, "\\s") // todo: Temp
        }
        s.split("\\s").map(_.trim)
      }

      val splitterUDF = udf(splitter)

      var df = rc.dataFrames(inbound.id)
      columns
        .filter(_.selected)
        .foreach(f => {
          df = df.withColumn(
            if (f.alias.nonEmpty) f.alias else f.name + "_Split",
            splitterUDF(df.col(f.name))
          )
        })

      rc.dataFrames(activity.id) = df
    }*/

  }
}

object SplitContext extends ContextJsonSupport {

  implicit val splitByEncoder: Encoder[SplitBy.Value] = Encoder.enumEncoder(SplitBy)
  implicit val splitByDecoder: Decoder[SplitBy.Value] = Decoder.enumDecoder(SplitBy)
  implicit val splitContextEncoder: Encoder[SplitContext] = deriveEncoder
  implicit val splitContextDecoder: Decoder[SplitContext] = deriveDecoder

  def fromJson(json: String): SplitContext = {
    val obj = decode[SplitContext](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}
