package mxGraph

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("mxGraphModel")
class MxGraphModel protected() extends js.Object {
  def this(root: js.Any) = this()
  def beginUpdate(): Unit = js.native
  def endUpdate(): Unit = js.native
  def cloneCell(cell: MxCell): MxCell = js.native
  def getValue(cell: MxCell): js.Any = js.native
  def setValue(cell: MxCell, value: js.Any): js.Any = js.native
}
