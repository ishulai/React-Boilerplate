package sparks.webui.forms

import sparks.webui.entity.Entity
import uifrwk.Dialog

class EntityDialog(editors: Entity*) extends EntityForm(editors: _*) {

  private def w: String = if (current.fixedUIWidth > 0) current.fixedUIWidth + "px" else null
  private def h: String = if (current.fixedUIHeight > 0) current.fixedUIHeight + "px" else null

  private val dialog = Dialog(current.title, content, w, h)
  // Form.onclose
  onclose = () => {
    dialog.onclose = null
    dialog.close()
  }
  // Form.redraw
  redraw = () => {
    dialog.changeTitle(current.title)
    dialog.redraw(content, w, h)
    buttons.fit()
  }
  // Form.resize
  resize = () => {
    dialog.resizeToFitContent()
    dialog.adjustPosition()
  }

  def show(): Dialog = {
    dialog.show()
    buttons.fit()

    current.complete()
    current.fill()

    dialog
  }
}

object EntityDialog {
  def apply(editors: Entity*) = new EntityDialog(editors: _*)
}

