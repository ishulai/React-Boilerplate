package sparks.dataflow.proxy.entity

import io.circe.syntax._
import sparks.dataflow.protocols.Auxiliaries.{Field, Schema}
import sparks.dataflow.protocols.Component._
import sparks.dataflow.protocols.Connector
import sparks.dataflow.protocols.Connector.ConnectorProtocol
import sparks.dataflow.protocols.entity._
import sparks.dataflow.protocols.processmodel.ActivityContext
import sparks.dataflow.prototypes.GraphicalModel.Vertex
import sparks.dataflow.proxy.BaseProxy

case class EntityProxy(
                        cp: ConnectorProtocol,
                        databaseName: String,
                        schema: String,
                        entityName: String,
                        columns: Seq[Field]
                      )
  extends BaseProxy
    with Connector.JsonSupport {

  if (node != null) node.schema = Schema(columns)

  // EntityProxy is a special proxy which will be created directly by SourceManager
  val spec = Component(
    "entity",
    UISpec(
      ToolboxSpec("Entity", "table.png"),
      VertexSpec(Seq.empty, Seq(PortSpec("output", "default.png", true))),
      isScript = false,
      JarSpec.empty,
      ScriptSpec.empty
    ),
    JarSpec(
      "",
      "sparks.dataflow.protocols.entity",
      "EntityContext"
    )
  )

  override def node_= (n: Vertex): Unit = {
    super.node_=(n)
    if (node != null) {
      node.schema = Schema(columns)
      if (node.outPort != null ) {
        node.outPort.schema = Schema(columns)
      }
    }
  }

  override def editor = EntityEditor(this)

  def validate() = node.isValid

  def toProtocol: ActivityContext =
    ActivityContext(
      spec.jarDesc,
      EntityContext(cp, databaseName, schema, entityName, node.schema ).asJson.noSpaces
    )

}
