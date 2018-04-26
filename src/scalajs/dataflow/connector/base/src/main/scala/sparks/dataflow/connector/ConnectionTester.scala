package sparks.dataflow.connector

import io.circe.generic.auto._
import io.circe.syntax._
import sparks.webui.base.AjaxClient
import sparks.webui.forms.EntityForm

trait ConnectionTester extends ConnectorProxyEditor with AjaxClient {

  def test(form: EntityForm) = {

    if (proxy != null) {
      form.indicator.show(Tags.label("/images/icons/progress.gif", "Testing connection..."))
      queryPlainText(
        Path("designer", "testconnection"),
        proxy.toProtocol.asJson.noSpaces,
        (msg: String) => if (msg.isEmpty) form.nextPage() else handleError(msg),
        e => handleError(e.getMessage)
      )
    }

    def handleError(msg: String) = {
      form.error.add(msg)
      form.error.show()
      form.buttons.enable()
      form.indicator.clear()
    }
  }

  override val nextHandler = () => test(form)
  override val confirmHandler = () => test(form)

}