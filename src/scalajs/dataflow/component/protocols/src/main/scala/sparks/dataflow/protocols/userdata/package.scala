package sparks.dataflow.protocols

package object userdata {

  object Delimiter extends Enumeration {
    type Delimiter = Value
    val Tab, Semicolon, Comma, Space, Other = Value
  }

}
