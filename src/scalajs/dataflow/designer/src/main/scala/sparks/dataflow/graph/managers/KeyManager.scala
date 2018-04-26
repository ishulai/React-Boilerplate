package sparks.dataflow.graph.managers

import mxGraph.MxKeyHandler
import org.scalajs.dom.KeyboardEvent
import org.scalajs.dom.raw.HTMLElement
import sparks.dataflow.graph.Diagram

class KeyManager(diagram: Diagram, target: HTMLElement = null) extends Manager(diagram) {

  private val keyHandler = new MxKeyHandler(graph, target)

  // Delete
  keyHandler.bindKey(46, (evt: KeyboardEvent) => delete())

  private def delete(): Unit = {
    val cells = graph.getSelectionCells()
    cells.foreach { cell =>
      if (cell.vertex && !diagram.vertexIsPort(cell))
        diagram.removeNode(cell)
      else if (cell.edge)
        diagram.removeLink(cell)
    }
    graph.removeCells(cells, true)
  }
}

object KeyManager {
  def apply(diagram: Diagram, target: HTMLElement = null) = new KeyManager(diagram, target)
}
