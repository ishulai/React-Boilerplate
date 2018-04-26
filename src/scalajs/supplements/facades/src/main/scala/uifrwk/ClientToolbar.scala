package uifrwk

import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation._

object ClientToolbar {

  @js.native
  @JSGlobal("clientToolbar")
  class ClientToolbar protected() extends UxObject {
    def this(container: js.Any) = this()

    @JSName("_container")
    var container: HTMLElement = js.native
    var imgPath: String = js.native
    var itemCellSize: Int = js.native
    var itemImgSize: Int = js.native
    var itemCellHeight: Int = js.native
    var useBackgroundColor: Boolean = js.native

    def addItem(item: ToolbarItem): ToolbarItem = js.native
    def addButton(img: String, hint: String, action: js.Function0[Unit]): ToolbarItem = js.native
    def addSeperator(): ToolbarItem = js.native
    def render(): ClientToolbar = js.native
    def execPostOps(): UxObject = js.native
  }

  object ClientToolbar {
    def apply(container: js.Any, init: (ClientToolbar) => _, items: ToolbarItem*) = {
      val toolbar = new ClientToolbar(container)
      if (init != null) init(toolbar)
      items.foreach(toolbar.addItem)
      toolbar
    }

    def apply(init: (ClientToolbar) => _, items: ToolbarItem*) = {
      val toolbar = new ClientToolbar(null)
      if (init != null) init(toolbar)
      items.foreach(toolbar.addItem)
      toolbar
    }

    def apply(items: ToolbarItem*) = {
      val toolbar = new ClientToolbar(null)
      items.foreach(toolbar.addItem)
      toolbar
    }
  }

  @ScalaJSDefined
  trait ToolbarItem extends js.Object

  @js.native
  @JSGlobal("toolbarButton")
  class Button protected() extends ToolbarItem {
    def this(img: String, hint: String, action: js.Function0[Unit]) = this()
  }

  object Button {
    def apply(img: String, hint: String, action: js.Function0[Unit]) = new Button(img, hint, action)
  }

  @js.native
  @JSGlobal("toolbarSeperator")
  class Seperator protected() extends ToolbarItem

  object Seperator {
    def apply() = new Seperator()
  }

}