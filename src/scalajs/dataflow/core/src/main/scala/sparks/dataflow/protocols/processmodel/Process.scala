package sparks.dataflow.protocols.processmodel

import java.util.UUID

import sparks.dataflow.protocols.processmodel.Topology._

import scala.collection.mutable.ListBuffer

case class Process(id: UUID, activities: List[Activity], codes: Seq[Code]) {

  private var _helper: ExecutionHelper = _
  def helper = _helper
  def helper_=(value: ExecutionHelper) = {
    if (_helper != value) {
      _helper = value
      activities.foreach(_.helper = value)
    }
  }

  def topology = {

    var generations = 0

    activities
      .filter(a => a.inPorts.isEmpty && a.outPorts.nonEmpty) // find roots
      .foreach(a => {
      val gen = a.populate(0)
      if (gen > generations) generations = gen
    })

    val parallels = new ListBuffer[ParallelActivities]()
    for (i <- 0 to generations) {
      parallels += ParallelActivities(
        i,
        activities.filter(_.generation == i)
      )
    }

    ProcessTopology(generations + 1, parallels.toList)
  }

  def run(runTo: Int, rc: RunningContext) = {

    if (_helper == null)
      throw new Exception("ExecutionHelper hasn't been assigned yet.")
    else {
      topology
        .parallels
        .flatMap(_.activities)
        .foreach(a => a.context.run(a, rc))
    }

  }
}
