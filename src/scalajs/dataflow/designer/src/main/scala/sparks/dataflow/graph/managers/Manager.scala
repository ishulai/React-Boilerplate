package sparks.dataflow.graph.managers

import sparks.dataflow.graph.Diagram

class Manager(val diagram: Diagram) {

  protected val graph = diagram.graph

}
