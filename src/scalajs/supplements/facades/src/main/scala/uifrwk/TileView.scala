package uifrwk

import org.scalajs.dom.raw.HTMLImageElement

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("tileView")
class TileView protected() extends js.Object {
  def this(container: js.Any) = this()

  @JSName("_items")
  var tiles: js.Array[Tile] = js.native
  var imgPath: String = js.native
  var tileWidth: Int = js.native
  var iconSizeLimit: Int = js.native
  var tileStyle: Int = js.native
  var isSelectable: Boolean = js.native
  var isCompact: Boolean = js.native
  var iconFontSize: String = js.native

  def addTile(caption: String, subCaption: String, userData: js.Any, icon: String): Tile = js.native
  def addTile(id: String, caption: String, subCaption: String, userData: js.Any, icon: String): Tile = js.native
  def deleteTile(tile: Tile): Unit = js.native
  def findTile(id: String): Tile = js.native
  def getSelectedItem(): Tile = js.native
  def getSelectedItems(): js.Array[Tile] = js.native
  def deleteSelectedItems(): Unit = js.native
  def render(): Unit = js.native
  def clear(): Unit = js.native
  def redraw(): Unit = js.native
}

object TileView {
  def apply(container: js.Any) = new TileView(container)
}

@js.native
@JSGlobal("tile")
class Tile protected() extends js.Object {
  def this(caption: String, subCaption: String, userData: js.Any, icon: String) = this()

  var id: String = js.native
  var userData: js.Any = js.native
  var iconImgElement: HTMLImageElement = js.native
  var view: TileView = js.native
  @JSName("_caption")
  var caption: String = js.native
  @JSName("_subCaption")
  var subCaption: String = js.native

  def getSelected(): Boolean = js.native
  def setSelected(selected: Boolean): Unit = js.native

  def getIndex(): Int = js.native
}

