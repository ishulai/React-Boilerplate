package sparks.dataflow.proxy

import uifrwk.PopupMenu._
import MenuFactory._
import sparks.dataflow.protocols.Component.Component
import sparks.dataflow.protocols.processmodel.ActivityContext
import sparks.dataflow.prototypes.GraphicalModel.Vertex
import sparks.webui.entity.Entity
import sparks.webui.forms.EntityDialog

trait BaseProxy {

  val spec: Component

  private var _node: Vertex = _
  def node: Vertex = _node
  def node_=(n: Vertex) = if (n != _node) _node = n

  lazy val menuItems: Seq[MenuObject] = Seq(
    Items.local("", "Edit...", (item: MenuItem)=>edit()),
    Items.local("table.png", "View result", (item: MenuItem) => if (showResult != null) showResult()),
    Items.local("", "Analyze...", (item: MenuItem)=>{}),
    Items.local("playsmall.png"," Run to here", (item: MenuItem) => if (runToHere != null) runToHere()),
    MenuSeperator(),
    Items.copy,
    Items.cut,
    Items.paste,
    Items.seperator,
    Items.delete
  )

  var runToHere: ()=>_ = _
  var showResult: ()=>_ = _
  var editCompleted: ()=>_ = _

  def editor: Entity = ???

  def edit() = {
    val dialog = EntityDialog(editor)
    if (editCompleted != null) dialog.ok = editCompleted
    dialog.show()
  }

  def validate(): Boolean

  def toProtocol: ActivityContext
}
