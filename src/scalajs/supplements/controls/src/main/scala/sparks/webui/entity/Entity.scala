package sparks.webui.entity

import org.scalajs.dom.raw.HTMLElement
import sparks.webui.forms.EntityForm

import scala.collection.mutable.ListBuffer

trait Entity {

  val title: String

  // Fixed dimension for EntityDialog, don't use these values anywhere else
  //
  // todo: use %
  val fixedUIWidth: Int = 800
  val fixedUIHeight: Int = 400
  final val containerWidth = "95%"

  private val controls: ListBuffer[EditorControl] = ListBuffer()
  def registerControl(ctrl: EditorControl): EditorControl = {
    if (!controls.contains(ctrl)) controls += ctrl
    ctrl
  }

  def complete() = controls.foreach(_.complete())

  val isBackAllowed: Boolean = true
  val requiresButtons: Boolean = true

  val autoGenerateUI: Boolean
  def userDefinedUI: HTMLElement
  val fieldSpecs: Seq[Any] = null

  var form: EntityForm = _
  val nextHandler: () => _ = null
  val confirmHandler: () => _ = null

  def validate(ignoreFields: String*): Boolean = {
    if (form != null) form.error.clear()
    var valid = true
    if (fieldSpecs != null && fieldSpecs.nonEmpty)
      fieldSpecs.foreach(_ match {
          case spec: FieldSpec => {
            if (ignoreFields.nonEmpty && !ignoreFields.contains(spec.name) || ignoreFields.isEmpty)
              if (!spec.validate()) valid = false
          }
          case spec: Any => {}
        }
      )
    valid
  }

  def fill(): Unit
  def collect(): Unit

}
