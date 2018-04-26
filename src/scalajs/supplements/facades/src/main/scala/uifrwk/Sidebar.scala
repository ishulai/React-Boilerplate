package uifrwk

import org.scalajs.dom.raw.{HTMLElement, HTMLTableRowElement}

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("sidebar")
class Sidebar protected() extends js.Object {
  def this(container: js.Any, width: js.Any = null, height: js.Any = null) = this()

  var sections: js.Array[SidebarSection] = js.native
  var element: HTMLElement = js.native

  def addSection(section: SidebarSection): SidebarSection = js.native
  def addSections(sections: js.Array[SidebarSection]): SidebarSection = js.native

  def hide(): Unit = js.native
  def show(): Unit = js.native
  def render(): Unit = js.native
}

@js.native
@JSGlobal("sidebarSection")
class SidebarSection protected() extends js.Object {
  def this(caption: String, contentPane: HTMLElement = null) = this()

  var caption: String = js.native
  var contentPane: HTMLElement = js.native
  var sidebar: Sidebar = js.native
  var titleRow: HTMLTableRowElement = js.native
  var contentRow: HTMLTableRowElement = js.native
  var expanded: Boolean = js.native

  def setContentPane(pane: HTMLElement): Unit = js.native

  def expand(): Unit = js.native

  def collapse(): Unit = js.native
}