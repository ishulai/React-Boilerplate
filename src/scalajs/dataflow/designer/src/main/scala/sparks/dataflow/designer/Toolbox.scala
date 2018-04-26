package sparks.dataflow.designer

import io.circe.generic.auto._
import org.scalajs.dom.raw.{HTMLDivElement, HTMLElement, HTMLImageElement, MouseEvent}
import sparks.dataflow.protocols.Component._
import sparks.webui.base.AjaxClient
import sparks.webui.controls.SearchBox
import uifrwk.Layout._
import uifrwk.{Layout => _, _}

import scala.collection.mutable
import scala.scalajs.js

object Toolbox {

  object ToolboxView extends Enumeration {
    type ToolboxView = Value
    val Tile, Hierarchy = Value
  }

  class Toolbox(
                 owner: DataFlowDesigner.type,
                 container: HTMLElement,
                 val complete: () => _ = null
               ) extends Draggable with AjaxClient with JsonSupport {

    private var currView = ToolboxView.Tile

    private var sidebar: Sidebar = _
    private var tree: Tree = _
    var sourceDock: Partition = _
    designer = owner

    Layout(
      container,
      isVertical = true,
      width = null,
      height = null,
      (layout: Layout) => {
        layout.cellPadding = 2
        layout.cellSpacing = 1
      },
      Partition(size = "40%", postOp = (p: Partition) => sourceDock = p),
      PartitionSplitter(),
      Partition(size = "34px", postOp = (p: Partition) => initSearchBox(p)),
      Partition(postOp = (p: Partition) => initViews(p))
    ).render().execPostOps()

    private def initViews(p: Partition) = {
      sidebar = new Sidebar(p)
      tree = new Tree(p, imgPath = "/images/icons")
      query[ComponentRegistry](
        Path("designer", "getcompregistry"),
        null,
        result => {
          initSidebar(p, result)
          initTree(p, result)
          if (complete != null) complete()
        },
        e => messageBox.showError(e.getMessage)
      )
    }

    import js.JSConverters._

    private def initTree(p: Partition, registry: ComponentRegistry) =
      tree.addNodes(
        registry.categories.map(c =>
          TreeNode(c.label, "folder.png")
            .addChildren(
              c.components.map(cc =>
                TreeNode(
                  cc.interfaceDesc.toolboxDesc.label,
                  "item.png",
                  false,
                  any(cc)
                )
              ).toJSArray
            )
        ).toJSArray
      ).deleteEmptyNodes()
        .render()
        .hide()
        .getAllNodes()
        .foreach(n => if (n.level == 1) makeDraggable(DragSource(n.element, n.userData.asInstanceOf[Component])))

    import Tags._

    private def initSearchBox(p: Partition) =
      SearchBox(
        p.element,
        ev => {},
        cell => {
          cell.style.textAlign = "center"
          cell.style.width = "32px"
          img(
            "/images/icons/tree.png",
            cursor = "pointer",
            onclick = (e: MouseEvent) => {
              val image = e.srcElement.asInstanceOf[HTMLImageElement]
              if (currView == ToolboxView.Tile) {
                image.src = "/images/icons/tile.png"
                currView = ToolboxView.Hierarchy
                sidebar.hide()
                tree.show()
              }
              else {
                image.src = "/images/icons/tree.png"
                currView = ToolboxView.Tile
                sidebar.show()
                tree.hide()
              }
            }
          )
        }
      ).render()

    private def initSidebar(p: Partition, registry: ComponentRegistry) = {

      val containers = mutable.Map[String, HTMLDivElement]()
      registry.categories.foreach(c => {
        val div = createContainer()
        containers(c.id) = div
        sidebar.addSection(new SidebarSection(c.label, div))
      })
      sidebar.render()

      registry.categories.foreach(c => {
        val view = createTileView(containers(c.id))
        c.components.foreach(cc =>
          view.addTile(
            cc.id,
            cc.interfaceDesc.toolboxDesc.label,
            "",
            any(cc),
            cc.interfaceDesc.toolboxDesc.icon
          )
        )
        view.render()
        view.tiles.foreach(tile => makeDraggable(DragSource(tile.iconImgElement, tile.userData.asInstanceOf[Component])))
      })

      def createTileView(ct: HTMLElement): TileView = {
        val view = new TileView(ct)
        view.tileWidth = 64
        view.isSelectable = false
        view.isCompact = true
        //view.iconFontSize = "9pt"
        view.imgPath = "/images/icons/dataflow"
        view
      }
    }
  }

}