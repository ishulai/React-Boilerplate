package uifrwk

import org.scalajs.dom.raw.{HTMLElement, HTMLTableRowElement}

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("simpleList")
class SimpleList protected() extends UxObject {
  def this(container: js.Any) = this()

  @JSName("_container")
  var container: HTMLElement = js.native
  @JSName("_items")
  var items: js.Array[SimpleListItem] = js.native
  var fontSize: String = js.native
  var onItemClick: js.Function1[SimpleListItem, Unit] = js.native

  def addItem(text: String, userData: js.Any): SimpleListItem = js.native
  def render(): SimpleList = js.native
  def execPostOps(): SimpleList = js.native
}

object SimpleList {
  def apply(container: js.Any) = new SimpleList(container)
}

@js.native
@JSGlobal("simpleListItem")
class SimpleListItem protected() extends js.Object {
  def this(text: String, userData: js.Any) = this()

  var userData: js.Any = js.native
  var element: HTMLElement = js.native
  def getSelected(): Boolean = js.native
  def setSelected(v: Boolean): Unit = js.native
}