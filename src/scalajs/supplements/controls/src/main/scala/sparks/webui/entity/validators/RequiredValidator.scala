package sparks.webui.entity.validators

class RequiredValidator extends BaseValidator {

  def validate(): Boolean = {
    reset()
    if (value == "") {
      highlight()
      false
    }
    else true
  }

}

object RequiredValidator {
  def apply() = new RequiredValidator()
}
