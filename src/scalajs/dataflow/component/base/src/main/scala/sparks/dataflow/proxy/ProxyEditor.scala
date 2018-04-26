package sparks.dataflow.proxy

import org.scalajs.dom.raw.{HTMLElement, HTMLInputElement, HTMLTextAreaElement}
import sparks.dataflow.protocols.Auxiliaries.Schema
import sparks.dataflow.proxy.controls.SchemaEditor
import sparks.dataflow.proxy.controls.SchemaGrid.{GridSwitches, SchemaGrid}
import sparks.webui.base.DomFunction
import sparks.webui.entity.validators.CustomValidator
import sparks.webui.entity.{Entity, FieldSpec, FieldType}
import sparks.webui.forms.UIGenerator
import uifrwk.ClientToolbar.ToolbarItem

trait ProxyEditor extends Entity with DomFunction {

  val proxy: BaseProxy
  val autoGenerateUI = false

  var caption: HTMLInputElement = _
  var description: HTMLTextAreaElement = _

  def userDefinedUI: HTMLElement = {
    import Tags._
    table(
      t => {
        t.style.tableLayout = "fixed"
        t.style.borderCollapse = "collapse"
        t.style.width = "100%"
      },
      tr(td(label("Caption"))),
      tr(td {
        caption = input
        caption.style.width = "99%"
        caption
      }),
      tr(td(label("Description"))),
      tr(td {
        description = textArea
        description.style.width = "99%"
        description.style.height = "50px"
        description
      }),
      //spacerRow(1),
      tr(
        td { cell => {
          cell.className = "fsCell"
          cell.style.height = "250px"
          cell.style.verticalAlign = "top"
          interface(cell)
        }
        }
      )
    )
  }

  def interface(parent: HTMLElement): HTMLElement = UIGenerator(this.form, this).run()

  def fill(): Unit = {
    caption.value = proxy.node.caption
    description.value = proxy.node.description
  }

  def collect(): Unit = {
    proxy.node.caption = caption.value
    proxy.node.description = description.value
  }

  protected def SchemaGridFieldSpec(
                                     name: String,
                                     label: String,
                                     grid: SchemaGrid,
                                     height: Int = 250,
                                     validationRule: () => Boolean = null
                                   ) =
    FieldSpec(
      name,
      label,
      FieldType.Custom,
      customRenderer = _ => {
        Tags.div(d => {
          d.style.width = "100%"
          d.style.height = s"${height}px"
          grid.render()
        })
      },
      validator = if (validationRule != null) CustomValidator(_ => validationRule()) else null
    )

  protected def SchemaEditorFieldSpec(
                                       name: String,
                                       label: String,
                                       editor: SchemaEditor,
                                       height: Int = 150,
                                       validationRule: () => Boolean = null
                                     ) =
    FieldSpec(
      name,
      label,
      FieldType.Custom,
      customRenderer = _ =>
        Tags.div(d => {
          d.style.width = "100%"
          d.style.height = s"${height}px"
          editor.render(d)
        }),
      validator = if (validationRule != null) CustomValidator(_ => validationRule()) else null
    )

  object SchemaGrid {
    def apply(schema: Schema): SchemaGrid = apply(schema, GridSwitches(side = false, aggfunc = false, userfunc = false))

    def apply(schema: Schema, visibility: GridSwitches, editable: GridSwitches = null): SchemaGrid = {
      val grid = new SchemaGrid(schema, visibility, editable)
      registerControl(grid)
      grid
    }
  }

  object SchemaEditor {
    def apply(schema: Schema, editable: GridSwitches, buttons: Seq[ToolbarItem] = null) = {
      val editor = new SchemaEditor(schema, editable, buttons)
      registerControl(editor)
      editor
    }
  }

}
