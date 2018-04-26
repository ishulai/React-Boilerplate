package uifrwk

import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("preview")
class Preview protected() extends js.Object {
  def this(container: js.Any, width: js.Any, height: js.Any) = this()

  var width: js.Any = js.native
  var height: js.Any = js.native
  var element: HTMLElement = js.native

  def render(data: js.Any): Unit = js.native
}

object Preview {
  def apply(container: js.Any, width: js.Any = null, height: js.Any = null) = new Preview(container, width, height)
}
