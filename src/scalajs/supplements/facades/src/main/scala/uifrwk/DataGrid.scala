package uifrwk

import org.scalajs.dom.raw.{HTMLElement, HTMLInputElement, HTMLTableCellElement, HTMLTableRowElement}

import scala.scalajs.js
import scala.scalajs.js.annotation._

object DataGrid {

  @ScalaJSDefined
  trait GridCheckBoxes extends js.Object {
    val columns: js.UndefOr[Boolean] = js.undefined
    val rows: js.UndefOr[Boolean] = js.undefined
  }

  object GridCheckBoxes {
    def apply(
               columns: Boolean,
               rows: Boolean
             ) = js.Dynamic.literal(columns = columns, rows = rows).asInstanceOf[GridCheckBoxes]
  }

  @ScalaJSDefined
  trait GridEffects extends js.Object {
    val hover: js.UndefOr[String] = js.undefined
    val select: js.UndefOr[String] = js.undefined
  }

  object GridEffects {
    def apply(
             hover: String,
             select: String
             ) = js.Dynamic.literal(hover = hover, select = select).asInstanceOf[GridEffects]
  }

  @js.native
  @JSGlobal("dataGrid")
  class DataGrid protected() extends js.Object {
    def this(container: js.Any) = this()

    var width: js.Any = js.native
    var height: js.Any = js.native
    var checkBoxes: GridCheckBoxes = js.native
    var checkBoxDefault: GridCheckBoxes = js.native
    var customColumns: js.Array[CustomColumn] = js.native
    var border: Boolean = js.native
    var cellPadding: Int = js.native
    var cellSpacing: Int = js.native
    var columns: js.Array[GridColumn] = js.native
    var effects: GridEffects = js.native
    var captionLengthLimit: Int = js.native
    var trimRight: Boolean = js.native
    var hiddenRowsVisible: Boolean = js.native
    var hiddenColumnsVisible: Boolean = js.native
    var columnXPath: String = js.native
    var rowXPath: String = js.native
    var cellXPath: String = js.native
    var delayEditing: Boolean = js.native
    var element: HTMLElement = js.native
    // Events
    var onScroll: js.Function0[_] = js.native
    var onRenderCustomColumn: js.Function2[HTMLTableRowElement, CustomColumn, _] = js.native
    var onRenderColumn: js.Function4[HTMLTableCellElement, Int, Int, String, _] = js.native
    var onRowEnter: js.Function1[HTMLTableRowElement, _] = js.native
    var onRowLeave: js.Function1[HTMLTableRowElement, _] = js.native
    var onRowSelected: js.Function1[HTMLTableRowElement, _] = js.native
    var onRowChecked: js.Function2[HTMLTableRowElement, Boolean, _] = js.native
    var onCellContextMenu: js.Function2[HTMLTableCellElement, HTMLTableRowElement, _] = js.native
    var onInplaceEditing: js.Function3[GridColumn, HTMLTableCellElement, HTMLInputElement, _] = js.native
    var onInplaceEdited: js.Function1[HTMLTableCellElement, _] = js.native

    def addColumn(column: GridColumn): GridColumn = js.native
    def highlightColumn(index: js.Any, highlight: Boolean): Unit = js.native
    def focusColumn(index: js.Any, focus: Boolean): Unit = js.native
    def changeColumnName(index: js.Any, newName: String): Unit = js.native
    def getColumnIndex(index: js.Any): Int = js.native
    def getColumnByName(name: String): GridColumn = js.native
    def getColumnByCell(cell: HTMLTableCellElement): GridColumn = js.native
    def getAllRows(): js.Array[HTMLTableRowElement] = js.native
    def getRowCount(): Int = js.native
    def isEmpty(): Boolean = js.native
    def getCheckedRowCount(): Int = js.native
    def getCheckedRows(): js.Array[HTMLTableRowElement] = js.native
    def getSelectedRows(): js.Array[HTMLTableRowElement] = js.native
    def getSelectedCount(): Int = js.native
    def getCheckedValues(columnName: String): js.Array[String] = js.native
    def hideRow(row: HTMLTableRowElement): Int = js.native
    def unhideRow(row: HTMLTableRowElement): Int = js.native
    def hideColumn(index: js.Any): Int = js.native
    def unhideColumn(index: js.Any): Int = js.native
    def appendRow(data: js.Array[String] = null): Unit = js.native
    def deleteRow(row: HTMLTableRowElement): Int = js.native
    def clearRows(): Unit = js.native
    def deleteColumn(index: js.Any): Int = js.native
    def getCheckedColumns(): js.Array[GridColumn] = js.native
    def checkColumns(names: js.Array[String]): Unit = js.native
    def checkRowHeader(checked: Boolean): Unit = js.native
    def setEditable(colIndex: Int, width: js.Any = null): Unit = js.native
    def setHiddenRowsVisibility(visible: Boolean): Unit = js.native
    def setHiddenColumnsVisibility(visible: Boolean): Unit = js.native
    def initSchema(dataSet: js.Any): Int = js.native
    def render(dataSet: js.Any): Unit = js.native
    def updateData(dataSet: js.Any): Unit = js.native
    @JSName("_sync")
    def sync(): Unit = js.native
    @JSName("_resize")
    def resize(): Unit = js.native

  }

  object DataGrid {
    def apply(container: js.Any) = new DataGrid(container)
  }

  @js.native
  @JSGlobal("gridColumn")
  class GridColumn protected() extends js.Object {
    var name: String = js.native
    var checkbox: Boolean = js.native
    var width: js.Any = js.native
    var markedForHidden: Boolean = js.native
    var isHidden: Boolean = js.native
    var isEditable: Boolean = js.native

    def chcek(value: Boolean): Unit = js.native
    def isChecked: Boolean = js.native
  }

  object GridColumn {
    def apply() = new GridColumn()
  }


  @js.native
  @JSGlobal("customColumn")
  class CustomColumn protected() extends js.Object {
    def this(name: String, width: js.Any, titleVisible: Boolean = true) = this()

    var name: String = js.native
    var width: js.Any = js.native
    var titleVisible: Boolean = js.native
  }

  object CustomColumn {
    def apply(name: String, width: js.Any, titleVisible: Boolean = true) = new CustomColumn(name, width, titleVisible)
  }


}