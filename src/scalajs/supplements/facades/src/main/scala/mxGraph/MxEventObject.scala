package mxGraph

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("mxEventObject")
class MxEventObject protected() extends js.Object {
  def this(name: String) = this()

  var name: String = js.native
  var properties: js.Dictionary[js.Any] = js.native
  var consumed: Boolean = js.native
  def getName(): String = js.native
  def getProperties(): js.Dictionary[js.Any] = js.native
  def getProperty(key: String): js.Any = js.native
  def isConsumed(): Boolean = js.native
  def consume(): Unit = js.native

}
