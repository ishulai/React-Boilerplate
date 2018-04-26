package ace

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("Editor")
class Editor protected() extends js.Object {

  var session: EditSession = js.native

  def on(event: String, func: js.Function1[Any, _]): Unit = js.native
  def clearSelection(): Unit = js.native
  def setTheme(theme: String): Unit = js.native
  def getValue(): String = js.native
  def setValue(text: String): Unit = js.native
  def moveCursorTo(row: Int, column: Int): Unit = js.native
}
