package mxGraph


import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("mxConnectionHandler")
class MxConnectionHandler protected() extends js.Object {

  var createEdgeState: js.Function1[js.Any, MxCellState] = js.native

  def addListener(name: String, funct: js.Function2[MxEventSource, MxEventObject, Unit]): Unit = js.native

}
