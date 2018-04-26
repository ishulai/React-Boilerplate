package mxGraph

import org.scalajs.dom.raw.{HTMLElement, KeyboardEvent}

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("mxKeyHandler")
class MxKeyHandler protected() extends js.Object {
  def this(graph: MxGraph, target: HTMLElement = null) = this()

  var graph: MxGraph = js.native
  var target: HTMLElement = js.native
  var normalKeys: js.Dictionary[KeyboardEvent] = js.native
  var shiftKeys: js.Dictionary[KeyboardEvent] = js.native
  var controlKeys: js.Dictionary[KeyboardEvent] = js.native
  var controlShiftKeys: js.Dictionary[KeyboardEvent] = js.native
  var enabled: Boolean = js.native

  def bindKey(code: Int, funct: js.Function1[KeyboardEvent, Unit]): Unit = js.native
  def bindShiftKey(code: Int, funct: js.Function1[KeyboardEvent, Unit]): Unit = js.native
  def bindControlKey(code: Int, funct: js.Function1[KeyboardEvent, Unit]): Unit = js.native
  def bindControlShiftKey(code: Int, funct: js.Function1[KeyboardEvent, Unit]): Unit = js.native
  def isControlDown(evt: KeyboardEvent): Boolean = js.native
  def getFunction(evt: KeyboardEvent): js.Function1[KeyboardEvent, Unit] = js.native
  def isGraphEvent(evt: KeyboardEvent): Boolean = js.native
  def keyDown(evt: KeyboardEvent): Unit = js.native
  def isEnabledForEvent(evt: KeyboardEvent): Boolean = js.native
  def isEnabledIgnored(evt: KeyboardEvent): Boolean = js.native
  def escape(evt: KeyboardEvent): Unit = js.native
  def destroy(): Unit = js.native
}
