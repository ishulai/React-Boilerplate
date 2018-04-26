package sparks.dataflow.protocols.userdata

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import sparks.dataflow.protocols.Auxiliaries.Field
import sparks.dataflow.protocols.ContextJsonSupport
import sparks.dataflow.protocols.processmodel.{Activity, RunningContext}

case class UserDataContext(userData: Array[Array[String]], columns: Seq[Field]) {

  def run(activity: Activity, rc: RunningContext) = {


    import rc.spark.implicits._
    val df = rc.spark.sparkContext.parallelize(userData).toDF()
    var finalDF = df.select((for (i <- userData(0).indices) yield df("value")(i).as(columns(i).name)): _*)

    // todo: datatype
    columns
      .foreach(f => {
        val tmp = s"${f.name}_casttmp"
        finalDF = finalDF
          .withColumn(tmp, finalDF(f.name).cast(f.dataType))
          .drop(f.name)
          .withColumnRenamed(tmp, f.name)
      })

    rc.dataFrames(activity.id) = finalDF

  }
}

object UserDataContext extends ContextJsonSupport {

  implicit val delimiterEncoder: Encoder[Delimiter.Value] = Encoder.enumEncoder(Delimiter)
  implicit val delimiterDecoder: Decoder[Delimiter.Value] = Decoder.enumDecoder(Delimiter)
  implicit val userDataContextEncoder: Encoder[UserDataContext] = deriveEncoder
  implicit val userDataContextDecoder: Decoder[UserDataContext] = deriveDecoder

  def fromJson(json: String): UserDataContext = {
    val obj = decode[UserDataContext](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}