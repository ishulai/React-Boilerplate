package sparks.webui.entity

object FieldType extends Enumeration {
  type FieldType = Value

  val
  Text, TextArea, Password, Collapsable, Select,
  List, RadioButton, Checkbox, UploadArea,
  RadioGroup, RadioGroupWithInput, Custom = Value

  def hasIndependentLabel(value: FieldType) = value match {
    case RadioButton | Checkbox | UploadArea => false
    case _ => true
  }

  def needsId(value: FieldType) = value match {
    case RadioButton | RadioGroup | RadioGroupWithInput | Checkbox | Custom => false
    case _ => true
  }
}
