package sparks.dataflow.proxy.controls

import org.scalajs.dom.raw.HTMLElement
import sparks.dataflow.protocols.Auxiliaries.Schema
import sparks.dataflow.proxy.controls.SchemaGrid._
import sparks.webui.base.DomFunction
import sparks.webui.entity.EditorControl
import uifrwk.ClientToolbar.{Button, ClientToolbar, ToolbarItem}
import uifrwk.Layout.{Layout, Partition}


class SchemaEditor(
                    val schema: Schema,
                    val editable: GridSwitches,
                    buttons: Seq[ToolbarItem] = null
                  )
  extends DomFunction
    with EditorControl {

  private val grid: SchemaGrid = new SchemaGrid(
    schema,
    GridSwitches(
      alias = false,
      newType = false,
      nullable = false,
      side = false,
      aggfunc = false,
      userfunc = false
    ),
    editable
  )

  def complete() = grid.complete()
  def collect() = grid.collect()
  def clear() = grid.clear()
  def isEmpty = grid.isEmpty
  def nonEmpty = grid.nonEmpty

  private val finalButtons = {
    val sys =
      Seq(
        Button("add.png", "Add new column", () => addNewColumn()),
        Button("delete.png", "Delete selected columns", () => deleteColumns())
      )

    if (buttons == null) sys else sys ++ buttons
  }

  def render(container: HTMLElement) =
    Layout(
      container,
      isVertical = true,
      (layout: Layout) => {
        layout.cellPadding = 0
        layout.cellSpacing = 0
      },
      Partition(
        size = "24px",
        content = ClientToolbar(
          (bar: ClientToolbar) => {
            bar.itemCellSize = 24
            bar.itemImgSize = 16
            bar.useBackgroundColor = true
          },
          finalButtons: _*
        ),
        postOp = (p: Partition) => p.element.style.backgroundColor = "#eeeeee"
      ),
      Partition(postOp = (p: Partition) => grid.render(p.element))
    ).render().execPostOps().element

  private def addNewColumn() = grid.appendRow()

  def addNewColumn(name: String, dataType: String) = grid.appendRow(Seq(name, dataType))

  private def deleteColumns() = grid.deleteCheckedRows()

}