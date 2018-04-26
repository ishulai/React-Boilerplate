package sparks.webui.entity.validators

import org.scalajs.dom.raw.{HTMLElement, HTMLInputElement, HTMLTextAreaElement}

trait BaseValidator {

  var element: HTMLElement = _
  var valueReader: () => String = _

  def value: String = {
    if (valueReader != null)
      valueReader()
    else if (element != null)
      element match {
        case input: HTMLInputElement => input.value
        case text: HTMLTextAreaElement => text.value
        case _ => ""
      }
    else ""
  }

  def validate(): Boolean

  protected def highlight() = if (element != null) element.className = "invalid"

  protected def reset() = if (element != null) element.className = ""

}
