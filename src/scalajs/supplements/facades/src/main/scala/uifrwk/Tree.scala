package uifrwk

import org.scalajs.dom.raw.{HTMLElement, MouseEvent}

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("tree")
class Tree protected() extends UxObject {
  def this(container: js.Any, width: js.Any = null, height: js.Any = null, imgPath: String = null) = this()

  @JSName("_container")
  var container: HTMLElement = js.native
  var imgPath: String = js.native
  var width: js.Any = js.native
  var height: js.Any = js.native
  var effects: js.Any = js.native
  var expandIcon: String = js.native
  var collapseIcon: String = js.native
  var loadingIcon: String = js.native
  var nodes: js.Array[TreeNode] = js.native
  var element: HTMLElement = js.native
  var onNodeSelected: js.Function1[TreeNode, Unit] = js.native
  var onNodeDblclick: js.Function1[TreeNode, Unit] = js.native
  var onDynamicLoad: js.Function1[TreeNode, Unit] = js.native
  var onContextMenu: js.Function2[MouseEvent, TreeNode, Unit] = js.native

  def addNode(node: TreeNode): TreeNode = js.native
  def addNodes(nodes: js.Array[TreeNode]): Tree = js.native
  def clear(): Unit = js.native
  def hide(): Tree = js.native
  def show(): Tree = js.native
  def render(): Tree = js.native
  def execPostOps(): Tree = js.native
  def getAllNodes(): js.Array[TreeNode] = js.native
  def getLeaves(): js.Array[TreeNode] = js.native
  def getSelectedNode(): TreeNode = js.native
  def getCheckedNodes(): js.Array[TreeNode] = js.native
  def checkNodes(names: js.Array[String]): Unit = js.native
  def expandAll(): Unit = js.native
  def collapseNodes(rule: (TreeNode)=>Boolean): Unit = js.native
  def selectNode(text: String): Unit = js.native
  def deleteEmptyNodes(): Tree = js.native
  def countChildren(): Tree = js.native

}

object Tree {

  def apply(): Tree = this.apply(null)

  def apply(
             container: js.Any,
             width: js.Any = null,
             height: js.Any = null,
             imgPath: String = null
           ): Tree = new Tree(container, width, height, imgPath)

}


@js.native
@JSGlobal("treeNode")
class TreeNode protected() extends js.Object {
  def this(text: String, icon: String, checkbox: Boolean, userData: js.Any) = this()

  var text: String = js.native
  var icon: String = js.native
  var color: String = js.native
  var level: Int = js.native
  var expanded: Boolean = js.native
  var checked: Boolean = js.native
  var selected: Boolean = js.native
  var dynamic: Boolean = js.native
  var checkbox: Boolean = js.native
  var userData: js.Any = js.native
  var parentNode: TreeNode = js.native
  var children: js.Array[TreeNode] = js.native
  var tree: Tree = js.native
  var element: HTMLElement = js.native

  def addChild(node: TreeNode): TreeNode = js.native
  def addChildren(nodes: js.Array[TreeNode]): TreeNode = js.native
  def hasChildren(): Boolean = js.native
  def setText(text: String): Unit = js.native
  def setIcon(icon: String): Unit = js.native
  def expand(): Unit = js.native
  def collapse(): Unit = js.native
  def check(value: Boolean): Unit = js.native
  def remove(): Unit = js.native
  def finalizeDynamicLoading(): Unit = js.native
  def busy(): Unit = js.native
  def idle(): Unit = js.native
  def getRoot(): TreeNode = js.native
}

object TreeNode {
  def apply(
             text: String,
             icon: String,
             checkbox: Boolean = false,
             userData: js.Any = null) = new TreeNode(text, icon, checkbox, userData)
}