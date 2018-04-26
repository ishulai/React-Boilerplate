package sparks.dataflow.designer

import org.scalajs.dom.raw.{HTMLElement, HTMLTableCellElement, MouseEvent}
import uifrwk.Layout._

class Console(
               owner: DataFlowDesigner.type,
               private val container: HTMLElement
             ) extends DesignerAuxiliary {

  designer = owner

  var userData: Any = _

  private var titleCell: HTMLTableCellElement = _
  def title: String = if (titleCell != null) titleCell.textContent else ""
  def title_=(s: String) = if (titleCell != null) titleCell.textContent = s

  private var _contentPane: Partition = _
  def element: HTMLElement = if (_contentPane != null) _contentPane.element else null

  def display(textContent: String) = _contentPane.element.textContent = textContent
  def display(content: HTMLElement) = {
    _contentPane.element.textContent = ""
    _contentPane.element.appendChild(content)
  }


  import Tags._

  Layout(
    container,
    isVertical = true,
    (layout: Layout) => {
      layout.cellPadding = 0
      layout.cellSpacing = 0
    },
    Partition(
      size = "24px",
      postOp = (p: Partition) => {
        p.element.appendChild(
          table(
            t => {
              t.cellPadding = "0"
              t.cellSpacing = "0"
              t.style.width = "100%"
              t.style.borderCollapse = "collapse"
            },
            tr(
              r => r.style.backgroundColor = "#eeeeee",
              td(c => titleCell = c),
              td(c => {
                c.style.width = "16px"
                img(
                  "/images/icons/x_small.png",
                  onclick = (e: MouseEvent) => designer.hideConsole()
                )
              })
            )
          )
        )
      }
    ),
    Partition(postOp = (p: Partition) => _contentPane = p)
  ).render().execPostOps()
}
