package sparks.dataflow.proxy.join

import sparks.dataflow.implicits._
import sparks.dataflow.protocols.Auxiliaries.{Field, Schema}
import sparks.dataflow.protocols.join.JoinType
import sparks.dataflow.proxy.ProxyEditor
import sparks.dataflow.proxy.controls.SchemaGrid._
import sparks.webui.base.AjaxClient
import sparks.webui.entity.validators.CustomValidator
import sparks.webui.entity.{FieldSpec, FieldType}

case class JoinEditor(proxy: JoinProxy) extends ProxyEditor with AjaxClient {

  if (proxy.node.schema == null) {
    val left = proxy.leftSchema.fields
    val right = proxy.rightSchema.fields.map(f => {
      val alias = if (left.exists(_.name == f.name)) f.name + "_1" else ""
      Field(f.name, f.dataType, alias, "", f.nullable, left = false)
    })
    proxy.node.schema = Schema(left ++ right)
  }

  private val mapper: KeyMapper = KeyMapper(proxy)
  private val grid = SchemaGrid(proxy.node.schema, GridSwitches(aggfunc = false))

  val title = "Join"
  override val fixedUIWidth = 640
  override val fieldSpecs = Seq(
    FieldSpec(
      "joinType",
      "Join type",
      FieldType.Select,
      userData = Seq(
        ("Inner join", "Inner"),
        ("Left outer join", "LeftOuter"),
        ("Right outer join", "RightOuter"),
        ("Outer join", "Outer"),
        ("Corss join", "Cross")
      )
    ),
    FieldSpec(
      "keys",
      "Key mappings",
      FieldType.Custom,
      customRenderer = (spec: FieldSpec) =>
        Tags.div(d => {
          d.style.height = "100px"
          d.style.width = "100%"
          if (proxy.keys == null) proxy.initKeys()
          mapper.render()
        }),
      validator = CustomValidator(_ => mapper.isValid)
    ),
    SchemaGridFieldSpec("output", "Output schema", grid, validationRule = () => grid.isValid)
  )

  override def fill() = {
    super.fill()
    form.setValue("joinType", proxy.joinType.toString)
  }

  override def collect() = {
    super.collect()
    proxy.joinType = JoinType.withName(form.valueOf("joinType"))
    proxy.node.schema = grid.collect()

    proxy.keys.clear()
    proxy.keys ++= mapper.collect()

    proxy.node.outPort.schema = proxy.node.schema.finalSchema
  }

  /*
  def interface(parent: HTMLElement): HTMLElement = {

    if (proxy.keys == null) proxy.initKeys()

    import Tags._
    div(d => {
      table(
        tr(
          td(
            table(
              tr(
                td(img("/images/icons/dataflow/join_inner.png")),
                td(img("/images/icons/dataflow/join_left_outer.png")),
                td(img("/images/icons/dataflow/join_right_outer.png")),
                td(img("/images/icons/dataflow/join_full_outer.png")),
                td(img("/images/icons/dataflow/join_cross.png"))
              ),
              tr(
                td(radioButton("Inner join", "inner", "join")),
                td(radioButton("Left outer join", "leftouter", "join")),
                td(radioButton("Right outer join", "rightouter", "join")),
                td(radioButton("Outer join", "outer", "join")),
                td(radioButton("Cross join", "cross", "join"))
              )
            )
          )
        ),
        tr(td(c => {
          c.style.height = "150px"
          mapper.render()
        })),
        tr(td(c => {
          c.style.height = "250px"
          grid.render()
        }))
      )
    })
  }*/
}
