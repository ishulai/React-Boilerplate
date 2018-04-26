package sparks.webui.controls

import org.scalajs.dom.raw.{Event, HTMLElement, HTMLTableCellElement}
import sparks.webui.base.DomFunction

/**
  * A search box with an additional cell for custom feature
  * @param container
  * @param bonusFeature
  */
case class SearchBox(
                 container: HTMLElement = null,
                 onchange: (Event)=>Unit = null,
                 bonusFeature: (HTMLTableCellElement)=>HTMLElement = null
               ) extends DomFunction {

  import Tags._
  def render() = {
    val box = table(
      t => {
        t.cellPadding = "0"
        t.cellSpacing = "0"
        t.style.width = "100%"
      },
      tr(
        td(d => {
          d.style.width = "18px"
          d.style.borderBottom = "solid 1px #bbbbbb"
          img("/images/icons/search.png")
        }),
        td(d => {
          // Do not use background image with padding, it won't work here.
          d.style.borderBottom = "solid 1px #bbbbbb"
          val box = input
          if (onchange != null) box.onchange = onchange
          box.className = "searchBox2"
          box.style.width = "100%"
          box
        }),
        td(d => if (bonusFeature != null) bonusFeature(d) else null)
      )
    )

    if (container != null) container.appendChild(box) else box
  }
}


