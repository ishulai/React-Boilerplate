package sparks.webui.base

import org.scalajs.dom.raw.{HTMLButtonElement, HTMLElement, MouseEvent}
import uifrwk.Dialog

import scala.collection.mutable.ListBuffer
import scala.scalajs.js

trait BaseControl extends DomFunction {

  object messageBox {

    object Answer extends Enumeration {
      type Answer = Value
      val Yes, No = Value
    }

    case class Button(caption: String, onclick: MouseEvent => Unit)

    object buttons {
      def close = Button("Close", (_: MouseEvent) => messageBox.close())
      def cancel = Button("Cancel", (_: MouseEvent) => messageBox.close())
      def yes = Button(
        "Yes",
        (_: MouseEvent) => {
          lastAnswer = Answer.Yes
          messageBox.close()
        }
      )
      def no = Button(
        "No",
        (_: MouseEvent) => {
          lastAnswer = Answer.No
          messageBox.close()
        }
      )
    }

    import Answer._

    private var dialog: Dialog = _
    private var lastAnswer: Answer = _

    def ask(message: String, yes: () => Unit, no: () => Unit): Unit = {
      show(message, buttons.yes, buttons.no, buttons.cancel)
      dialog.onclose = () => {
        if (lastAnswer == Answer.Yes && yes != null) yes()
        else if (lastAnswer == Answer.No && no != null) no()
        lastAnswer = null
        true
      }
    }

    def showBusy(message: String, buttons: Button*) = show(Tags.label("/images/icons/progress.gif", message), buttons: _*)

    def showError(message: String) = show(Tags.multiline(message), buttons.close)
    //show(Tags.label("/images/icons/error.png", message), buttons.close)

    def show(content: Any, buttons: Button*): Unit = show(content, "400px", "200px", buttons: _*)

    def show(content: Any, width: js.Any, height: js.Any, buttons: Button*): Unit = {

      import Tags._

      val cobj = content match {
        case hobj: HTMLElement => hobj
        case s: String => span(s)
        case _ => span("Unknown content type.")
      }

      val btns: ListBuffer[HTMLButtonElement] = ListBuffer()

      dialog = Dialog(
        "",
        table(
          spacerRow(2),
          tr(td(cobj)),
          spacerRow(2),
          tr(td(c => {
            if (buttons.nonEmpty)
              buttons.foreach(b => {
                val btn = button(b.caption, b.onclick)
                btns += btn
                c.appendChild(btn)
                c.appendChild(spacer(2))
              })
          }))
        ),
        width,
        height
      ).show()
    }

    def close() =
      if (dialog != null) {
        dialog.close()
        dialog = null
      }

  }

}
