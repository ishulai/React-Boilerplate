package sparks.dataflow.proxy.topic

import sparks.dataflow.protocols.Auxiliaries.Field
import sparks.dataflow.proxy.ProxyEditor
import sparks.webui.entity.validators.RangeValidator
import sparks.webui.entity.{FieldSpec, FieldType}

case class TopicEditor(proxy: TopicProxy) extends ProxyEditor {

  val title = "Topic Modeling"
  override val fixedUIWidth = 480

  if (proxy.node.schema == null) proxy.node.schema = proxy.node.inputSchema.unselectAll

  override val fieldSpecs = Seq(
    FieldSpec("numberOfTopics", "Number of Topics", FieldType.Text, validator = RangeValidator(2, 99)),
    FieldSpec("stopWords", "Stop Words", FieldType.TextArea, validator = null)
  )

  override def fill() ={
    super.fill()
    form.setValue("stopWords", proxy.stopWords)
    form.setValue("numberOfTopics", proxy.numberOfTopics.toString)
  }

  override def collect() = {
    super.collect()
    proxy.stopWords = form.valueOf("stopWords").toString
    proxy.numberOfTopics = form.valueOf("numberOfTopics").toInt

    proxy.node.schema = proxy.node.schema.unselectAll
    proxy.node.outPort.schema =
      proxy.node.schema.unselectAll ++ Seq( Field("topics", "String"))
  }
}
