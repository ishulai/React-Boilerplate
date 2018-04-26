package sparks.dataflow.protocols.topic

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import org.apache.spark.ml.clustering.LDA
import org.apache.spark.ml.feature.{CountVectorizer, CountVectorizerModel}
import org.apache.spark.sql.functions._
import sparks.dataflow.protocols.ContextJsonSupport
import sparks.dataflow.protocols.processmodel.{Activity, RunningContext}


case class TopicContext(stopWords: String, numberOfTopics : Int) {


  def run(activity: Activity, rc: RunningContext) = {

    val inbound = activity.getInboundActivity
    if (inbound != null) {


      val df = rc.dataFrames(inbound.id).filter("reviewText <> ''")

      val cleanup_text = udf[Array[String], String] ( s =>
        s.split("""[ '\-\(\)\*":;\[\]|{},.!?\>\<\/\=]+""").map(_.toLowerCase).filter( ! stopWords.contains(_)).filter(_.length >3).filter(_.forall(java.lang.Character.isLetter))
      )

      val clean_df = df.filter("reviewText<>''").withColumn("words",cleanup_text(df("reviewText"))).select("words")


      //4. Generate a word count vector
      val cvmodel: CountVectorizerModel = new CountVectorizer()
        .setInputCol("words")
        .setOutputCol("rawFeatures")
        .setVocabSize(1000)
        .fit(clean_df)


      val featurizedData = cvmodel.transform(clean_df)

      val vocab = cvmodel.vocabulary
      val vocab_broadcast = rc.spark.sparkContext.broadcast(vocab)


      //4. Generate a TF-IDF (Term Frequency Inverse Document Frequency) Matrix
      import org.apache.spark.ml.feature.IDF
      val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
      val idfModel = idf.fit(featurizedData)
      val rescaledData = idfModel.transform(featurizedData)
      rescaledData.select("rawFeatures", "features").show()

      //5. Using LDA for Topic Modeling
      //val lda = new LDA().setK(25).setSeed(123).setFeaturesCol("features").setOptimizer("em")
      val lda = new LDA().setK(numberOfTopics).setSeed(123).setMaxIter(20).setFeaturesCol("features").setOptimizer("em")
      val ldamodel = lda.fit(rescaledData)
      val transformed = ldamodel.transform(rescaledData)

      val desc = udf[String, scala.collection.mutable.WrappedArray[Int]] (v => v.map(i=>vocab(i)).mkString(",") )

      val ldatopics = ldamodel.describeTopics(25).withColumn("terms", desc(col("termIndices"))).select("topic", "terms")

      rc.dataFrames(activity.id) = ldatopics

    }
  }
}



object TopicContext extends ContextJsonSupport {

  implicit val TopicContextEncoder: Encoder[TopicContext] = deriveEncoder
  implicit val TopicContextDecoder: Decoder[TopicContext] = deriveDecoder

  def fromJson(json: String): TopicContext = {
    val obj = decode[TopicContext](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}

