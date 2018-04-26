package sparks.dataflow.protocols.processmodel

import org.apache.spark.sql.expressions.UserDefinedFunction
import sparks.dataflow.implicits._
import sparks.dataflow.protocols.processmodel.CodeType._

case class Code(name: String, body: String, `type`: CodeType) {

  def register(rc: RunningContext) = `type` match {
    case Function => registerUDF(rc)
    case Aggregation => registerUDAF(rc)
  }

  private def registerUDF(rc: RunningContext) = {
    rc.spark.udf.register(
      name,
      rc.eval[UserDefinedFunction](
        StringBuilder
          .newBuilder
          .appendLine("import org.apache.spark.sql.functions._")
          .appendLine("import org.apache.spark.sql.expressions.UserDefinedFunction")
          .appendLine(body)
          .appendLine(s"udf($name)")
          .toString
      )
    )
  }

  private def registerUDAF(rc: RunningContext) = {}

}
