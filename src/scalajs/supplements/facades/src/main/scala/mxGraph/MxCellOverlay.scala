package mxGraph

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("mxCellOverlay")
class MxCellOverlay protected() extends js.Object {
  def this(
            image: MxImage,
            tooltip: String = "",
            align: String = MxConstants.ALIGN_RIGHT,
            verticalAlign: String = MxConstants.ALIGN_BOTTOM,
            offset: MxPoint = null,
            cursor: String = null
          ) = this()

  var image: MxImage = js.native
  var tooltip: String = js.native
  var align: String = js.native
  var verticalAlign: String = js.native
  var offset: MxPoint = js.native
  var cursor: String = js.native
  var defaultOverlap: Double = js.native

  def addListener(name: String, funct: js.Function2[MxEventSource, MxEventObject, Unit]): Unit = js.native
  def getBounds(state: MxCellState): MxRectangle = js.native
}
