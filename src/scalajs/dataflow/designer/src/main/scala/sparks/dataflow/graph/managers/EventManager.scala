package sparks.dataflow.graph.managers

import mxGraph._
import sparks.dataflow.graph.Diagram

import scala.scalajs.js
import scala.scalajs.js.annotation._

class EventManager(diagram: Diagram) extends Manager(diagram) {

  @ScalaJSDefined
  private object DefaultMouseListener extends MouseListener {

    private var clicking = false

    override def mouseDown(sender: MxEventSource, evt: MxMouseEvent): Unit = {
      if (evt.evt.button == 0) clicking = true
      if (diagram.closeContextMenu != null) diagram.closeContextMenu()
    }

    override def mouseMove(sender: MxEventSource, evt: MxMouseEvent): Unit = {}

    override def mouseUp(sender: MxEventSource, evt: MxMouseEvent): Unit = try {
      if (evt.evt.button == 0 && clicking) {
        val cell = evt.getCell()
        if (cell != null) {
          if (cell.vertex && diagram.vertexSelected != null)
            diagram.vertexSelected(cell)
          else if (cell.edge && diagram.edgeSelected != null)
            diagram.edgeSelected(cell)
        }
      }
      else if (evt.evt.button == 2 && diagram.openContextMenu != null)
        diagram.openContextMenu(evt.getCell(), evt.getX().toInt, evt.getY().toInt)
    }
    finally {
      clicking = false
    }
  }

  // Default mouse behaviors
  graph.addMouseListener(DefaultMouseListener)

  // New edge
  graph.connectionHandler.addListener(
    MxEvent.CONNECT,
    (sender: MxEventSource, evt: MxEventObject) => {
      val edge = evt.getProperty("cell").asInstanceOf[MxCell]
      val src = diagram.findPort(edge.source)
      val tar = diagram.findPort(edge.target)
      if (src != null && tar != null) {
        diagram.addLink(edge, src, tar)
      }
    }: Unit
  )

  // Change connection
  diagram.addListener(
    MxEvent.CELL_CONNECTED,
    (sender: MxEventSource, evt: MxEventObject) => {
      if (evt.getProperty("previous") != null) {
        val edge = evt.getProperty("edge").asInstanceOf[MxCell]
        val link = diagram.findLink(edge)
        if (link != null) {
          val src = diagram.findPort(edge.source)
          val tar = diagram.findPort(edge.target)
          if (src != null && tar != null) link.reconnect(src, tar)
        }
      }
    }: Unit
  )

  // Validate connection
  private val chpt = js.Dynamic.global.mxConnectionHandler.prototype
  private val originConnect = chpt.connect.asInstanceOf[js.ThisFunction4[MxConnectionHandler, MxCell, MxCell, js.Any, MxCell, Unit]]
  chpt.connect = (source: MxCell, target: MxCell, evt: js.Any, dropTarget: MxCell) => {
    if (source.parent != target.parent && source.geometry.x == 1 && target.geometry.x == 0) {
      val tarPort = diagram.findPort(target)
      if (tarPort != null && tarPort.links.isEmpty) {
        originConnect(graph.connectionHandler, source, target, evt, dropTarget)
      }
    }
  }

}

object EventManager {
  def apply(diagram: Diagram) = new EventManager(diagram)
}