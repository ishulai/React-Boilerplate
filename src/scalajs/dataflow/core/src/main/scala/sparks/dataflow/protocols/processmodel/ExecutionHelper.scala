package sparks.dataflow.protocols.processmodel

import sparks.dataflow.protocols.BaseConnector
import sparks.dataflow.protocols.Connector.ConnectorProtocol

class ExecutionHelper(process: Process) {

  private lazy val activityMap = process.activities.map(a => (a.id, a)).toMap
  private lazy val portMap = (process.activities.flatMap(_.inPorts) ++ process.activities.flatMap(_.outPorts)).map(p => (p.id, p)).toMap
  var connectorGetter: ConnectorProtocol => BaseConnector = _

  def getConnector(cp: ConnectorProtocol) = {
    if (connectorGetter != null) {
      val connector = connectorGetter(cp)
      if (connector == null)
        throw new Exception(s"Unable to load connector ${cp.jar.qualifiedName}.")
      else
        connector
    }
    else
      throw new Exception("ConnectorGetter is not assigned.")
  }

  def findActivity(id: Int) = activityMap(id)
  def findPort(id: Int) = portMap(id)

}
