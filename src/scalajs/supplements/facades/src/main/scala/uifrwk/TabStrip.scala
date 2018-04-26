package uifrwk

import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation._

object TabStrip {

  @js.native
  @JSGlobal("tabStrip")
  class TabStrip protected() extends UxObject {
    def this(container: js.Any) = this()

    @JSName("_container")
    var container: HTMLElement = js.native
    var tabs: js.Array[Tab] = js.native
    var tabWidth: String = js.native
    var tabPosition: String = js.native
    var element: HTMLElement = js.native
    var onclose: js.Function1[Tab, _] = js.native
    var onselect: js.Function2[Tab, Tab, _] = js.native

    def addTab(caption: String): Tab = js.native
    def removeTab(tab: Tab): Unit = js.native
    def findTab(caption: String): Tab = js.native
    def getSelectedTab(): Tab = js.native
    def isEmpty(): Boolean = js.native
    def nonEmpty(): Boolean = js.native
    def render(): TabStrip = js.native
    def execPostOps(): TabStrip = js.native
  }

  @js.native
  @JSGlobal("tab")
  class Tab protected() extends js.Object {
    def this(caption: String) = this()

    var caption: String = js.native
    var selected: Boolean = js.native
    var element: HTMLElement = js.native
    var owner: TabStrip = js.native
    var userData: js.Any = js.native

    def setCaption(s: String): Tab = js.native
    def select(): Tab = js.native
    def unselect(): Tab = js.native
    def remove(): Unit = js.native
  }

}