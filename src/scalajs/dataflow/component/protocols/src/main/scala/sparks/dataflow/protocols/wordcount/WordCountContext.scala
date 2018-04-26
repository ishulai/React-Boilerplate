package sparks.dataflow.protocols.wordcount

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import sparks.dataflow.protocols.ContextJsonSupport
import sparks.dataflow.protocols.processmodel.{Activity, RunningContext}


case class WordCountContext(stopWords: String) {


  def run(activity: Activity, rc: RunningContext) = {

    val inbound = activity.getInboundActivity
    if (inbound != null) {


      val df = rc.dataFrames(inbound.id).filter("reviewText <> ''")

      val cleanup_text = udf[Array[String], String] ( s =>
        s.split("""[ '\-\(\)\*":;\[\]|{},.!?\>\<\/\=]+""").map(_.toLowerCase).filter( ! stopWords.contains(_)).filter(_.length >3).filter(_.forall(java.lang.Character.isLetter))
      )

      val rdd = df.withColumn("word",cleanup_text(df("reviewText"))).select("word").rdd

      val rdd2 = rdd.map(_.getAs[scala.collection.mutable.WrappedArray[String]](0)).flatMap(_.clone()).map((_,1)).reduceByKey(_+_)

      val schema = StructType(Seq(
        StructField("word", StringType, nullable = true),
        StructField("count", IntegerType, nullable = true)
      ))

      val result = rc.spark.createDataFrame(rdd2.map(Row.fromTuple(_)), schema).orderBy(desc("count"))
      rc.dataFrames(activity.id) = result

    }
  }
}

object WordCountContext extends ContextJsonSupport {

  implicit val WordCountContextEncoder: Encoder[WordCountContext] = deriveEncoder
  implicit val WordCountContextDecoder: Decoder[WordCountContext] = deriveDecoder

  def fromJson(json: String): WordCountContext = {
    val obj = decode[WordCountContext](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}

