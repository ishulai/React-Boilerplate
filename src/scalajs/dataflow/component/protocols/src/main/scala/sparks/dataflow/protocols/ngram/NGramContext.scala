package sparks.dataflow.protocols.ngram

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import org.apache.spark.ml.feature.NGram
import sparks.dataflow.protocols.Auxiliaries.Field
import sparks.dataflow.protocols.ContextJsonSupport
import sparks.dataflow.protocols.processmodel.{Activity, RunningContext}

case class NGramContext(n: Int, columns: Seq[Field]) {

  def run(activity: Activity, rc: RunningContext) = {

    val inbound = activity.getInboundActivity
    if (inbound != null) {

      val ngram = new NGram().setN(n)
      var df = rc.dataFrames(inbound.id)

      columns
        .filter(_.selected)
        .foreach(f =>
          df = ngram
            .setInputCol(f.name)
            .setOutputCol(if (f.alias.nonEmpty) f.alias else f.name + "_ngram")
            .transform(df)
        )

      rc.dataFrames(activity.id) = df

    }
  }
}

object NGramContext extends ContextJsonSupport {

  implicit val ngramContextEncoder: Encoder[NGramContext] = deriveEncoder
  implicit val ngramContextDecoder: Decoder[NGramContext] = deriveDecoder

  def fromJson(json: String): NGramContext = {
    val obj = decode[NGramContext](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}