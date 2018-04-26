package sparks.dataflow.protocols.json

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}


case class JSONParameters(filename: String, isRemote: Boolean, url: String, filepath: String) {}

object JSONParameters {

  implicit val parametersDecoder: Decoder[JSONParameters] = deriveDecoder
  implicit val parametersEncoder: Encoder[JSONParameters] = deriveEncoder

  def fromJson(json: String): JSONParameters = {
    val obj = decode[JSONParameters](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}