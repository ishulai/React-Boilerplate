package mxGraph

import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("mxUtils")
object MxUtils extends js.Object {

  def error(message: String, width: Int, close: Boolean = true, icon: String = ""): Unit = js.native
  def makeDraggable(
                     element: HTMLElement,
                     graphF: MxGraph,
                     funct: js.Function5[MxGraph, MxMouseEvent, MxCell, Double, Double, Unit],
                     dragElement: HTMLElement = null,
                     dx: Double = 0,
                     dy: Double = 0,
                     autoscroll: Boolean = true,
                     scalePreview: Boolean = false,
                     highlightDropTargets: Boolean = true
                   ): js.Any = js.native
}
