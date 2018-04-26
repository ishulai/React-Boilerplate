package mxGraph

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("mxShape")
class MxShape protected() extends js.Object {
  def this(stencil: MxStencil) = this()

  var stencil: MxStencil = js.native
  var constraints: js.Array[MxConnectionConstraint] = js.native

}
