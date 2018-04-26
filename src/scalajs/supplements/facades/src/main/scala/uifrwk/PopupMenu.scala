package uifrwk

import scala.scalajs.js
import scala.scalajs.js.annotation._

object PopupMenu {

  @js.native
  @JSGlobal("popupMenu")
  class PopupMenu() extends js.Object {
    var imgPath: String = js.native

    def add(item: MenuObject): MenuObject = js.native
    def addItem(icon: String, caption: String, funct: js.Function1[MenuItem, Unit]): MenuItem = js.native
    def addEntry(icon: String, caption: String): MenuEntry = js.native
    def addSeperator(): MenuSeperator = js.native
    def show(x: Double, y: Double): Unit = js.native
    def close(): Unit = js.native
  }

  object PopupMenu {
    def apply(items: MenuObject*) = {
      val menu = new PopupMenu()
      items.foreach{ item => {
        item match {
          case i: MenuItem => menu.add(i)
          case e: MenuEntry => menu.add(e)
          case s: MenuSeperator => menu.add(s)
          case _ => // Null or something else, do nothing
        }
      }}
      menu
    }
  }

  @ScalaJSDefined
  trait MenuObject extends js.Object {
    var globalId: Int
  }

  @js.native
  @JSGlobal("menuItem")
  class MenuItem protected() extends MenuObject {
    def this(icon: String, caption: String, funct: js.Function1[MenuItem, _]) = this()

    var icon: String = js.native
    var caption: String = js.native
    var funct: js.Function1[MenuItem, Unit] = js.native
    var globalId: Int = js.native
  }

  object MenuItem {
    def apply(icon: String, caption: String, funct: js.Function1[MenuItem, _]) = new MenuItem(icon, caption, funct)
    def apply(icon: String, caption: String, globalId: Int, funct: js.Function1[MenuItem, _]) = {
      val item = new MenuItem(icon, caption, funct)
      item.globalId = globalId
      item
    }
  }

  @js.native
  @JSGlobal("menuEntry")
  class MenuEntry protected() extends MenuObject {
    def this(icon: String, caption: String) = this()

    var icon: String = js.native
    var caption: String = js.native
    var menu: PopupMenu = js.native
    var globalId: Int = js.native
  }

  object MenuEntry {
    def apply(icon: String, caption: String) = new MenuEntry(icon, caption)
  }

  @js.native
  @JSGlobal("menuSeperator")
  class MenuSeperator protected() extends MenuObject {
    var globalId: Int = js.native
  }

  object MenuSeperator {
    def apply() = new MenuSeperator()
    def apply(globalId: Int) = {
      val item = new MenuSeperator()
      item.globalId = globalId
      item
    }
  }

}