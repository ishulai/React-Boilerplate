package sparks.dataflow.prototypes

import java.util.UUID

import sparks.dataflow.protocols.Auxiliaries.Schema

import scala.collection.mutable.ListBuffer

object GraphicalModel {

  trait Graph {

    val id: UUID
    def nodes: ListBuffer[Vertex]

  }

  trait Vertex {

    val id: Int
    val graph: Graph
    var schema: Schema

    var caption: String = ""
    var description: String = ""

    def inPort: VertexPort = if (inPorts.isEmpty) null else inPorts.head
    def outPort: VertexPort = if (outPorts.isEmpty) null else outPorts.head

    def inPorts: ListBuffer[VertexPort]
    def outPorts: ListBuffer[VertexPort]

    def inputSchema: Schema = inPorts.fromSchema(0)
    def inputSchemas(index: Int): Schema = inPorts.fromSchema(index)
    def outputSchema: Schema = outPorts.schema(0)
    def outputSchemas(index: Int): Schema = outPorts.schema(index)

    def isValid = schema != null && outputSchema != null
    def validate(): Boolean
  }

  trait VertexPort {

    val id: Int
    var schema: Schema

    def link: Edge
    def links: ListBuffer[Edge]

  }

  trait Edge {

    var from: VertexPort
    var to: VertexPort

  }

  implicit class PortListExtension(list: ListBuffer[VertexPort]) {
    // Special accessor for inputSchema
    def fromSchema(index: Int): Schema = {
      if (index < list.length) {
        val port = list(index)
        if (port.link == null || port.link.from == null)
          null
        else
          port.link.from.schema
      }
      else throw new Exception("Port index out of range.")
    }

    // Special accessor for selfSchema
    def schema(index: Int): Schema = {
      if (index < list.length) {
        val port = list(index)
        if (port == null)
          null
        else
          port.schema
      }
      else throw new Exception("Port index out of range.")
    }

    def isValid: Boolean = list.nonEmpty && list.forall(_.schema != null)
  }

}
