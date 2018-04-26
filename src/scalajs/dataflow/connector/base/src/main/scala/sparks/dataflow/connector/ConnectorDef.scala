package sparks.dataflow.connector

import sparks.dataflow.protocols.Component.Component


case class ConnectorDef(categoryId: String, spec: Component) {

  def canEqual(that: Any) = that.isInstanceOf[ConnectorDef]

  override def equals(obj: Any) =
    obj match {
      case cd: ConnectorDef =>
        cd.canEqual(this) &&
          categoryId.equalsIgnoreCase(cd.categoryId) &&
          spec.id.equalsIgnoreCase(cd.spec.id)
      case _ => false
    }

}
