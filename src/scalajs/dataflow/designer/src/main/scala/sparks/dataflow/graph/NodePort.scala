package sparks.dataflow.graph

import mxGraph.MxCell
import sparks.dataflow.protocols.Auxiliaries.Schema
import sparks.dataflow.protocols.processmodel.ActivityPort
import sparks.dataflow.prototypes.GraphicalModel.{Edge, Vertex, VertexPort}

import scala.collection.mutable.ListBuffer

class NodePort(
                val id: Int,
                val vertex: MxCell,
                val owner: Vertex
              ) extends VertexPort {

  var schema: Schema = _

  private val _links: ListBuffer[Edge] = ListBuffer()
  def link: Edge = if (_links.nonEmpty) _links.head else null
  def links: ListBuffer[Edge] = _links

  def toProtocol: ActivityPort = ActivityPort(id, owner.id, links.map(_.asInstanceOf[Link].toProtocol).toList)

}
