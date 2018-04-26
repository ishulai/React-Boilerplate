package mxGraph

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("mxGeometry")
class MxGeometry protected() extends js.Object {
  def this(x: Double, y: Double, width: Double, height: Double) = this()

  var x: Double = js.native
  var y: Double = js.native
  var width: Double = js.native
  var height: Double = js.native
  var offset: MxPoint = js.native
  var relative: Boolean = js.native
}
