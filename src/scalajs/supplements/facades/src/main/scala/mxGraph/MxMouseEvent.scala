package mxGraph

import scala.scalajs.js
import scala.scalajs.js.annotation._
import org.scalajs.dom.raw.{HTMLElement, MouseEvent}

@js.native
@JSGlobal("mxMouseEvent")
class MxMouseEvent protected() extends js.Object {
  var consumed: Boolean = js.native
  var evt: MouseEvent = js.native
  var graphX: Double = js.native
  var graphY: Double = js.native
  var state: MxCellState = js.native
  var sourceState: MxCellState = js.native
  def getEvent(): MouseEvent = js.native
  def getSource(): HTMLElement = js.native
  def isSource(shape: MxShape): Boolean = js.native
  def getX(): Double = js.native
  def getY(): Double = js.native
  def getGraphX(): Double = js.native
  def getGraphY(): Double = js.native
  def getState(): MxCellState = js.native
  def getCell(): MxCell = js.native
  def isPopupTrigger(): Boolean = js.native
  def isConsumed(): Boolean = js.native
  def consume(preventDefault: Boolean): Boolean = js.native
}
