package mxGraph

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("mxStylesheet")
class MxStylesheet protected() extends js.Object {
  def getDefaultEdgeStyle(): js.Dictionary[js.Any] = js.native
  def putDefaultEdgeStyle(style: js.Dictionary[js.Any]): Unit = js.native
  def getDefaultVertexStyle(): js.Dictionary[js.Any] = js.native
  def putDefaultVertexStyle(style: js.Dictionary[js.Any]): Unit = js.native
  def putCellStyle(name: String, style: js.Any): Unit = js.native
}
