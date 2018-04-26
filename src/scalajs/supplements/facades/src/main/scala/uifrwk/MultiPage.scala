package uifrwk

import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("multiPage")
class MultiPage protected() extends js.Object {
  def this(containerId: String, width: js.Any, height: js.Any) = this()

  var pages: js.Array[Page] = js.native
  var element: HTMLElement = js.native

  def addPage(p: Page): Page = js.native
  def switchPage(p: js.Any): Unit = js.native
  def getActivePage(): Page = js.native
  def render(): Unit = js.native

}


@js.native
@JSGlobal("multiPage")
class Page protected() extends js.Object {
  def this(id: String, caption: String, isActive: Boolean, initFunc: js.Function1[HTMLElement, HTMLElement], color: String) = this

  var id: String = js.native
  var caption: String = js.native
  var isActive: Boolean = js.native
  var initFunc: js.Function1[HTMLElement, HTMLElement] = js.native
  var initialized: Boolean = js.native
  var color: String = js.native
  var content: js.Any = js.native
  var onSwitch: js.Function0[Unit] = js.native

  def setActive(active: Boolean): Unit = js.native
}
