package mxGraph

import scala.scalajs.js
import scala.scalajs.js.annotation._

@ScalaJSDefined
trait MouseListener extends js.Object {
  def mouseDown(sender: MxEventSource, evt: MxMouseEvent): Unit
  def mouseMove(sender: MxEventSource, evt: MxMouseEvent): Unit
  def mouseUp(sender: MxEventSource, evt: MxMouseEvent): Unit
}
