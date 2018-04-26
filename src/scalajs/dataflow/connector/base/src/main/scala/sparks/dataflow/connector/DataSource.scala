package sparks.dataflow.connector

import sparks.dataflow.protocols.Auxiliaries.Field
import uifrwk.TreeNode

import scala.collection.mutable.ListBuffer

object DataSource {

  case class DataSource(proxy: ConnectorProxy, databases: ListBuffer[Database]) {

    def name = proxy.name

    def +=(db: Database) = {
      val curr = databases.find(_.name.equalsIgnoreCase(db.name)).orNull
      if (curr == null)
        databases += db
      else
        db.entities.foreach(curr += _)
    }

    def edit(node: TreeNode) = {
      import scala.scalajs.js
      proxy.editCompleted = () => {
        node.userData = DataSource(proxy, databases).asInstanceOf[js.Any]
      }

      proxy.edit()
    }

    def clear() = databases.clear()
  }

  case class Database(name: String, entities: ListBuffer[Entity]) {
    def +=(e: Entity) = if (!entities.exists(_.qualifiedName.equalsIgnoreCase(e.qualifiedName))) entities += e
  }

  case class Entity(schema: String, name: String, qualifiedName: String, var columns: Seq[Field] = null)

}
