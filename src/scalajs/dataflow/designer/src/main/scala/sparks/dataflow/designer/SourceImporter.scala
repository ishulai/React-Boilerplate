package sparks.dataflow.designer

import sparks.dataflow.connector.DataSource._
import sparks.dataflow.connector.{ConnectorDef, ConnectorProxy, ConnectorProxyEditor}
import sparks.webui.forms.EntityDialog

class SourceImporter extends DesignerAuxiliary {

  import TableSelector._

  private var proxy: ConnectorProxy = _
  private val connector = new ConnectionSelector()
  lazy private val tables = new TableSelector()
  lazy private val loader = new SchemaLoader()

  private val form = EntityDialog(connector, null)

  var completeImport: (DataSource) => _ = _

  form.next = (currIndex: Int) => {

    val currEntity = form.entities(currIndex)
    currEntity match {
      case _: ConnectionSelector =>
        if (form.entities(1) == null) {
          val cdef = any(ConnectorDef(connector.categoryId, connector.selected))
          proxy = newInstance[ConnectorProxy](connector.selected.interfaceDesc.jarDesc.qualifiedName)(cdef)
          form.entities =
            if (proxy.requiresSelector) Array(connector, proxy.editor, tables, loader)
            else Array(connector, proxy.editor, loader)
        }

      case _: ConnectorProxyEditor =>
        if (proxy.requiresSelector)
          tables.proxy = proxy
        else
          loader.objectsToLoad = proxy.getObjectsToLoad

      case _: TableSelector => loader.objectsToLoad = tables.selection

    }
  }

  form.ok = () => if (completeImport != null) completeImport(loader.objectsToLoad)

  def show() = form.show()
}
