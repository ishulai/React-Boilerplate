package sparks.dataflow.designer

import org.scalajs.dom.raw.MouseEvent
import sparks.dataflow.connector.DataSource._
import sparks.dataflow.designer.TableSelector.TableSelector
import sparks.dataflow.protocols.Auxiliaries.Schema
import sparks.dataflow.protocols.Component._
import sparks.dataflow.proxy.entity.EntityProxy
import sparks.webui.forms.EntityDialog
import uifrwk.ClientToolbar._
import uifrwk.Layout._
import uifrwk.PopupMenu._
import uifrwk.{Tree, TreeNode}

import scala.collection.mutable.ListBuffer

class SourceManager(owner: DataFlowDesigner.type) extends Draggable {

  designer = owner
  private var sources: Tree = _

  Layout(
    createContainer(designer.toolbox.sourceDock.element),
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
        Button("addsrc.png", "New data source...", () => dataSources.addNew()),
        Button("validatesrc.png", "Validate", () => validateSources()),
        Seperator(),
        Button("dock2right.png", "Dock to right", () => dockToDesigner())
      ),
      postOp = (p: Partition) => p.element.style.backgroundColor = "#eeeeee"
    ),
    Partition(
      postOp = (p: Partition) => sources = initSources(p)
    )
  ).render().execPostOps()

  private def initSources(p: Partition): Tree = {

    def getProxy(n: TreeNode) = {
      val root = n.getRoot()
      if (root != null && root.userData != null)
        root.userData.asInstanceOf[DataSource].proxy
      else
        throw new Exception("Tree structure is incorrect.")
    }

    val tree = Tree(p, imgPath = "/images/icons")
    tree.addNode(TreeNode("No data sources have been defined yet", ""))
    tree.render()
    tree.onContextMenu = (ev: MouseEvent, node: TreeNode) => {
      val proxy = getProxy(node)
      if (proxy != null) {
        val menu = node.userData match {
          case ds: DataSource => PopupMenu(
            if (proxy.canImport) MenuItem("", "Import entities...", (_: MenuItem) => dataSources.importEntities(ds))
            else null,
            MenuItem("", "Settings...", (_: MenuItem) => ds.edit(node)),
            MenuSeperator(),
            MenuItem("delete.png", "Delete", (_: MenuItem) => {})
          )
          case db: Database => PopupMenu(
            if (proxy.canImport)
              MenuItem("", "Import entities...", (_: MenuItem) => dataSources.importEntities(
                node.parentNode.userData.asInstanceOf[DataSource],
                db.name
              ))
            else null,
            MenuItem("delete.png", "Delete", (_: MenuItem) => {})
          )
          case _ => PopupMenu(
            MenuItem("", "Add to flow", (_: MenuItem) => {}),
            MenuItem("delete.png", "Delete", (_: MenuItem) => {})
          )
        }
        menu.show(ev.clientX, ev.clientY)
      }
    }
    tree
  }

  private def validateSources() = {}

  def dockToToolbox() = {}

  def dockToDesigner() = {}

  /**
    * dataSources
    */
  object dataSources {
    private val list = ListBuffer[DataSource]()

    def +=(ds: DataSource) = {
      if (ds != null) {
        val curr = list
          .find(d => d.proxy.connector.equals(ds.proxy.connector) && d.proxy.locationallyEquals(ds.proxy)).orNull

        if (curr == null)
          list += ds
        else
          ds.databases.foreach(curr += _)
      }
    }

    def clear() = list.clear()

    def length = list.length

    def addNew(): Unit = {
      val importer = new SourceImporter()
      importer.completeImport = (ds: DataSource) => {
        if (ds != null) dataSources += ds
        dataSources.render()
      }
      importer.show()
    }

    def importEntities(ds: DataSource, defaultCatalog: String = "") = {
      val selector = new TableSelector()
      selector.proxy = ds.proxy
      selector.defaultCatalog = defaultCatalog
      val loader = new SchemaLoader()
      val dialog = EntityDialog(selector, loader)
      dialog.next = (currIndex: Int) => if (currIndex == 0) loader.objectsToLoad = selector.selection
      dialog.ok = () => {
        this += loader.objectsToLoad
        render()
      }
      dialog.show()
    }

    def render() = {
      sources.clear()
      list.foreach(ds => {
        val dsNode = sources.addNode(TreeNode(ds.name, "dbserver.png", userData = any(ds)))
        ds.databases.foreach(db => {
          val dbNode = dsNode.addChild(TreeNode(db.name, "database.png", userData = any(db)))
          db.entities.foreach(et =>
            dbNode.addChild(TreeNode(s"${et.schema}.${et.name}", "table.png", userData = any(et))))
        })
      })

      sources.render()

      if (sources.nodes.length > 0) sources.nodes(0).expand()
      sources.getLeaves().foreach(n => {
        val entity = n.userData.asInstanceOf[Entity]
        makeDraggable(
          DragSource(
            n.element,
            Component(
              "entity",
              UISpec(
                ToolboxSpec(
                  n.text,
                  "table.png"
                ),
                VertexSpec(
                  Seq.empty,
                  Seq(PortSpec("output", "default.png", true))
                )
              )
            ),
            n.text,
            EntityProxy(
              n.parentNode.parentNode.userData.asInstanceOf[DataSource].proxy.toProtocol,
              n.parentNode.userData.asInstanceOf[Database].name,
              entity.schema,
              entity.name,
              entity.columns
            ),
            node => {
              node.schema = Schema(entity.columns)
              node.outPort.schema = Schema(entity.columns)
            }
          )
        )
      })
    }
  }

}