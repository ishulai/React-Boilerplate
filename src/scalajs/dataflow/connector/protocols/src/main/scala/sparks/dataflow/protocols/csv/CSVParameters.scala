package sparks.dataflow.protocols.csv

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}



case class CSVParameters(filename: String, isRemote: Boolean, url: String, filepath: String ,sep : String,
                         encoding : String, quote : String, header : Boolean,inferSchema : Boolean, timestampFormat : String) {}


object CSVParameters {

  implicit val parametersDecoder: Decoder[CSVParameters] = deriveDecoder
  implicit val parametersEncoder: Encoder[CSVParameters] = deriveEncoder

  def fromJson(json: String): CSVParameters = {
    val obj = decode[CSVParameters](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}