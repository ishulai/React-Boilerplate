package sparks.webui.forms

import sparks.webui.base.BaseControl

class Form extends BaseControl {

  var onclose: () => _ = _

  def close(): Unit = ???
}
