package sparks.dataflow.graph

import java.util.UUID

import io.circe.syntax._
import mxGraph.MxCell
import org.scalajs.dom.raw.{HTMLElement, HTMLTableCellElement}
import sparks.dataflow.designer.{CodeEditor, DataFlowDesigner, DesignerAuxiliary}
import sparks.dataflow.protocols.processmodel
import sparks.dataflow.protocols.processmodel._
import sparks.dataflow.prototypes.GraphicalModel.{Graph, Vertex}
import sparks.dataflow.proxy.BaseProxy
import sparks.webui.base.{AjaxClient, DomFunction}
import sparks.webui.forms.EntityDialog
import uifrwk.DataGrid.DataGrid

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.scalajs.js
import scala.scalajs.js.timers.SetTimeoutHandle
import scala.util.control.Breaks._

class Diagram(
               owner: DataFlowDesigner.type,
               val id: UUID,
               container: HTMLElement
             )
  extends DiagramMxLayer(container)
    with Graph
    with AjaxClient
    with DomFunction
    with DesignerAuxiliary
    with processmodel.JsonSupport {

  designer = owner

  private val _nodes: ListBuffer[Vertex] = ListBuffer()

  def nodes: ListBuffer[Vertex] = _nodes

  import managers._

  EventManager(this)
  val styleManager = StyleManager(this)
  val keyManager = KeyManager(this)

  var openContextMenu: (MxCell, Int, Int) => _ = _
  var closeContextMenu: () => _ = _
  var vertexSelected: (MxCell) => _ = _
  var edgeSelected: (MxCell) => _ = _

  object balloons {

    private var _description: Boolean = false
    def showingDescription = _description

    private var _statistics: Boolean = false
    def showingStatistics = _statistics

    def clear() = {
      try {
        _description = false
        _statistics = false
        model.beginUpdate()
        nodes.castForeach[Node](_.balloon.clear())
      }
      finally {
        model.endUpdate()
      }
    }

    def displayDescription() =
      if (nodes.nonEmpty) {
        try {
          _description = true
          _statistics = false
          model.beginUpdate()
          nodes.castForeach[Node](_.balloon.showDescription())
        }
        finally {
          model.endUpdate()
        }
      }


    def displayStatistics() =
      if (nodes.nonEmpty) {
        try {
          _description = false
          _statistics = true
          model.beginUpdate()
          nodes.castForeach[Node](_.balloon.showStatistic())
        }
        finally {
          model.endUpdate()
        }
      }
  }

  private def newNodeId: Int = {
    var id = 0
    nodes.castForeach[Node](n => if (n.id > id) id = n.id)
    id + 1
  }

  def newPortId: Int = {
    var id = 0
    (nodes.flatMap(_.inPorts) ++ nodes.flatMap(_.outPorts)).castForeach[NodePort](p => if (p.id > id) id = p.id)
    id + 1
  }

  def addNode(vertex: MxCell, context: BaseProxy): Node = {
    val node = Node(newNodeId, vertex, this, context)
    if (context != null) context.node = node
    nodes += node
    node
  }

  def addNode(
               context: BaseProxy,
               x: Int,
               y: Int,
               width: Int,
               height: Int,
               style: String = "",
               relative: Boolean = false
             ): Node = {
    addNode(
      graph.insertVertex(defaultParent, "", "", x, y, width, height, style, relative),
      context
    )
  }

  def findNode(vertex: MxCell): Node = nodes.castFind[Node](_.vertex == vertex).orNull

  def findNode(id: Int): Node = nodes.castFind[Node](_.id == id).orNull

  // Need to find a better way to rewrite this method
  def findPort(vertex: MxCell): NodePort = {
    var port: NodePort = null
    breakable {
      for (i <- nodes.indices) {
        val act = nodes(i)
        port = act.inPorts.castFind[NodePort](_.vertex == vertex).orNull
        if (port != null)
          break
        else {
          port = act.outPorts.castFind[NodePort](_.vertex == vertex).orNull
          if (port != null) break
        }
      }
    }
    port
  }

  def findLink(edge: MxCell): Link = {
    nodes.castForeach[Node](n => {
      val link = n.findLink(edge)
      if (link != null) return link
    })
    null
  }

  def addLink(
               edge: MxCell,
               source: NodePort,
               target: NodePort
             ): Link = Link(edge, this).connect(source, target)

  def addLink(
               source: NodePort,
               target: NodePort,
               style: String = ""
             ): Link = addLink(
    graph.insertEdge(defaultParent, "", "", source.vertex, target.vertex, style),
    source,
    target
  )

  def removeNode(node: Node): Unit = if (node != null) {
    node.delete()
    _nodes -= node
  }

  def removeNode(vertex: MxCell): Unit = removeNode(findNode(vertex))

  def removeLink(link: Link): Unit = if (link != null) link.delete()

  def removeLink(edge: MxCell): Unit = removeLink(findLink(edge))

  def selectedNodes: Seq[Node] = {
    val nodes = new ArrayBuffer[Node]()
    val cells = graph.getSelectionCells()
    cells.foreach {
      cell => {
        if (cell.vertex) {
          val n = findNode(cell)
          if (n != null) nodes += n
        }
      }
    }
    nodes
  }

  private var timeoutHandle: SetTimeoutHandle = _

  def showNodeResult(node: Node) = {

    if (node.hasData && (!console.visible || console.userData != node.id)) {

      if (timeoutHandle != null) js.timers.clearTimeout(timeoutHandle)

      timeoutHandle = js.timers.setTimeout(250) {
        console.title = node.vertex.value.toString
        console.display(Tags.label("/images/icons/progress.gif", "Loading..."), userData = node.id)
        queryXml(
          Path("processmodel", "loaddataframe"),
          Messages.GetDataFrame(id, node.lastStat.view, 50).asJson.noSpaces,
          (result: js.Any) => {
            timeoutHandle = null
            console.clear()

            var hasArray = false
            val indices = ListBuffer[Int]()
            val grid = new DataGrid(console.element)
            grid.onRenderColumn = (cell: HTMLTableCellElement, rowIndex: Int, colIndex: Int, value: String) => {
              if (rowIndex == 0)
                if (value.startsWith("WrappedArray(")) {
                  hasArray = true
                  indices += colIndex
                }

              cell.textContent =
                if (hasArray && indices.contains(colIndex))
                  value.substring(13, value.length - 1)
                else
                  value
            }

            grid.initSchema(result)
            grid.render(result)
          },
          e => {
            timeoutHandle = null
            messageBox.showError(e.getMessage)
          }
        )
      }
    }
  }

  def validate(): Boolean = {
    var isValid = true
    nodes.foreach(n => if (!n.validate()) isValid = false)
    isValid
  }

  def runToNode(node: Node) = {
    balloons.clear()
    if (validate()) {
      import messageBox._
      showBusy("Running process...", buttons.cancel)
      query[ExecutionResult](
        Path("processmodel", "execute"),
        Execution(toProtocol, node.id).asJson.noSpaces,
        result => {
          messageBox.close()
          updateStatistics(result)
          balloons.displayStatistics()
        },
        e => showError(e.getMessage)
      )
    }
  }

  def run() =
    if (validate()) {
      import messageBox._
      showBusy("Running process...", buttons.cancel)
      query[ExecutionResult](
        Path("processmodel", "execute"),
        Execution(toProtocol, runAll).asJson.noSpaces,
        result => {
          messageBox.close()
          updateStatistics(result)
          balloons.displayStatistics()
        },
        e => showError(e.getMessage)
      )
    }

  def updateStatistics(result: ExecutionResult) =
    result.activities.foreach(a => {
      val n = findNode(a._1)
      if (n != null) n.lastStat = a._2
    })

  def toggleDescriptions() = if (!balloons.showingDescription) balloons.displayDescription() else balloons.clear()

  def toggleStatistics() = if (!balloons.showingStatistics) balloons.displayStatistics() else balloons.clear()


  def toProtocol: Process =
    Process(
      id,
      nodes.map(_.asInstanceOf[Node].toProtocol).toList,
      designer.library.toProtocol
    )

}
