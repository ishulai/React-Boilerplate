package sparks.dataflow.protocols.processmodel

object Topology {

  case class ParallelActivities(generation: Integer, activities: List[Activity])
  case class ProcessTopology(generations: Int, parallels: List[ParallelActivities])

}
