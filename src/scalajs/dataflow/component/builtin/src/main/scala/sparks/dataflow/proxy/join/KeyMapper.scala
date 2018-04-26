package sparks.dataflow.proxy.join

import org.scalajs.dom.raw._
import sparks.dataflow.implicits._
import sparks.dataflow.protocols.join.{JoinOperator, KeyMapping}
import sparks.webui.base.DomFunction

case class KeyMapper(proxy: JoinProxy) extends DomFunction {

  private var mappingTable: HTMLTableElement = _
  private val left = proxy.leftSchema
  private val right = proxy.rightSchema

  def collect() = {

    def findField(name: String, left: Boolean) = {
      proxy.node.schema.fields.find(f => {
        (left && f.left || !left && !f.left) && f.name.equalsIgnoreCase(name)
      }).orNull
    }

    mappingTable
      .getRows(1)
      .filter(r => r.cells(0).textContent.nonEmpty || r.cells(2).textContent.nonEmpty)
      .map(r => {
        val left = findField(r.cells(0).textContent, true)
        val right = findField(r.cells(2).textContent, false)
        KeyMapping(left.name, left.alias, right.name, right.alias, JoinOperator.withName(r.cells(1).textContent))
      })
  }

  def isValid: Boolean = {
    val rows = mappingTable
      .getRows(1)
      .filter(r => r.cells(0).textContent.nonEmpty || r.cells(2).textContent.nonEmpty)

    if (rows.nonEmpty) {
      rows.foreach(r => if (r.cells(0).textContent.isEmpty || r.cells(2).textContent.isEmpty) return false)
      true
    }
    else
      false
  }

  def render(): HTMLElement = {
    import Tags._
    div(d => {
      d.style.width = "100%"
      d.style.height = "100%"
      d.style.overflow = "auto"
      d.style.position = "relative"
      mappingTable = table(
        t => {
          t.border = "1"
          t.cellPadding = "4"
          t.cellSpacing = "2"
          t.style.borderCollapse = "collapse"
          t.style.tableLayout = "fixed"
          t.style.border = "solid 1px darkgray"
          t.onclick = (e: MouseEvent) => onTableClick(e)
          t.onmouseover = (e: MouseEvent) => onTableMouseover(e)
          t.onmouseout = (e: MouseEvent) => onTableMouseout(e)
        },
        tr(
          r => r.style.backgroundColor = "#dfdfdf",
          td(c => {
            c.style.width = "220px"
            span("Left")
          }),
          td(c => {
            c.style.width = "40px"
            spacer
          }),
          td(c => {
            c.colSpan = 2
            c.style.width = "220px"
            span("Right")
          })
        )
      )

      proxy.keys.foreach(key => mappingTable.appendChild(row(key)))
      mappingTable.appendChild(row(KeyMapping.empty))

      mappingTable
    })
  }

  private def row(mapping: KeyMapping): HTMLTableRowElement = {
    import Tags._
    tr(
      r => r.className = "key",
      td(if (mapping.isEmpty || mapping.left.isEmpty) "" else mapping.left),
      td(mapping.operator.toString),
      td(c => {
        c.style.borderRight = "none"
        c.textContent = if (mapping.isEmpty || mapping.right.isEmpty) "" else mapping.right
        null
      }),
      td(c => {
        c.style.width = "16px"
        c.style.height = "19px"
        c.style.borderLeft = "none"
        img("/images/icons/delete.png", deleteRow, "none")
      })
    )
  }

  private def isEmptyRow(row: HTMLTableRowElement) =
    row == null || row.cells(0).textContent.isEmpty && row.cells(2).textContent.isEmpty

  private def onTableClick(e: MouseEvent) = {
    val cell = getCell(e)
    if (cell != null && !cell.isInTitleRow) attachSelect(cell)
  }

  private def onTableMouseover(e: MouseEvent) = {
    val cell = getCell(e)
    if (cell != null && !cell.isInTitleRow && !isEmptyRow(cell.row))
      cell.row.lastElementChild.firstElementChild.asInstanceOf[HTMLImageElement].style.display = "inline"
  }

  private def onTableMouseout(e: MouseEvent) = {
    val cell = getCell(e)
    if (cell != null && !cell.isInTitleRow)
      cell.row.lastElementChild.firstElementChild.asInstanceOf[HTMLImageElement].style.display = "none"
  }

  private def getCell(e: MouseEvent) = {
    val c = e.getAncestor[HTMLTableCellElement]("TD")
    if (c == null || c.row.className != "key")
      null
    else
      c
  }

  private def deleteRow(e: MouseEvent) = {
    val cell = getCell(e)
    if (cell != null) cell.row.remove()
  }

  private def attachSelect(cell: HTMLTableCellElement): HTMLSelectElement = {
    val fields = if (cell.cellIndex == 0)
      left.fields.map(f => (f.name, f.name))
    else if (cell.cellIndex == 1)
      Seq(("=", "="), ("<", "<"), ("<=", "<="), (">", ">"), (">=", ">="))
    else
      right.fields.map(f => (f.name, f.name))

    val select = Tags.select(8, fields: _*)

    document.body.appendChild(select)
    select.style.position = "absolute"
    select.style.zIndex = "65535"

    val rect = cell.getBoundingClientRect()
    select.style.width = cell.offsetWidth + "px"
    select.style.left = rect.left + "px"
    select.style.top = (rect.top + cell.offsetHeight) + "px"

    select.onblur = (e: FocusEvent) => document.body.removeChild(select)

    def updateCell() = {
      cell.textContent = select.value
      cell.parentElement.style.color = ""
      cell.parentElement.style.backgroundColor = ""
      select.blur()

      if (!isEmptyRow(cell.lastRow)) {
        mappingTable.appendChild(row(KeyMapping.empty))
        if (proxy.editor.form.resize != null) proxy.editor.form.resize()
      }
    }

    select.onclick = (e: MouseEvent) => updateCell()
    select.onchange = (e: Event) => updateCell()
    select.focus()

    select
  }

}
