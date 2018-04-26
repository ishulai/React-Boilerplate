package ace

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("EditSession")
class EditSession protected() extends js.Object {

  def setMode(mode: String): Unit = js.native
  def clearBreakpoints(): Unit = js.native
  def setBreakpoint(row: Int, className: String = "ace_breakpoint"): Unit = js.native
}
