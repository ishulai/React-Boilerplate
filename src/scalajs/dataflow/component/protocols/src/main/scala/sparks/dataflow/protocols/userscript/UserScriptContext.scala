package sparks.dataflow.protocols.userscript

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import sparks.dataflow.implicits._
import sparks.dataflow.protocols.ContextJsonSupport
import sparks.dataflow.protocols.processmodel.{Activity, RunningContext}
import sparks.dataflow.protocols.userscript.ScriptLanguage._

case class UserScriptContext(language: ScriptLanguage, script: String) {

  def run(activity: Activity, rc: RunningContext) = {

    val sb = StringBuilder
      .newBuilder
      .appendLine("import org.apache.spark.SparkContext")
      .appendLine("import org.apache.spark.SparkConf")
      .appendLine("import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}")
      .appendLine("import org.apache.spark.sql.functions._")
      .appendLine("import sparks.dataflow.protocols.processmodel.RunningContext")
      .appendLine("import sparks.dataflow.protocols.userscript.UserScriptInterface")
      .appendLine("object UserScriptRunner extends UserScriptInterface {")
      .appendLine("def run(rc: RunningContext): Unit = {")

    for (i <- activity.inPorts.indices) {
      val inbound = activity.getInboundActivity(i)
      if (inbound != null) sb.appendLine(s"val input${i + 1} = rc.dataFrames(${inbound.id})")
    }

    sb
      .appendLine("val spark = rc.spark")
      .appendLine("import spark.implicits._")
      .appendLine("var output: DataFrame = null")
      .appendLine(script)
      .appendLine(s"rc.dataFrames(${activity.id}) = output")
      .appendLine("}}")
      .appendLine("UserScriptRunner")

    rc.eval[UserScriptInterface](sb.toString).run(rc)
  }
}

object UserScriptContext extends ContextJsonSupport {

  implicit val scriptLanguageEncoder: Encoder[ScriptLanguage.Value] = Encoder.enumEncoder(ScriptLanguage)
  implicit val scriptLanguageDecoder: Decoder[ScriptLanguage.Value] = Decoder.enumDecoder(ScriptLanguage)
  implicit val userScriptContextEncoder: Encoder[UserScriptContext] = deriveEncoder
  implicit val userScriptContextDecoder: Decoder[UserScriptContext] = deriveDecoder

  def fromJson(json: String): UserScriptContext = {
    val obj = decode[UserScriptContext](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}
