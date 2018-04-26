package sparks.dataflow.proxy.join

import io.circe.syntax._
import sparks.dataflow.protocols.Auxiliaries.Schema
import sparks.dataflow.protocols.Component.Component
import sparks.dataflow.protocols.join._
import sparks.dataflow.protocols.processmodel.ActivityContext
import sparks.dataflow.proxy.BaseProxy

import scala.collection.mutable.ListBuffer
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("sparks.dataflow.proxy.join.JoinProxy")
case class JoinProxy(spec: Component) extends BaseProxy {

  private var _keys: ListBuffer[KeyMapping] = _
  def keys: ListBuffer[KeyMapping] = _keys

  var joinType = JoinType.Inner

  override def editor = JoinEditor(this)

  def leftSchema: Schema = node.inputSchemas(0)
  def rightSchema: Schema = node.inputSchemas(1)
  def initKeys() = {
    _keys = new ListBuffer[KeyMapping]()
    leftSchema
      .findDuplicates(rightSchema)
      .foreach(f => _keys += KeyMapping(f.name, "", f.name, "", JoinOperator.EqualTo))
  }

  def validate() = node.inputSchemas(0) != null && node.inputSchemas(1) != null && node.isValid

  def toProtocol: ActivityContext =
    ActivityContext(
      spec.jarDesc,
      JoinContext(
        joinType,
        _keys,
        node.schema.fields
      ).asJson.noSpaces
    )
}
