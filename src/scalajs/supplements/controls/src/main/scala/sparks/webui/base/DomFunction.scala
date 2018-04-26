package sparks.webui.base

import org.scalajs.dom
import org.scalajs.dom.raw._

import scala.scalajs.js

trait DomFunction {

  protected case class Point(x: Double, y: Double)

  lazy val document: HTMLDocument = dom.document
  lazy val body: HTMLBodyElement = dom.document.body.asInstanceOf[HTMLBodyElement]

  def createElement[T](tagName: String): T = document.createElement(tagName).asInstanceOf[T]

  def getElement(elem: Any): HTMLElement = elem match {
    case s: String => dom.document.getElementById(s).asInstanceOf[HTMLElement]
    case e: HTMLElement => e
    case _ => null
  }

  def getRadioButtonValue(name: String): String = {
    val radios = document.getElementsByName(name)
    for (i <- 0 until radios.length) {
      val radio = radios.item(i).asInstanceOf[HTMLInputElement]
      if (radio.checked) return radio.value
    }
    ""
  }

  def setRadioButtonValue(name: String, value: String) = {
    val radios = document.getElementsByName(name)
    for (i <- 0 until radios.length) {
      val radio = radios.item(i).asInstanceOf[HTMLInputElement]
      radio.checked = radio.value.equalsIgnoreCase(value)
    }
  }

  def isRadioButton(name: String): Boolean = document.getElementsByName(name).isTypeOf(name, "radio")

  def isCheckbox(name: String): Boolean = document.getElementsByName(name).isTypeOf(name, "checkbox")

  def ?(id: String): js.Dynamic = js.Dynamic.global.document.getElementById(id)

  def alert(msg: String): Unit = dom.window.alert(msg)

  def parseXml(xml: String) = js.Dynamic.global.global.parseXml(xml)

  def createContainer(parent: HTMLElement = null, id: String = null): HTMLDivElement = {
    val c = Tags.div
    if (id != null) c.id = id
    c.style.overflow = "hidden"
    c.style.width = "100%"
    c.style.height = "100%"
    if (parent != null) parent.appendChild(c)
    c
  }

  def any(that: Any): js.Any = that.asInstanceOf[js.Any]

  def newInstance[T >: Null](className: String)(args: js.Any*): T = {
    val ctor = className.split("\\.").foldLeft(js.Dynamic.global) { (a, b) => a.selectDynamic(b) }
    if (ctor != null)
      js.Dynamic.newInstance(ctor)(args: _*).asInstanceOf[T]
    else
      null
  }

  private var sizer: HTMLSpanElement = _

  def getSize(s: String, fontSize: Int, bold: Boolean = true): Point = {
    if (sizer == null) {
      sizer = Tags.span
      sizer.style.visibility = "hidden"
      sizer.style.fontSize = s"${fontSize}px"
      sizer.style.fontWeight = if (bold) "bold" else ""
      document.body.appendChild(sizer)
    }
    sizer.textContent = s
    Point(sizer.offsetWidth, sizer.offsetHeight)
  }

  object Tags {
    // Table
    def table: HTMLTableElement = createElement[HTMLTableElement]("table")

    def table(numOfRow: Int, numOfCell: Int, div: Boolean = false): HTMLTableElement = {
      val tbl = table
      for (i <- 0 until numOfRow) {
        val row = tr
        for (j <- 0 until numOfCell) {
          val cell = td
          if (div) cell.appendChild(Tags.div)
          row.appendChild(cell)
        }
        tbl.appendChild(row)
      }
      tbl
    }

    def table(rows: HTMLTableRowElement*): HTMLTableElement = {
      val t = table
      var maxcols = 1
      rows.foreach { r => {
        t.appendChild(r)
        val cc = r.cells.length
        if (cc > maxcols) maxcols = cc
      }
      }

      if (maxcols > 1)
        rows.foreach { r => {
          if (r.cells.length == 1) r.cells(0).asInstanceOf[HTMLTableCellElement].colSpan = maxcols
        }
        }
      t
    }

    def table(f: (HTMLTableElement) => Unit, rows: HTMLTableRowElement*): HTMLTableElement = {
      val t = Tags.table(rows: _*)
      f(t)
      t
    }

    def tr: HTMLTableRowElement = createElement[HTMLTableRowElement]("tr")

    def tr(numOfCell: Int): HTMLTableRowElement = {
      val row = tr
      for (i <- 0 until numOfCell) row.appendChild(td)
      row
    }

    def tr(f: HTMLTableRowElement => Unit, cells: HTMLTableCellElement*): HTMLTableRowElement = {
      val row = tr
      f(row)
      cells.foreach(row.appendChild(_))
      row
    }

    def tr(cells: HTMLTableCellElement*): HTMLTableRowElement = {
      val row = tr
      cells.foreach(row.appendChild(_))
      row
    }

