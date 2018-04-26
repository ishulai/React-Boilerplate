package mxGraph

import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("mxGraph")
class MxGraph protected() extends js.Object {
  def this(container: HTMLElement, model: MxGraphModel) = this()

  var view: MxGraphView = js.native
  var container: HTMLElement = js.native
  var dropEnabled: Boolean = js.native
  var cellsResizable: Boolean = js.native

  var connectionHandler: MxConnectionHandler = js.native

  def addListener(name: String, funct: js.Function2[MxEventSource, MxEventObject, Unit]): Unit = js.native
  def addMouseListener(listener: MouseListener): Unit = js.native
  def removeMouseListener(listener: MouseListener): Unit = js.native

  def getDefaultParent(): js.Any = js.native
  def getModel(): MxGraphModel = js.native

  def isValidDropTarget(cell: MxCell): Boolean = js.native

  def insertVertex(parent: js.Any, id: String, value: String, x: Double, y: Double, width: Double, height: Double, style: String = "", relative: Boolean = false): MxCell = js.native
  def insertEdge(parent: js.Any, id: String, value: String, source: MxCell, target: MxCell, style: String = ""): MxCell = js.native
  def createEdge(parent: js.Any, id: String, value: String, source:MxCell, target: MxCell, style:String = ""): MxCell = js.native

  def updateCellSize(cell: MxCell, ignoreChildren: Boolean = false): MxCell = js.native

  def setConnectable(connectable: Boolean): Unit = js.native
  def setMultigraph(value: Boolean): Unit = js.native
  def setAllowDanglingEdges(value: Boolean): Unit = js.native
  def stopEditing(value: Boolean): Unit = js.native
  def getPointForEvent(evt: MxMouseEvent, addOffset: Boolean = false): MxPoint = js.native
  def getCellAt(x: Double, y: Double, parent: MxCell = null, vertices: Boolean = true, edges: Boolean = true, ignoreFn: js.Any = null): MxCell = js.native
  def getCellStyle(cell: MxCell): js.Dictionary[String] = js.native
  def getCellGeometry(cell: MxCell): MxGeometry = js.native

  def addCellOverlay(cell: MxCell, overlay: MxCellOverlay): MxCellOverlay = js.native
  def getCellOverlays(cell: MxCell): MxCellOverlay = js.native
  def removeCellOverlay(cell: MxCell, overlay: MxCellOverlay): MxCellOverlay = js.native
  def removeCellOverlays(cell: MxCell): js.Array[MxCellOverlay] = js.native
  def clearCellOverlays(cell: MxCell): Unit = js.native
  def setCellWarning(cell: MxCell, warning: String, img: MxImage = null, isSelect: Boolean = false): MxCellOverlay = js.native

  def getSelectionCell(): MxCell = js.native
  def setSelectionCell(cell: MxCell): Unit = js.native

  def getSelectionCells(): js.Array[MxCell] = js.native
  def setSelectionCells(cells: js.Array[MxCell]): Unit = js.native

  def getStylesheet(): MxStylesheet = js.native
  def setStylesheet(stylesheet: MxStylesheet): Unit = js.native

  def importCells(
                   cells: js.Array[MxCell],
                   dx: Double = 0,
                   dy: Double = 0,
                   target: MxCell = null,
                   evt: js.Any = null,
                   mapping: js.Any = null
                 ): js.Array[MxCell] = js.native
  def removeCells(cells: js.Array[MxCell] = null, includeEdges: Boolean = true): js.Array[MxCell] = js.native
}