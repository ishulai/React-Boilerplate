package sparks.dataflow.graph

import mxGraph.MxCell
import sparks.dataflow.protocols.processmodel.Transition
import sparks.dataflow.prototypes.GraphicalModel.{Edge, VertexPort}

class Link(val edge: MxCell, val diagram: Diagram) extends Edge {

  var from: VertexPort = _
  var to: VertexPort = _

  def connect(src: NodePort, tar: NodePort): Link = {
    from = src
    to = tar
    from.links += this
    to.links += this
    this
  }

  def reconnect(newSrc: NodePort, newTar: NodePort): Link = {
    from.links -= this
    to.links -= this
    from = newSrc
    to = newTar
    from.links += this
    to.links += this
    this
  }

  def delete(): Unit = {
    if (from != null) from.links -= this
    if (to != null) to.links -= this
  }

  def toProtocol: Transition = Transition(from.id, to.id)

}

object Link {
  def apply(edge: MxCell, diagram: Diagram) = new Link(edge, diagram)
}
