package sparks.webui.entity.validators

class CustomValidator(rule: (String)=>Boolean) extends BaseValidator {

  def validate(): Boolean = {
    reset()
    if (!rule(value)) {
      highlight()
      false
    }
    else true
  }

}

object CustomValidator {
  def apply(rule: (String)=>Boolean) = new CustomValidator(rule)
}