    def singleCellRow = tr(1)

    def spacerRow: HTMLTableRowElement = spacerRow(2)

    def spacerRow(height: Int): HTMLTableRowElement = {
      val row = singleCellRow
      for (i <- 0 until height) row.cells(0).appendChild(br)
      row
    }

    def td: HTMLTableCellElement = createElement[HTMLTableCellElement]("td")

    def td(content: HTMLElement): HTMLTableCellElement = {
      val cell = td
      cell.appendChild(content)
      cell
    }

    def td(textContent: String): HTMLTableCellElement = {
      val cell = td
      cell.textContent = textContent
      cell
    }

    def td(f: HTMLTableCellElement => Any): HTMLTableCellElement = {
      val cell = td
      val child = f(cell)
      if (child != null && child.isInstanceOf[HTMLElement]) cell.appendChild(child.asInstanceOf[HTMLElement])
      cell
    }

    def td(f: () => HTMLElement): HTMLTableCellElement = {
      val cell = td
      cell.appendChild(f())
      cell
    }

    // Inputs
    def input = createElement[HTMLInputElement]("input")

    def password = {
      val e = input
      e.`type` = "password"
      e
    }

    def textArea = createElement[HTMLTextAreaElement]("textarea")

    def button(caption: String, onclick: MouseEvent => Unit = null): HTMLButtonElement = {
      val e = createElement[HTMLButtonElement]("input")
      e.`type` = "button"
      e.value = caption
      e.className = "sysButton"
      if (onclick != null) e.onclick = onclick
      e
    }

    def upload(name: String): HTMLInputElement = {
      val e = input
      e.name = name
      e.`type` = "file"
      e
    }

    def radioButton(
                     caption: String,
                     value: String,
                     name: String = "",
                     checked: Boolean = false
                   ): HTMLTableElement = inputWithCaption("radio", caption, value, name, checked)

    def checkbox(
                  caption: String,
                  name: String = "",
                  checked: Boolean = false
                ): HTMLTableElement = inputWithCaption("checkbox", caption, name, checked = checked)

    def inputWithCaption(
                          `type`: String,
                          caption: String,
                          value: String,
                          name: String = "",
                          checked: Boolean = false
                        ): HTMLTableElement = {

      var input: HTMLInputElement = null
      table(
        t => {
          t.cellSpacing = "0"
          t.cellPadding = "0"
        },
        tr(
          td(d => {
            input = createElement[HTMLInputElement]("input")
            input.`type` = `type`
            input.name = name
            input.value = value
            input.checked = checked
            input
          }),
          td(d => {
            d.style.width = "100%"
            span(caption, (e: MouseEvent) => if (input != null) input.checked = true)
          })
        )
      )

    }

    // Span
    def span: HTMLSpanElement = createElement[HTMLSpanElement]("span")

    def span(text: String, onclick: MouseEvent => _ = null, nowrap: Boolean = true): HTMLSpanElement = {
      val s = span
      s.textContent = text
      if (nowrap) s.style.whiteSpace = "nowrap"
      if (onclick != null) s.onclick = onclick
      s
    }

    def label(text: String, breakable: Boolean = true): HTMLElement = span(text, nowrap = !breakable)

    def label(icon: String, text: String) = table(tr(td(img(icon)), td(span(text))))

    def multiline(text: String): HTMLDivElement =
      div(d=>{
        text
          .split(System.lineSeparator)
          .foreach(line => d.appendChild(div(line)))
        null
      })

    def spacer: HTMLSpanElement = spacer(1)

    def spacer(num: Int): HTMLSpanElement = {
      val s = span
      s.innerHTML = "&nbsp;" * num
      s
    }

    // Div
    def div: HTMLDivElement = createElement[HTMLDivElement]("div")

    def div(text: String): HTMLDivElement = {
      val d = div
      d.textContent = text
      d
    }

    def div(f: HTMLDivElement => HTMLElement): HTMLDivElement = {
      val d = div
      val c = f(d)
      if (c != null) d.appendChild(c)
      d
    }

    def br = createElement[HTMLBRElement]("br")

    def img = createElement[HTMLImageElement]("img")

    def img(src: String, onclick: MouseEvent => Unit = null, display: String = "inline", cursor: String = null): HTMLImageElement = {
      val elem = img
      elem.src = src
      if (onclick != null) elem.onclick = onclick
      elem.style.display = display
      if (cursor != null) elem.style.cursor = cursor
      elem
    }

    // UL
    def ul = createElement[HTMLUListElement]("ul")

    def ul(items: Seq[String]): HTMLUListElement = {
      val list = ul
      items.foreach { v => list.appendChild(li(v)) }
      list
    }

