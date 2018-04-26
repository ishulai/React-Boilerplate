package sparks.dataflow.designer

import org.scalajs.dom.raw.HTMLElement
import sparks.webui.base.BaseControl

trait DesignerAuxiliary extends BaseControl {

  var designer: DataFlowDesigner.type = _

  object console {

    def title: String = designer.console.title
    def title_=(s: String) = designer.console.title = s
    def element = designer.console.element
    def visible = designer.isConsoleVisible
    def userData = designer.console.userData

    def display(content: Any, title: String = null, userData: Any = null) = {
      show()
      if (title != null) this.title = title
      if (userData != null) designer.console.userData = userData
      content match {
        case s: String => designer.console.display(s)
        case hobj: HTMLElement => designer.console.display(hobj)
        case _ => hide()
      }
    }

    def show() = designer.showConsole()
    def hide() = designer.hideConsole()
    def clear() = designer.clearConsole()
  }

}
