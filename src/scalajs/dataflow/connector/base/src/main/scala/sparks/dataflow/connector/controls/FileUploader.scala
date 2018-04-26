package sparks.dataflow.connector.controls

import org.scalajs.dom.raw._
import sparks.webui.base.DomFunction

class FileUploader extends DomFunction {
  val u = Tags.upload("uploadFile")

  def filename: String = {
    u.value.split("""(\\|\/)""").last
  }

  def render(): HTMLElement = {
    Tags.div(d => {
      d.style.textAlign = "center"
      d.style.lineHeight = "100px"
      d.style.height = "100px"
      d.style.border = "dashed 2px lightsteelblue"
      d.textContent = "Drop file here to upload or "
      u
    })
  }

  def upload(): String = {
    var formData = new FormData()
    formData.append("userfile", u.files(0))
    formData.append("filename", filename)
    var request = new XMLHttpRequest()
    //request.open("POST", "/designer/uploadfile", false)
    request.open("POST", "/spark/designer/uploadfile", false)
    request.send(formData)
    request.responseText
  }
}