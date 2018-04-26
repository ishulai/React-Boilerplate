package sparks.dataflow.protocols.text

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}

case class TextParameters(isRemote: Boolean, path: String)

object TextParameters {

  implicit val parametersDecoder: Decoder[TextParameters] = deriveDecoder
  implicit val parametersEncoder: Encoder[TextParameters] = deriveEncoder

  def fromJson(json: String): TextParameters = {
    val obj = decode[TextParameters](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}
