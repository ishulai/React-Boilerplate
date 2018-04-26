package uifrwk

import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation._

@ScalaJSDefined
trait UxObject extends js.Object {

  @JSName("_container")
  var container: HTMLElement

  def render(): UxObject
  def execPostOps(): UxObject

}
