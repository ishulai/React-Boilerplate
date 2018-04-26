package sparks.dataflow.proxy

import uifrwk.PopupMenu._

import scala.scalajs.js

object MenuFactory {

  var copy: js.Function1[MenuItem, _] = _
  var cut: js.Function1[MenuItem, _] = _
  var paste: js.Function1[MenuItem, _] = _
  var delete: js.Function1[MenuItem, _] = _

  object GlobalIds {
    val copy = 1
    val cut = 2
    val paste = 3
    val delete = 4

    val seperator = 1048575
    val localItem = 1048576
  }

  object Items {
    // todo: Implement copy, cut, paste and delete
    def copy = MenuItem("copy.png", "Copy", GlobalIds.copy, MenuFactory.copy)
    def cut = MenuItem("cut.png", "Cut", GlobalIds.cut, MenuFactory.cut)
    def paste = MenuItem("paste.png", "Paste", GlobalIds.paste, MenuFactory.paste)
    def delete = MenuItem("delete.png", "Delete", GlobalIds.delete, MenuFactory.delete)
    def local(icon: String, caption: String, f: js.Function1[MenuItem, _]) = MenuItem(icon, caption, GlobalIds.localItem, f)
    def seperator = MenuSeperator(GlobalIds.seperator)

    def join(contexts: Seq[BaseProxy]): PopupMenu = {
      if (contexts.lengthCompare(1) == 0)
        PopupMenu(contexts.head.menuItems:_*)
      else
        PopupMenu(copy, cut, paste, seperator, delete)
    }
  }

}