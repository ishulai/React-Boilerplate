package sparks.dataflow.protocols.processmodel

import java.util.UUID

case class ProcessInstance(id: UUID, activities: Map[Int, ActivityInstance])