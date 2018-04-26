package sparks.dataflow.designer

import io.circe.generic.auto._
import io.circe.syntax._
import org.scalajs.dom.raw.{HTMLElement, HTMLImageElement, MouseEvent}
import sparks.dataflow.connector.ConnectorProxy
import sparks.dataflow.connector.DataSource._
import sparks.dataflow.protocols.Auxiliaries.{DbEntity, EntityPreview}
import sparks.dataflow.protocols.Connector.SchemaQuery
import uifrwk.Layout._
import uifrwk.{Preview, Tree, TreeNode}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object TableSelector {

  object NodeType extends Enumeration {
    type NodeType = Value
    val Database, Table = Value
  }

  case class NodeData(`type`: NodeType.NodeType, schema: String, name: String, qualifiedName: String = "")


  class TableSelector
    extends DesignerEntity
      with sparks.dataflow.protocols.Auxiliaries.JsonSupport {

    var proxy: ConnectorProxy = _
    var defaultCatalog: String = ""
    var selection: DataSource = _

    val title = "Select entities"
    val autoGenerateUI = false
    override val fixedUIHeight = 500

    private var dbTree: Tree = _
    private var navigator: Partition = _
    private var preview: Partition = _


    def userDefinedUI: HTMLElement = {

      val container = createContainer()
      val view = Layout(container, height = "400px")
      view.autoFill = false
      navigator = view.addPartition(Partition("navigator", "200px"))
      view.addSplitter()
      preview = view.addPartition(Partition("preview"))
      view.render()

      navigator.element.appendChild(Tags.label("/images/icons/progress.gif", "Loading..."))

      navigator.element.parentElement.style.verticalAlign = "top"

      val catalog = defaultCatalog.toLowerCase()
      query[Array[String]](
        Path("designer", "listdatabases"),
        proxy.toProtocol.asJson.noSpaces,
        databases => {
          navigator.element.textContent = ""
          dbTree = Tree(navigator, imgPath = "/images/icons")

          dbTree.onDynamicLoad = (node: TreeNode) => {
            val dbName = node.userData.asInstanceOf[NodeData].name
            query[Array[DbEntity]](
              Path("designer", "listtables"),
              SchemaQuery(proxy.toProtocol, catalog = dbName).asJson.noSpaces,
              tables => {
                tables.foreach { table => {
                  val tableNode = TreeNode("%s.%s".format(table.schema, table.name), "table.png", true)
                  tableNode.userData = any(NodeData(NodeType.Table, table.schema, table.name, table.qualifiedName))
                  node.addChild(tableNode)
                }
                }
                node.finalizeDynamicLoading()
                node.expand()
              },
              e => {}
            )
          }

          dbTree.onNodeSelected = (node: TreeNode) => {
            EntityPreviewer.busy()
            val ud = node.userData.asInstanceOf[NodeData]
            ud.`type` match {
              case NodeType.Database => clearPreview()
              case NodeType.Table =>
                val params = SchemaQuery(
                  proxy.toProtocol,
                  catalog = node.parentNode.userData.asInstanceOf[NodeData].name,
                  table = ud.qualifiedName
                )
                query[EntityPreview](
                  Path("designer", "previewtable"),
                  params.asJson.noSpaces,
                  ep => {
                    preview.clear()
                    EntityPreviewer(preview.element, ud.name, params).render(ep)
                  },
                  e => {
                    // todo: temoprary
                    alert(e.getMessage)
                  }
                )
            }
          }

          var default: TreeNode = null
          databases.foreach { db => {
            val node = TreeNode(db, "database.png")
            node.userData = any(NodeData(NodeType.Database, "", db))
            node.dynamic = true
            dbTree.addNode(node)
            if (catalog.nonEmpty && catalog == db.toLowerCase()) default = node
          }
          }
          dbTree.render()

          if (default != null) default.expand()
        },
        e => {}
      )
      container
    }

    private class EntityPreviewer(container: HTMLElement, caption: String, params: SchemaQuery) {
      private var previewArea: Layout = _
      private var title: Partition = _
      private var grid: Partition = _

      def render(ep: EntityPreview) = {
        preview.clear()
        if (previewArea == null) {
          previewArea = Layout(preview.element, isVertical = true)
          title = previewArea.addPartition(Partition(size = "32px"))
          grid = previewArea.addPartition(Partition())
          previewArea.render()

          title.element.style.overflow = "hidden"
        }

        renderCaption(ep)
        renderPreview(ep)
      }

      private def renderCaption(ep: EntityPreview) = {
        title.clear()

        val tbl = TableAccessor(Tags.table(1, 2))
        tbl.element.style.width = "100%"
        tbl.element.style.tableLayout = "fixed"
        val cachedTime = if (ep.timeCached.nonEmpty) "(Cached: %s)".format(ep.timeCached) else ""
        tbl.appendChild(0, Tags.label("%s %s".format(caption, cachedTime), false))

        val right = tbl.cell(1)
        right.style.width = "16px"
        right.appendChild(refreshButton)

        title.element.appendChild(tbl.element)
      }

      private def refreshButton: HTMLImageElement = {
        val btn = Tags.img("/images/icons/refresh.png")
        btn.onclick = (ev: MouseEvent) => {
          previewArea = null
          EntityPreviewer.busy()
          query[EntityPreview](
            Path("designer", "reloadpreview"),
            params.asJson.noSpaces,
            ep => render(ep),
            e => {
              alert(e.getMessage)
            }
          )
        }
        btn
      }

      private def renderPreview(ep: EntityPreview): Unit = {
        grid.clear()
        Preview(grid).render(parseXml(ep.preview))
      }
    }

    private object EntityPreviewer {

      def apply(
                 container: HTMLElement,
                 caption: String,
                 params: SchemaQuery
               ) = new EntityPreviewer(container, caption, params)

      def busy() = {
        import Tags._
        preview.clear()
        preview.element.appendChild(label("/images/icons/progress.gif", "Loading preview..."))
      }

    }

    private def clearPreview(): Unit = preview.clear()

    def fill(): Unit = {}

    def collect(): Unit = {

      val dbmap = mutable.HashMap[String, Database]()
      dbTree.getCheckedNodes().foreach { tbl => {
        val dbNode = tbl.parentNode.userData.asInstanceOf[NodeData]
        val db = if (!dbmap.contains(dbNode.name)) {
          val t = Database(dbNode.name, ListBuffer[Entity]())
          dbmap(dbNode.name) = t
          t
        }
        else dbmap(dbNode.name)

        val nd = tbl.userData.asInstanceOf[NodeData]
        db += Entity(nd.schema, nd.name, nd.qualifiedName)
      }
      }

      val lb = ListBuffer[Database]()
      dbmap.values.foreach {
        lb += _
      }

      selection = DataSource(proxy, lb)
    }
  }

}