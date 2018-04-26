package sparks.dataflow.protocols.mssql

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}


case class MSSqlParameters(
                            hostName: String,
                            port: Int,
                            userName: String,
                            password: String,
                            timeout: Int,
                            properties: String,
                            databaseName: String = ""
                          ) {

  def changeDatabase(name: String) = MSSqlParameters(hostName, port, userName, password, timeout, properties, name)

}

object MSSqlParameters {

  implicit val parametersDecoder: Decoder[MSSqlParameters] = deriveDecoder
  implicit val parametersEncoder: Encoder[MSSqlParameters] = deriveEncoder

  def fromJson(json: String): MSSqlParameters = {
    val obj = decode[MSSqlParameters](json)
    if (obj.isRight) obj.right.get else throw obj.left.get
  }

}