package sparks.dataflow.connector.json

import org.scalajs.dom.raw._
import sparks.dataflow.connector.ConnectionTester
import sparks.dataflow.connector.controls.FileUploader
import sparks.webui.entity.validators.CustomValidator
import sparks.webui.entity.{FieldSpec, FieldType}


case class JSONEditor(proxy: JSONProxy) extends ConnectionTester {

  val title = "JSON File"
  override val fixedUIWidth = 400

  private val fileUploader = new FileUploader()

  override val fieldSpecs = Seq(
    FieldSpec("loc", "Remote file", FieldType.RadioButton, userData = "remote"),
    FieldSpec(
      "url",
      "",
      FieldType.Text,
      validator = CustomValidator(v => if (form.valueOf("loc") == "remote") v.nonEmpty else true)
    ),
    FieldSpec("loc", "Upload", FieldType.RadioButton, userData = "local"),
    FileUploaderFieldSpec("upload", "", fileUploader)
  )

  override def fill() = {
  }

  override def collect() = {
    proxy.filename = fileUploader.filename
    proxy.isRemote = form.valueOf("loc") == "remote"
    proxy.url = form.valueOf("url")
    proxy.filepath = fileUploader.upload()
  }

}