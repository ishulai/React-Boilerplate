package sparks.dataflow.protocols.processmodel

case class ActivityPort(id: Int, activityId: Int, transitions: List[Transition])