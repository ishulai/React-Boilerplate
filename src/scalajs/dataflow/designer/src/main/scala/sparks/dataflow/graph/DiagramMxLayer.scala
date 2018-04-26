package sparks.dataflow.graph

import mxGraph._
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js

class DiagramMxLayer(container: HTMLElement) {

  MxConstants.VERTEX_SELECTION_COLOR = "green"

  val model: MxGraphModel = new MxGraphModel()
  val graph: MxGraph = new MxGraph(container, model)

  protected val rubberband: MxRubberband = new MxRubberband(graph)
  protected val defaultParent: js.Any = graph.getDefaultParent()

  graph.setConnectable(true)
  graph.setMultigraph(false)
  graph.setAllowDanglingEdges(false)

  graph.cellsResizable = false
  graph.dropEnabled = true
  js.Dynamic.global.mxDragSource.prototype.getDropTarget = (g: MxGraph, x: Double, y: Double) => {
    var cell = g.getCellAt(x, y)
    if (!g.isValidDropTarget(cell))
      cell = null
    cell
  }

  def vertexIsPort(vertex: MxCell): Boolean = {
    val geo = graph.getCellGeometry(vertex)
    geo != null && geo.relative
  }

  protected def beginUpdate(): Unit = model.beginUpdate()
  protected def endUpdate(): Unit = model.endUpdate()

  def addListener(name: String, funct: js.Function2[MxEventSource, MxEventObject, Unit]) = graph.addListener(name, funct)
}
