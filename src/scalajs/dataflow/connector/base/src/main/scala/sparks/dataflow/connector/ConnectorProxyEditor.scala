package sparks.dataflow.connector

import sparks.webui.base.DomFunction
import sparks.webui.entity.{FieldSpec, FieldType, Entity}
import sparks.webui.entity.validators.CustomValidator
import sparks.dataflow.connector.controls._

trait ConnectorProxyEditor extends Entity with DomFunction {

  val proxy: ConnectorProxy
  val autoGenerateUI = true
  val userDefinedUI = null

  def fill(): Unit
  def collect(): Unit

  protected def FileUploaderFieldSpec(
    name: String,
    label: String,
    uploader: FileUploader,
    validationRule: () => Boolean = null
  ) = FieldSpec(
    name,
    label,
    FieldType.Custom,
    customRenderer = (spec: FieldSpec) => {
      uploader.render()
    },
    validator = if (validationRule != null) CustomValidator(_ => validationRule()) else null
  )
}
