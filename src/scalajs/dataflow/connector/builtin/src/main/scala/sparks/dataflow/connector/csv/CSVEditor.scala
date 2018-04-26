package sparks.dataflow.connector.csv

import org.scalajs.dom.raw._
import sparks.dataflow.connector.ConnectionTester
import sparks.dataflow.connector.controls.FileUploader
import sparks.webui.entity.validators.CustomValidator
import sparks.webui.entity.{FieldSpec, FieldType}


case class CSVEditor(proxy: CSVProxy) extends ConnectionTester {
  val title = "CSV File"
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
    FileUploaderFieldSpec("upload", "", fileUploader),
    FieldSpec("sep", "Sets the single character as a separator for each field and value.", FieldType.Text, validator = null),
    FieldSpec("encoding", "Decodes the CSV files by the given encoding type.", FieldType.Text, validator = null),
    FieldSpec("quote", "Sets the single character used for escaping quoted values where the separator can be part of the value.", FieldType.Text, validator = null),
    FieldSpec("header", "Uses the first line as names of columns.", FieldType.Checkbox, validator = null),
    FieldSpec("inferSchema", "Infers the input schema automatically from data.", FieldType.Checkbox, validator = null) ,
    FieldSpec("timestampFormat", "Sets the string that indicates a timestamp format."
      , FieldType.Select
      , userData = Seq(("MM/dd/yyyy","MM/dd/yyyy"),("dd MMMM yyyy","dd MMMM yyyy"),("M/d/yy", "M/d/yy"), ("yyyy-MM-dd","yyyy-MM-dd")),
      validator = null)
  )

  override def fill() = {
    form.setValue("sep", proxy.sep)
    form.setValue("encoding", proxy.encoding)
    form.setValue("quote", proxy.quote)
    form.setValue("header", proxy.header.toString)
    form.setValue("inferSchema", proxy.inferSchema.toString)
    form.setValue("timestampFormat", proxy.timestampFormat)
  }

  override def collect() = {
    proxy.filename = fileUploader.filename
    proxy.isRemote = form.valueOf("loc") == "remote"
    proxy.url = form.valueOf("url")
    proxy.filepath = fileUploader.upload()

    proxy.sep = form.valueOf("sep")
    proxy.encoding = form.valueOf("encoding")
    proxy.quote = form.valueOf("quote")
    proxy.header = form.valueOf("header").toBoolean
    proxy.inferSchema = form.valueOf("inferSchema").toBoolean
    proxy.timestampFormat = form.valueOf("timestampFormat")
  }
}