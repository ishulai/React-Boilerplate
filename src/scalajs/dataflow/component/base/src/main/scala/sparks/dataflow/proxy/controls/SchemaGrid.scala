package sparks.dataflow.proxy.controls

import org.scalajs.dom.raw.{HTMLElement, HTMLInputElement, HTMLTableCellElement, HTMLTableRowElement}
import sparks.dataflow.protocols.Auxiliaries.{Field, Schema}
import sparks.webui.base.DomFunction
import sparks.webui.entity.EditorControl
import uifrwk.DataGrid.{DataGrid, GridCheckBoxes}

import scala.collection.mutable.ListBuffer
import scala.scalajs.js

object SchemaGrid {

  case class GridSwitches(
                           name: Boolean = true,
                           dataType: Boolean = true,
                           alias: Boolean = true,
                           newType: Boolean = true,
                           nullable: Boolean = true,
                           side: Boolean = true,
                           selected: Boolean = true,
                           aggfunc: Boolean = true,
                           userfunc: Boolean = true
                         ) {

    implicit class BooleanExtension(b: Boolean) {
      def toInt = if (b) 1 else -1
    }

    def toIndices = {
      val switches = Array(
        side.toInt, name.toInt, alias.toInt, userfunc.toInt,
        dataType.toInt, newType.toInt, nullable.toInt, aggfunc.toInt)

      var count = 0
      for (i <- switches.indices) {
        if (switches(i) == 1) {
          switches(i) = count
          count += 1
        }
      }

      ColumnIndices(
        switches(0), switches(1), switches(2), switches(3),
        switches(4), switches(5), switches(6), switches(7)
      )
    }

    def renderSchema(sb: StringBuilder, names: Array[String]) = {

      def render(name: String) = sb.append("<column name=\"%s\" />".format(name))

      if (side) render(names(0))
      if (name) render(names(1))
      if (alias) render(names(2))
      if (userfunc) render(names(3))
      if (dataType) render(names(4))
      if (newType) render(names(5))
      if (nullable) render(names(6))
      if (aggfunc) render(names(7))
    }

  }

  case class ColumnIndices(
                            side: Int, name: Int, alias: Int, userfunc: Int,
                            dataType: Int, newType: Int, nullable: Int, aggfunc: Int
                          )

  class SchemaGrid(val schema: Schema, val visibility: GridSwitches, val editable: GridSwitches = null)
    extends DomFunction
      with EditorControl {

    private val indices = visibility.toIndices
    private var grid: DataGrid = _

    var onRowChecked: (HTMLTableRowElement, Boolean) => _ = _

    val defaultCaptions = Array("Side", "Name", "Alias", "Function", "Data Type", "New Type", "Nullable", "Aggregation")

    private val editableWidth = "140px"

    def complete() = if (grid != null) {
      grid.sync()
      grid.resize()
    }

    def isValid = grid != null && grid.getCheckedRowCount() > 0

    def isEmpty = grid == null || grid.isEmpty

    def nonEmpty = !isEmpty

    def collect(selectedOnly: Boolean = false) = {
      // Index gap caused by checkbox
      val gap = if (visibility.selected) 1 else 0
      val rows = if (selectedOnly) grid.getCheckedRows() else grid.getAllRows()
      Schema(
        rows
          .map(r => Field(
            if (visibility.name) r.cells(indices.name + gap).textContent else "",
            if (visibility.dataType) r.cells(indices.dataType + gap).textContent else "",
            if (visibility.alias) r.cells(indices.alias + gap).textContent else "",
            if (visibility.newType) r.cells(indices.newType + gap).textContent else "",
            if (visibility.nullable) r.cells(indices.nullable + gap).getAttribute("orival") == "1" else true,
            if (visibility.side) r.cells(indices.side + gap).textContent == "Left" else true,
            if (visibility.selected) r.cells(0).children(0).asInstanceOf[HTMLInputElement].checked else true,
            if (visibility.aggfunc) r.cells(indices.aggfunc + gap).textContent else "",
            if (visibility.userfunc) r.cells(indices.userfunc + gap).textContent else ""
          ))
      )
    }

    import js.JSConverters._

    def appendRow(data: Seq[String] = null) = if (grid != null) grid.appendRow(if (data != null) data.toJSArray else null)

    def checkedCount = if (grid != null) grid.getCheckedRowCount() else 0

    def deleteCheckedRows() = if (grid != null) grid.getCheckedRows().foreach(grid.deleteRow(_))

    def clear() = if (grid != null) grid.clearRows()

    def render(): HTMLElement = render(null)

    def render(container: HTMLElement): HTMLElement = {

      val cntr = if (container == null) {
        val d = Tags.div
        d.style.width = "100%"
        d.style.height = "100%"
        d.style.overflow = "auto"
        d.style.position = "relative"
        d
      }
      else
        container

      grid = DataGrid(cntr)
      grid.checkBoxes = GridCheckBoxes(columns = false, rows = visibility.selected)
      grid.delayEditing = true

      grid.onRenderColumn = (cell: HTMLTableCellElement, rowIndex: Int, colIndex: Int, value: String) => {
        if (colIndex == indices.nullable) {
          cell.setAttribute("orival", value)
          cell.appendChild(Tags.img("/images/icons/%s.png".format(if (value == "0") "delete" else "check")))
        }
        else
          cell.textContent = value
      }

      grid.onRowChecked = (row: HTMLTableRowElement, checked: Boolean) =>
        if (onRowChecked != null) onRowChecked(row, checked)

      val ds = serialize()
      grid.initSchema(ds)

      if (editable == null) {
        if (visibility.alias) grid.setEditable(indices.alias, editableWidth)
        if (visibility.userfunc) grid.setEditable(indices.userfunc, editableWidth)
      }
      else {
        if (editable.name && visibility.name) grid.setEditable(indices.name, editableWidth)
        if (editable.dataType && visibility.dataType) grid.setEditable(indices.dataType)
        if (editable.alias && visibility.alias) grid.setEditable(indices.alias, editableWidth)
        if (editable.newType && visibility.newType) grid.setEditable(indices.newType)
        if (editable.aggfunc && visibility.aggfunc) grid.setEditable(indices.aggfunc)
        if (editable.userfunc && visibility.userfunc) grid.setEditable(indices.alias, editableWidth)
      }

      grid.render(ds)

      if (schema != null && visibility.selected && schema.allSelected) grid.checkRowHeader(true)

      cntr
    }

    private def serialize() = {

      implicit class FieldExtension(f: Field) {
        def renderRow(sb: StringBuilder) = {
          val values = ListBuffer[String]()
          if (visibility.side) values += (if (f.left) "Left" else "Right")
          if (visibility.name) values += f.name
          if (visibility.alias) values += f.alias
          if (visibility.userfunc) values += f.userfunc
          if (visibility.dataType) values += f.dataType
          if (visibility.newType) values += f.newType
          if (visibility.nullable) values += (if (f.nullable) "1" else "0")
          if (visibility.aggfunc) values += f.aggfunc

          if (visibility.selected)
            sb.append("<row checked=\"%d\">".format(if (f.selected) 1 else 0))
          else
            sb.append("<row>")

          values.foreach(v => sb.append("<cell><![CDATA[%s]]></cell>".format(v)))

          sb.append("</row>")
        }
      }

      val sb = StringBuilder.newBuilder
      sb.append("<dataset><schema>")
      visibility.renderSchema(sb, defaultCaptions)
      sb.append("</schema><data>")
      if (schema != null) schema.fields.foreach(_.renderRow(sb))
      js.Dynamic.global.global.parseXml(sb.append("</data></dataset>").toString)
    }

  }

}