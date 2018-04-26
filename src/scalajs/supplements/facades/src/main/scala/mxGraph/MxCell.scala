package mxGraph

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("mxCell")
class MxCell protected() extends js.Object {
  def this(value: js.Any, geometry: MxGeometry, style: String) = this()

  var id: String = js.native
  var value: js.Any = js.native
  var geometry: MxGeometry = js.native
  var style: String = js.native
  var parent: MxCell = js.native
  var source: MxCell = js.native
  var target: MxCell = js.native
  var edges: js.Array[MxCell] = js.native
  var vertex: Boolean = js.native
  var edge: Boolean = js.native
  var visible: Boolean = js.native

  def setVertex(vertex: Boolean): Unit = js.native
  def setConnectable(connectable: Boolean): Unit = js.native
  def isVisible: Boolean = js.native
  def setVisible(visible: Boolean): Unit = js.native
}
