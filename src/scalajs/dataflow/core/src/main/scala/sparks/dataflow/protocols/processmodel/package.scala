package sparks.dataflow.protocols

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

package object processmodel {

  val runAll = -1

  object ActivityState extends Enumeration {
    type ActivityState = Value
    val Idle, Running, Complete, Failed = Value
  }

  object Messages {

    case class GetDataFrame(processId: UUID, viewName: String, numOfRows: Int)

  }

  object CodeType extends Enumeration {
    type CodeType = Value
    val Function, Aggregation = Value
  }

  trait JsonSupport extends Component.JsonSupport {

    import Messages._

    implicit val getDataFrameEncoder: Encoder[GetDataFrame] = deriveEncoder

    implicit val activityStateEncoder: Encoder[ActivityState.Value] = Encoder.enumEncoder(ActivityState)
    implicit val activityStateDecoder: Decoder[ActivityState.Value] = Decoder.enumDecoder(ActivityState)
    implicit val processEncoder: Encoder[Process] = deriveEncoder
    implicit val activityEncoder: Encoder[Activity] = deriveEncoder
    implicit val activityContextEncoder: Encoder[ActivityContext] = deriveEncoder
    implicit val activityPortEncoder: Encoder[ActivityPort] = deriveEncoder
    implicit val transitionEncoder: Encoder[Transition] = deriveEncoder
    implicit val codeEncoder: Encoder[Code] = deriveEncoder
    implicit val codeTypeEncoder: Encoder[CodeType.Value] = Encoder.enumEncoder(CodeType)
    implicit val executionEncoder: Encoder[Execution] = deriveEncoder
    implicit val activityStatisticEncoder: Encoder[ActivityStatistic] = deriveEncoder
    implicit val activityStatisticDecoder: Decoder[ActivityStatistic] = deriveDecoder
    implicit val executionResultEncoder: Encoder[ExecutionResult] = deriveEncoder
    implicit val executionResultDecoder: Decoder[ExecutionResult] = deriveDecoder
  }

  // This demostrates how to solve circular reference issue
  trait AkkaJsonSupport
    extends JsonSupport
      with Component.AkkaJsonSupport
      with SprayJsonSupport
      with DefaultJsonProtocol {

    implicit object UUIDJsonFormat extends RootJsonFormat[UUID] {
      def write(uuid: UUID) = JsString(uuid.toString)
      def read(value: JsValue) = value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _ => throw DeserializationException("Not a valid UUID.")
      }
    }

    implicit val activityStateFormat = enumFormat(ActivityState)
    implicit val processFormat: RootJsonFormat[Process] = rootFormat(lazyFormat(jsonFormat(Process, "id", "activities", "codes")))
    implicit val activityFormat: RootJsonFormat[Activity] = rootFormat(lazyFormat(jsonFormat(Activity, "id", "inPorts", "outPorts", "context")))
    implicit val activityContextFormat: RootJsonFormat[ActivityContext] = rootFormat(lazyFormat(jsonFormat2(ActivityContext.apply)))
    implicit val activityPortFormat: RootJsonFormat[ActivityPort] = rootFormat(lazyFormat(jsonFormat3(ActivityPort)))
    implicit val transitionFormat: RootJsonFormat[Transition] = rootFormat(lazyFormat(jsonFormat2(Transition)))
    implicit val codeFormat: RootJsonFormat[Code] = rootFormat(lazyFormat(jsonFormat3(Code)))
    implicit val codeTypeFormat = enumFormat(CodeType)
    implicit val executionFormat: RootJsonFormat[Execution] = rootFormat(lazyFormat(jsonFormat2(Execution)))
    implicit val activityStatisticFormat: RootJsonFormat[ActivityStatistic] = rootFormat(lazyFormat(jsonFormat7(ActivityStatistic.apply)))
    implicit val executionResultFormat: RootJsonFormat[ExecutionResult] = rootFormat(lazyFormat(jsonFormat1(ExecutionResult)))

    import Messages._

    implicit val getDataFrameFormat: RootJsonFormat[GetDataFrame] = rootFormat(lazyFormat(jsonFormat3(GetDataFrame)))

  }

}
