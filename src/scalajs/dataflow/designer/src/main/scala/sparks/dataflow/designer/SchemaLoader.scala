package sparks.dataflow.designer

import io.circe.generic.auto._
import io.circe.syntax._
import org.scalajs.dom.raw.{HTMLElement, HTMLImageElement}
import sparks.dataflow.connector.DataSource._
import sparks.dataflow.protocols.Auxiliaries.Field
import sparks.dataflow.protocols.Connector.SchemaQuery
import sparks.webui.base.{AjaxClient, DomFunction}

class SchemaLoader extends DesignerEntity {

  var objectsToLoad: DataSource = _
  val title = "Importing schema"
  val autoGenerateUI = false
  override val isBackAllowed = false

  def userDefinedUI: HTMLElement = {
    import Tags._
    div(d => {
      d.style.width = "500px"
      d.style.height = "300px"
      d.style.position = "relative"
      d.style.overflow = "auto"
      table(
        t => {
          t.cellSpacing = "0"
          t.cellPadding = "0"
          t.style.borderCollapse = "collapse"
        },
        objectsToLoad.databases
          .flatMap(db => {
            db.entities.map(entity => {
              tr(td {
                val line = TableAccessor(
                  label(
                    "/images/icons/progress.gif",
                    s"${db.name}.${entity.schema}.${entity.name}"
                  )
                )
                val icon = line.cell(0).children(0).asInstanceOf[HTMLImageElement]
                query[Array[Field]](
                  Path("designer", "listcolumns"),
                  SchemaQuery(
                    objectsToLoad.proxy.toProtocol,
                    db.name,
                    entity.schema,
                    entity.name
                  ).asJson.noSpaces,
                  columns => {
                    entity.columns = columns
                    icon.src = "/images/icons/check.png"
                  },
                  e => {
                    icon.src = "/images/icons/delete.png"
                  }
                )
                line.element
              })
            })
          }): _*
      )
    })
  }

  def fill() = {}
  def collect() = {}
}
