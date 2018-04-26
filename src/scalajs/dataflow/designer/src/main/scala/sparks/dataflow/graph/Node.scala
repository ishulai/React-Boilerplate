package sparks.dataflow.graph

import mxGraph._
import sparks.dataflow.protocols.Auxiliaries.Schema
import sparks.dataflow.protocols.processmodel.{Activity, ActivityContext, ActivityStatistic}
import sparks.dataflow.prototypes.GraphicalModel.{Graph, Vertex, VertexPort}
import sparks.dataflow.proxy.BaseProxy

import scala.collection.mutable.ListBuffer
import scala.scalajs.js

class Node(
            val id: Int,
            val vertex: MxCell,
            val graph: Graph,
            val proxy: BaseProxy
          ) extends Vertex {

  object overlays {

    private var overlay: MxCellOverlay = _

    def show(icon: MxImage, tooltip: String, onclick: () => Unit = null) = {
      clear()
      overlay = new MxCellOverlay(icon, tooltip, verticalAlign = MxConstants.ALIGN_TOP)
      if (onclick != null) {
        overlay.addListener(
          MxEvent.CLICK,
          (sender: MxEventSource, evt: MxEventObject) => onclick()
        )
      }
      diagram.graph.addCellOverlay(vertex, overlay)
    }

    def showError(tooltip: String, onclick: () => Unit = null) =
      show(new MxImage("/images/icons/error_s2.png", 12, 12), tooltip, onclick)

    def clear() = {
      if (overlay != null) {
        diagram.graph.removeCellOverlay(vertex, overlay)
        overlay = null
      }
    }
  }

  object balloon {

    private var cell: MxCell = _

    def visible: Boolean = if (cell != null) cell.visible else false
    def visible_=(v: Boolean) = if (cell != null) cell.setVisible(v)

    def showDescription() = {
      clear()
      if (description.nonEmpty) create(description, "grey")
    }

    def showStatistic() = {
      clear()
      if (lastStat != null) create(lastStat.label, lastStat.color)
    }

    def clear() = {
      if (cell != null) {
        diagram.graph.removeCells(js.Array(cell))
        cell = null
      }
    }

    private def create(text: String, color: String) = {
      val lines = split(text, 35)
      val v = diagram.graph.insertVertex(
        vertex,
        null,
        lines.mkString("\n"),
        0, -1, 0, 0,
        s"shape=rectangle;rounded=1;fillOpacity=70;strokeColor=none;fillColor=$color;align=left;" +
          s"verticalLabelPosition=middle;fontColor=white;fontStyle=1;movable=0;connectable=false;",
        true
      )
      diagram.graph.updateCellSize(v)
      val size = diagram.getSize(max(lines), diagram.styleManager.fontSize)
      v.geometry.width = size.x + 15
      v.geometry.height = lines.length * size.y
      v.geometry.y = (v.geometry.height / vertex.geometry.height + 0.2) * -1
      cell = v
    }

    private def split(s: String, len: Int) = {
      if (s.contains('\n')) s.split('\n')
      else {
        var curr = ""
        var currLen = 0
        val lines = ListBuffer[String]()
        s.split(' ').foreach(w => {
          currLen += w.length
          curr += w + " "
          if (currLen > len) {
            lines += curr
            curr = ""
            currLen = 0
          }
        })
        if (curr.nonEmpty) lines += curr
        lines.toArray
      }
    }

    private def max(strs: Array[String]) = {
      var c = ""
      strs.foreach(s => if (s.length > c.length) c = s)
      c
    }

  }

  val diagram = graph.asInstanceOf[Diagram]

  private val _inPorts: ListBuffer[VertexPort] = ListBuffer()
  def inPorts: ListBuffer[VertexPort] = _inPorts

  private val _outPorts: ListBuffer[VertexPort] = ListBuffer()
  def outPorts: ListBuffer[VertexPort] = _outPorts

  var schema: Schema = _
  var lastStat: ActivityStatistic = _
  def hasData = lastStat != null && lastStat.view.nonEmpty

  def x: Double = vertex.geometry.x
  def x_=(n: Double): Unit = vertex.geometry.x = n

  def y: Double = vertex.geometry.y
  def y_=(n: Double): Unit = vertex.geometry.y = n

  def width: Double = vertex.geometry.width
  def width_=(n: Double): Unit = vertex.geometry.width = n

  def height: Double = vertex.geometry.height
  def height_=(n: Double): Unit = vertex.geometry.height = n

  def moveTo(x: Double, y: Double): Unit = {
    vertex.geometry.x = x
    vertex.geometry.y = y
  }

  def moveTo(x: Double, y: Double, width: Double, height: Double): Unit = {
    moveTo(x, y)
    vertex.geometry.width = width
    vertex.geometry.height = height
  }

  def addInPort(vertex: MxCell) = addPort(inPorts, vertex)

  def addOutPort(vertex: MxCell) = addPort(outPorts, vertex)

  private def addPort(ports: ListBuffer[VertexPort], vertex: MxCell) = {
    val port = new NodePort(diagram.newPortId, vertex, this)
    ports += port
    port
  }

  // todo: Need to find a better way to rewrite this method
  def findLink(edge: MxCell): Link = {
    import util.control.Breaks._
    var link: Link = null
    breakable {
      for (i <- inPorts.indices) {
        link = inPorts(i).links.castFind[Link](_.edge == edge).orNull
        if (link != null) break
      }
    }

    if (link == null) {
      breakable {
        for (i <- outPorts.indices) {
          link = outPorts(i).links.castFind[Link](_.edge == edge).orNull
          if (link != null) break
        }
      }
    }

    link
  }

  def delete() = {
    inPorts.foreach(_.links.castForeach[Link](_.delete()))
    outPorts.foreach(_.links.castForeach[Link](_.delete()))
  }

  def validate() = {
    val isValid = proxy.validate()
    if (!isValid)
      overlays.showError("Node hasn't been properly configured.", () => proxy.edit())
    else
      overlays.clear()
    isValid
  }

  def toProtocol: Activity = Activity(
    id,
    inPorts.map(_.asInstanceOf[NodePort].toProtocol).toList,
    outPorts.map(_.asInstanceOf[NodePort].toProtocol).toList,
    if (proxy != null) proxy.toProtocol else ActivityContext.empty
  )
}

object Node {

  def apply(id: Int, vertex: MxCell, diagram: Diagram, proxy: BaseProxy) = new Node(id, vertex, diagram, proxy)

}
