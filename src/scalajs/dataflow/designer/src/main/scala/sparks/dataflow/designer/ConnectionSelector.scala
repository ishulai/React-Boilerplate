package sparks.dataflow.designer

import io.circe.generic.auto._
import org.scalajs.dom.raw.HTMLElement
import sparks.dataflow.protocols.Component._
import sparks.webui.base.{AjaxClient, DomFunction}
import sparks.webui.entity.Entity
import uifrwk.Layout._
import uifrwk.{SimpleList, SimpleListItem, TileView}

class ConnectionSelector extends DesignerEntity {

  var categoryId: String = _
  var selected: Component = _

  val title = "Connect data"
  val autoGenerateUI = false
  val userDefinedNextPage = null

  private var connectors: TileView = _
  private var reg: ComponentRegistry = _

  def userDefinedUI: HTMLElement = {

    val container = createContainer()
    val view = Layout(container, height = "300px")
    view.autoFill = false
    val p1 = view.addPartition(Partition("categoryList", "120px"))
    val p2 = view.addPartition(Partition("connectorList"))
    view.render()

    p1.element.parentElement.style.verticalAlign = "top"
    p1.element.style.backgroundColor = "#eaeaea"

    connectors = TileView(p2.element)
    connectors.imgPath = "/images/icons/dataflow/connectors"
    connectors.tileWidth = 128
    connectors.iconSizeLimit = 64
    connectors.isSelectable = true
    connectors.isCompact = true
    connectors.render()

    query[ComponentRegistry](
      Path("designer", "getconnectors"),
      null,
      result => {
        reg = result
        val list = SimpleList(p1.element)
        list.addItem("All", "")
        reg.categories.foreach { cat => list.addItem(cat.label, cat.id) }
        list.onItemClick = (item: SimpleListItem) => {
          refreshConnectors(item.userData.toString)
        }
        list.render()

        list.items(0).setSelected(true)
        refreshConnectors("")
      },
      e => {}
    )

    def refreshConnectors(category: String): Unit = {
      val cat = if (category == "") Category("", "", reg.all) else reg.find(category)
      if (cat != null) {
        connectors.clear()
        cat.components.foreach { c =>
          connectors.addTile(
            c.id,
            c.interfaceDesc.toolboxDesc.label,
            "",
            any(c),
            c.interfaceDesc.toolboxDesc.icon
          )
        }
        connectors.redraw()
        if (connectors.tiles.nonEmpty)
          connectors.tiles(0).setSelected(true)
      }
    }

    container
  }

  def fill(): Unit = {}

  def collect(): Unit = {
    val item = connectors.getSelectedItem()
    if (item != null) {
      selected = item.userData.asInstanceOf[Component]
      categoryId = reg.getCategoryId(selected.id)
    }
  }

}
