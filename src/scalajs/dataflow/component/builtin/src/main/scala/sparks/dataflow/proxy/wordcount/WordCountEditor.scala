package sparks.dataflow.proxy.wordcount


  import sparks.dataflow.protocols.Auxiliaries.Field
  import sparks.dataflow.proxy.ProxyEditor
  import sparks.webui.entity.{FieldSpec, FieldType}

  case class WordCountEditor(proxy: WordCountProxy) extends ProxyEditor {

    val title = "Word Count"
    override val fixedUIWidth = 480

    if (proxy.node.schema == null) proxy.node.schema = proxy.node.inputSchema.unselectAll

    override val fieldSpecs = Seq(
      FieldSpec("stopWords", "Stop Words", FieldType.TextArea, validator = null)
    )

    override def fill() ={
      super.fill()
      form.setValue("stopWords", proxy.stopWords)
    }

    override def collect() = {
      super.collect()
      proxy.stopWords = form.valueOf("stopWords").toString

      proxy.node.schema = proxy.node.schema.unselectAll
      proxy.node.outPort.schema =
        proxy.node.schema.unselectAll ++ Seq( Field("word", "String"), Field("count", "Int"))
    }
  }
