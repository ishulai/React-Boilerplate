package sparks.webui.entity

import org.scalajs.dom.raw.HTMLElement
import sparks.webui.entity.FieldType._
import sparks.webui.entity.validators.BaseValidator

case class FieldSpec(
                      name: String,
                      label: String,
                      fieldType: FieldType,
                      userData: Any = "",
                      width: Int = 0,
                      height: Int = 0,
                      validator: BaseValidator = null,
                      childSpecs: Seq[FieldSpec] = null,
                      customRenderer: FieldSpec => HTMLElement = null
                    ) {

  def validate(): Boolean = if (validator != null) validator.validate() else true

}