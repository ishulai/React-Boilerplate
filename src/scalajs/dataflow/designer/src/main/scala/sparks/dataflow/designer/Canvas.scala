package sparks.dataflow.designer

import java.util.UUID

import mxGraph.MxCell
import org.scalajs.dom.raw.HTMLElement
import sparks.dataflow.graph.{Diagram, Node}
import sparks.dataflow.proxy.MenuFactory
import uifrwk.PopupMenu._

class Canvas(
              owner: DataFlowDesigner.type,
              private val container: HTMLElement,
              background: String
            ) extends DesignerAuxiliary {

  designer = owner

  if (container == null) throw new IllegalArgumentException("Not a valid container")
  container.style.background = background

  // todo: Temp
  val diagram: Diagram = new Diagram(owner, UUID.randomUUID(), container)

  diagram.openContextMenu = (cell: MxCell, x: Int, y: Int) => {
    val nodes = diagram.selectedNodes
    if (nodes.nonEmpty) openCellMenu(nodes, x, y) else openCanvasMenu(x, y)
  }

  diagram.vertexSelected = (cell: MxCell) => {
    if (console.visible) {
      val node = diagram.findNode(cell)
      if (node != null) diagram.showNodeResult(node)
    }
  }

  private def openCellMenu(nodes: Seq[Node], x: Int, y: Int) = MenuFactory.Items.join(nodes.map(_.proxy)).show(x, y)

  private def openCanvasMenu(x: Int, y: Int): PopupMenu = null

  MenuFactory.copy = (_: MenuItem) => alert("Copy")
  MenuFactory.cut = (_: MenuItem) => {}
  MenuFactory.paste = (_: MenuItem) => {}
  MenuFactory.delete = (_: MenuItem) => {}
}
