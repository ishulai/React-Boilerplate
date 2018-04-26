package mxGraph

import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("mxToolbar")
class MxToolbar protected() extends js.Object {
  def this(container: HTMLElement) = this()

  var enabled: Boolean = js.native

  def addMode(
               title: String,
               icon: String,
               funct: js.Function5[MxGraph, MxMouseEvent, MxCell, Double, Double, Unit],
               pressedIcon: String = "",
               style: String = "",
               toggle: Boolean = true
             ): HTMLElement = js.native

  def addLine(): Unit = js.native

}