    def li(text: String) = {
      val item = createElement[HTMLLIElement]("li")
      item.textContent = text
      item
    }

    // Select
    def select: HTMLSelectElement = createElement[HTMLSelectElement]("select")

    def select(sizeLimit: Int, options: Enumeration): HTMLSelectElement = {
      val sel = select
      val values = options.values.toSeq
      val length = values.length
      if (sizeLimit > 0) sel.size = if (length < sizeLimit) length else sizeLimit
      values.foreach(v => sel.appendChild(option(v.toString, v.toString)))
      sel
    }

    def select(sizeLimit: Int, options: (String, String)*): HTMLSelectElement = {
      val sel = select
      if (sizeLimit > 0) sel.size = if (options.length < sizeLimit) options.length else sizeLimit
      options.foreach(o => sel.appendChild(option(o._2, o._1)))
      sel
    }

    def option: HTMLOptionElement = createElement[HTMLOptionElement]("option")

    def option(value: String, text: String = ""): HTMLOptionElement = {
      val opt = option
      opt.textContent = if (text.isEmpty) value else text
      opt.value = value
      opt
    }

    /*
    def select(sizeLimit: Int, options: String*): HTMLSelectElement = {
      val sel = select
      if (sizeLimit > 0) sel.size = if (options.length < sizeLimit) options.length else sizeLimit
      options.foreach(o => sel.appendChild(option(o)))
      sel
    }
    */

    def style(css: String): HTMLStyleElement = {
      val s = createElement[HTMLStyleElement]("style")
      s.textContent = css
      s
    }
  }

  class TableAccessor(table: HTMLTableElement) {

    def element: HTMLTableElement = table

    def row(index: Int): HTMLTableRowElement = {
      if (index < table.rows.length)
        table.rows(index).asInstanceOf[HTMLTableRowElement]
      else
        null
    }

    // Access cells of the first row
    def cell(index: Int): HTMLTableCellElement = {
      if (table.rows.length > 0) {
        val r = row(0)
        if (r != null && index < r.cells.length) r.cells(index).asInstanceOf[HTMLTableCellElement] else null
      }
      else null
    }

    def cell(rowIndex: Int, index: Int): HTMLTableCellElement = {
      if (rowIndex < table.rows.length) {
        val r = row(rowIndex)
        if (r != null && index < r.cells.length) r.cells(index).asInstanceOf[HTMLTableCellElement] else null
      }
      else null
    }

    def content[T >: Null](rowIndex: Int, index: Int): T = {
      val c = cell(rowIndex, index)
      if (c != null && c.children.length > 0)
        c.firstChild.asInstanceOf[T]
      else
        null
    }

    def appendChild(rowIndex: Int, colIndex: Int, elem: HTMLElement): Unit = {
      val c = cell(rowIndex, colIndex)
      if (c != null) c.appendChild(elem)
    }

    def appendChild(cellIndex: Int, elem: HTMLElement): Unit = appendChild(0, cellIndex, elem)
  }

  object TableAccessor {
    def apply(table: HTMLTableElement) = new TableAccessor(table)
  }

  implicit class HTMLTableExtension(t: HTMLTableElement) {
    def getRows(start: Int): Seq[HTMLTableRowElement] = {
      val rows = t.rows
      for (i <- start until rows.length) yield rows(i).asInstanceOf[HTMLTableRowElement]
    }
  }

  implicit class HTMLTableRowExtension(r: HTMLTableRowElement) {
    def remove() = r.parentElement.removeChild(r)

    def isEmpty: Boolean = {
      for (i <- 0 until r.cells.length) if (r.cells(i).textContent.nonEmpty) return false
      true
    }

    def nonEmpty = !isEmpty
  }

  implicit class HTMLTableCellExtension(c: HTMLTableCellElement) {
    def table = row.parentElement.asInstanceOf[HTMLTableElement]

    def row = c.parentElement.asInstanceOf[HTMLTableRowElement]

    def rowIndex = row.rowIndex

    def nextRow = row.nextElementSibling.asInstanceOf[HTMLTableRowElement]

    def lastRow = table.lastElementChild.asInstanceOf[HTMLTableRowElement]

    def isInTitleRow = rowIndex == 0
  }

  implicit class MouseEventExtension(e: MouseEvent) {
    def getAncestor[T](tagName: String): T = {
      var src = e.srcElement.asInstanceOf[HTMLElement]
      while (src.tagName != tagName) src = src.parentElement
      src.asInstanceOf[T]
    }
  }

  implicit class NodeListExtension(l: NodeList) {
    def isTypeOf(name: String, `type`: String): Boolean = {
      for (i <- 0 until l.length) {
        l(i) match {
          case input: HTMLInputElement if input.`type`.toLowerCase == `type` => return true
          case _ => return false
        }
      }
      false
    }
  }

}