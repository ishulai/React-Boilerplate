package sparks.dataflow.protocols

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import io.circe._
import io.circe.generic.semiauto._
import sparks.dataflow.protocols.Component.JarSpec
import spray.json._

object Connector {

  case class ConnectorProtocol(jar: JarSpec, context: String)

  case class SchemaQuery(cp: ConnectorProtocol, catalog: String = "", schema: String = "", table: String = "")

  trait JsonSupport extends Component.JsonSupport {

    implicit val connectorProtocolEncoder: Encoder[ConnectorProtocol] = deriveEncoder
    implicit val connectorProtocolDecoder: Decoder[ConnectorProtocol] = deriveDecoder

    implicit val schemaQueryEncoder: Encoder[SchemaQuery] = deriveEncoder
  }

  trait AkkaJsonSupport
    extends JsonSupport
      with Component.AkkaJsonSupport
      with SprayJsonSupport
      with DefaultJsonProtocol {

    implicit val connectorProtocolFormat = jsonFormat2(ConnectorProtocol)
    implicit val schemaQueryFormat = jsonFormat4(SchemaQuery)

  }

}