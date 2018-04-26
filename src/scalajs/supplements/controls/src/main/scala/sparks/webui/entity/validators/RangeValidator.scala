package sparks.webui.entity.validators

class RangeValidator(min: Int, max: Int, allowNull: Boolean = false) extends BaseValidator {

  def validate(): Boolean = {
    reset()
    if (value.isEmpty && allowNull)
      true
    else {
      try {
        val v = value.toInt
        if (v < min || v > max) {
          highlight()
          false
        }
        else true
      }
      catch {
        case _: Throwable => {
          highlight()
          false
        }
      }
    }
  }
}

object RangeValidator {
  def apply(min: Int, max: Int, allowNull: Boolean = false) = new RangeValidator(min, max)
}


