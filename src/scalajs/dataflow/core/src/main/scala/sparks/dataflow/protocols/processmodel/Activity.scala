package sparks.dataflow.protocols.processmodel

case class Activity(id: Int, inPorts: List[ActivityPort], outPorts: List[ActivityPort], context: ActivityContext) {

  private var _generation: Int = 0
  def generation: Int = _generation

  private var _helper: ExecutionHelper = _
  def helper = _helper
  def helper_=(value: ExecutionHelper) = _helper = value

  def dfName = s"dataFrames($id)"

  def populate(current: Int): Int = {
    if (current > _generation) _generation = current
    val trans = outPorts.flatMap(_.transitions)
    if (trans.nonEmpty) {
      var next = current + 1
      trans
        .map(t => _helper.findActivity(_helper.findPort(t.toPort).activityId))
        .foreach(a => next = a.populate(next))
      next
    }
    else
      current
  }

  private def checkHelper() = if (_helper == null)
    throw new Exception("The helper for activity %d hasn't been assigned yet.")

  def getInboundActivity: Activity = getInboundActivity(0)

  def getInboundActivity(index: Int): Activity = {
    checkHelper()
    if (inPorts.lengthCompare(index) > 0) {
      val thisPort = inPorts(index)
      if (thisPort != null && thisPort.transitions.nonEmpty) {
        val port = _helper.findPort(thisPort.transitions.head.fromPort)
        if (port != null)
          return _helper.findActivity(port.activityId)
      }
    }
    null
  }
}