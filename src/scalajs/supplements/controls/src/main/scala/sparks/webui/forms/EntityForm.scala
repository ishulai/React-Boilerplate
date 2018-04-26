package sparks.webui.forms

import org.scalajs.dom.raw._
import sparks.webui.entity.Entity

import scala.collection.mutable.ListBuffer

class EntityForm(editors: Entity*) extends Form {

  val prefix = s"ef_${System.nanoTime()}_"

  var entities = editors.toArray

  if (entities == null || entities.isEmpty) throw new Exception("Entities cannot be empty or null.")

  entities.foreach { entity => if (entity != null) entity.form = this }

  protected var currIndex: Int = 0

  protected def current: Entity = entities(currIndex)

  def content = generateUI(current)

  var ok: () => _ = _
  var next: (Int) => _ = _
  var cancel: () => _ = _
  var redraw: () => _ = _
  var resize: () => _ = _

  def uniqueId(ctrlId: String) = s"${prefix}_$ctrlId"

  def valueOf(ctrlId: String): String = {

    val uid = uniqueId(ctrlId)
    val c = ?(uid)
    if (c == null && isRadioButton(uid))
      getRadioButtonValue(uid)
    else
      c match {
        case input: HTMLInputElement if input.`type` == "checkbox" => input.checked.toString
        case _ => c.value.toString
      }
  }

  def setValue(ctrlId: String, value: Any): Unit = {
    val uid = uniqueId(ctrlId)
    val c = ?(uid)
    if (c == null && isRadioButton(uid))
      setRadioButtonValue(uid, value.toString)
    else
      c match {
        case input: HTMLInputElement if input.`type` == "checkbox" => input.checked = value.asInstanceOf[Boolean]
        case _ => c.value = value.toString
      }
  }

  def getEntity[T >: Null](index: Int): T = {
    if (index < entities.length)
      entities(index).asInstanceOf[T]
    else
      null
  }

  object error {
    private var errorCell: HTMLTableCellElement = _
    private val errorMessages: ListBuffer[String] = ListBuffer[String]()

    var clearHandler: () => _ = _

    def add(msg: String) = errorMessages += msg

    def clear() = {
      if (clearHandler != null) clearHandler()

      if (errorMessages.nonEmpty) {
        if (resize != null) resize()
        errorMessages.clear()
      }

      if (errorCell != null) {
        errorCell.textContent = ""
        errorCell.parentElement.style.display = "none"
      }
    }

    def show() = {
      if (errorCell != null && errorMessages.nonEmpty) {
        val list = Tags.ul(errorMessages)
        list.style.margin = "5px"
        list.style.paddingLeft = "20px"
        errorCell.appendChild(list)
        errorCell.parentElement.style.display = "table-row"
        if (resize != null) resize()
      }
    }

    def inject(container: HTMLElement) = {
      var row = Tags.singleCellRow
      errorCell = row.cells(0).asInstanceOf[HTMLTableCellElement]
      row.style.display = "none"
      errorCell.colSpan = 2
      errorCell.style.border = "solid 1px #ff8080"
      errorCell.style.backgroundColor = "lightYellow"
      container.appendChild(row)
    }
  }

  object buttons {
    private val minWidth: Int = 70
    private val buttons: ListBuffer[HTMLButtonElement] = ListBuffer[HTMLButtonElement]()

    def add(parentElement: HTMLElement, button: HTMLButtonElement) = {
      parentElement.appendChild(button)
      buttons += button
    }

    def enable() = buttons.foreach(_.disabled = false)

    def disable() = buttons.foreach(_.disabled = true)

    def clear() = buttons.clear()

    def fit() = buttons.foreach(btn => if (btn.clientWidth < minWidth) btn.style.width = s"${minWidth}px")
  }

  object indicator {
    private var cell: HTMLTableCellElement = _

    def inject(container: HTMLElement): Unit = {
      var row = Tags.singleCellRow
      cell = row.cells(0).asInstanceOf[HTMLTableCellElement]
      row.style.display = "none"
      cell.colSpan = 2
      container.appendChild(row)
    }

    def show(msg: HTMLElement) = {
      cell.textContent = ""
      cell.appendChild(msg)
      cell.parentElement.style.display = "table-row"
    }

    def clear() = {
      cell.textContent = ""
      cell.parentElement.style.display = "none"
    }
  }

  private def generateUI(entity: Entity): HTMLElement = {

    val container = Tags.table
    container.style.width = entity.containerWidth
    container.style.borderCollapse = "collapse"

    error.inject(container)

    val row = Tags.singleCellRow
    container.appendChild(row)

    val cell = row.cells(0).asInstanceOf[HTMLTableCellElement]
    cell.colSpan = 2
    cell.appendChild(
      if (entity.autoGenerateUI) {
        UIGenerator(this, entity).run()
      } else {
        entity.userDefinedUI
      }
    )

    indicator.inject(container)

    if (current.requiresButtons) {
      container.appendChild(Tags.spacerRow)
      container.appendChild(commandRow)
    }

    container
  }

  private def commandRow: HTMLTableRowElement = {

    buttons.clear()

    val r = Tags.tr(2)

    val left = r.cells(0).asInstanceOf[HTMLTableCellElement]
    left.style.width = "50%"
    left.style.textAlign = "left"

    val right = r.cells(1).asInstanceOf[HTMLTableCellElement]
    right.style.width = "50%"
    right.style.textAlign = "right"

    if (entities.length == 1) {
      buttons.add(left, okButton)
    }
    else if (current.isBackAllowed) {
      if (currIndex > 0) buttons.add(left, backButton)
      buttons.add(right, if (currIndex < entities.length - 1) nextButton else okButton)
    }
    else
      buttons.add(left, if (currIndex < entities.length - 1) nextButton else okButton)

    def nextButton: HTMLButtonElement = {
      val btn = Tags.button("Next")
      btn.onclick = (e: MouseEvent) => {
        error.clear()
        buttons.disable()
        if (current.validate()) {
          current.collect()
          if (current.nextHandler != null)
            current.nextHandler()
          else
            nextPage()
        }
        else buttons.enable()
      }
      btn
    }

    def backButton: HTMLButtonElement = {
      val btn = Tags.button("Back")
      btn.onclick = (e: MouseEvent) => {
        error.clear()
        currIndex -= 1
        error.clear()
        if (redraw != null) {
          redraw()
          current.fill()
        }
      }
      btn
    }

    def okButton: HTMLButtonElement = {
      val btn = Tags.button(if (entities.length == 1) "Ok" else "Finish")
      btn.onclick = (e: MouseEvent) => {
        try {
          error.clear()
          buttons.disable()
          if (current.validate()) {
            current.collect()
            if (current.confirmHandler != null)
              current.confirmHandler()
            else
              confirm()
          }
          else buttons.enable()
        }
        catch {
          case e: Exception => {
            error.add(e.getMessage)
            buttons.enable()
          }
        }
        error.show()
      }
      btn
    }

    r
  }

  def nextPage() = {
    if (next != null) next(currIndex)
    currIndex += 1
    current.form = this
    error.clear()
    if (redraw != null) {
      redraw()
      current.fill()
    }
  }

  def confirm() = {
    close()
    if (ok != null) ok()
  }

  override def close() = if (onclose != null) onclose()

}
