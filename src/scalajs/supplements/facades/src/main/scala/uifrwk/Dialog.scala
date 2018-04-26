package uifrwk

import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("dialog")
class Dialog protected() extends js.Object {
  def this(title: String, content: HTMLElement = null, width: js.Any = null, height: js.Any = null) = this()

  var title: String = js.native
  var contentPane: HTMLElement = js.native
  var canClose: Boolean = js.native
  var onclose: js.Function0[Boolean] = js.native

  def show(content: HTMLElement = null): Dialog = js.native
  def redraw(content: HTMLElement, newWidth: js.Any = null, newHeight: js.Any = null): Unit = js.native
  def adjustPosition(): Unit = js.native
  def resizeToFitContent(): Unit = js.native
  def changeTitle(title: String): Unit = js.native
  def isOpened(): Boolean = js.native
  def close(): Unit = js.native
}

object Dialog {
  def apply(
             title: String,
             content: HTMLElement = null,
             width: js.Any = null,
             height: js.Any = null
           ) = new Dialog(title, content, width, height)
}
