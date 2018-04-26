package sparks.dataflow.connector.text

import sparks.dataflow.connector.ConnectionTester
import sparks.webui.entity.validators.CustomValidator
import sparks.webui.entity.{FieldSpec, FieldType}

case class TextEditor(proxy: TextProxy) extends ConnectionTester {

  val title = "Text file"
  override val fixedUIWidth = 450

  override val fieldSpecs = Seq(
    FieldSpec("loc", "Remote file", FieldType.RadioButton, userData = "remote"),
    FieldSpec(
      "url",
      "",
      FieldType.Text,
      validator = CustomValidator(v => if (form.valueOf("loc") == "remote") v.nonEmpty else true)
    ),
    FieldSpec("loc", "Upload", FieldType.RadioButton, userData = "local"),
    FieldSpec("upload", "", FieldType.UploadArea)
  )

  override def fill() = {
    form.setValue("loc", if (proxy.isRemote) "remote" else "local")
    form.setValue("url", proxy.path)
  }

  override def collect() = {
    proxy.isRemote = form.valueOf("loc") == "remote"
    proxy.path = form.valueOf("url")
  }

}
