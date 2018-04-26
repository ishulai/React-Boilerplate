package sparks.dataflow.connector

import sparks.dataflow.connector.DataSource._
import sparks.dataflow.protocols.Connector.ConnectorProtocol
import sparks.webui.entity.Entity
import sparks.webui.forms.EntityDialog

trait ConnectorProxy {

  val connector: ConnectorDef
  val requiresSelector: Boolean
  val canImport: Boolean = true

  var editCompleted: () => _ = _

  def editor: Entity = ???

  def edit() = {
    val dialog = EntityDialog(editor)
    if (editCompleted != null) dialog.ok = editCompleted
    dialog.show()
  }

  def toProtocol: ConnectorProtocol

  def name: String

  def locationallyEquals(p: ConnectorProxy): Boolean

  def getObjectsToLoad: DataSource = ???

}
