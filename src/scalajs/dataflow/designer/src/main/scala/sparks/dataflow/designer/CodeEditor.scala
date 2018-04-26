package sparks.dataflow.designer

import ace.{Ace, Editor}
import org.scalajs.dom.raw.HTMLElement
import sparks.dataflow.designer.CodeManager._
import sparks.webui.base.{AjaxClient, BaseControl}
import sparks.webui.entity.Entity
import uifrwk.ClientToolbar.{Button, ClientToolbar, Seperator}
import uifrwk.Layout.{Layout, Partition, PartitionSplitter}
import uifrwk.TabStrip.{Tab, TabStrip}
import uifrwk.{Tree, TreeNode}

import scala.scalajs.js

class CodeEditor(cm: CodeManager) extends DesignerEntity with BaseControl {

  val title = "Code editor"
  val autoGenerateUI = false
  override val fixedUIWidth = 1024
  override val fixedUIHeight = 800
  override val requiresButtons = false

  private var currentCode: Code = _
  private var switching = false

  private var codeList: Tree = _
  private var tabs: TabStrip = _
  private var editor: Editor = _
  private var editorPanel: Partition = _
  private val layout: Layout =
    Layout(
      createContainer(),
      isVertical = true,
      init = null,
      Partition(
        "toolbar",
        "32px",
        backgroundColor = "#eeeeee",
        content = ClientToolbar(
          Button("newfunc.png", "New function", () => addFunction()),
          Button("newagg.png", "New aggregation", () => addAggregation()),
          Button("delete2.png", "Delete", () => deleteFunctions()),
          Seperator(),
          Button("save.png", "Save", () => save()),
          Button("saveall.png", "Save all", () => cm.saveAll()),
          Seperator(),
          Button("debug.png", "Validate", () => validateCode())
        )
      ),
      Partition(
        "context",
        size = "480px",
        content = Layout(
          Partition(
            "nav",
            "25%",
            content = {
              import js.JSConverters._
              codeList = Tree()
              codeList
                .addNodes(cm.codes.map(_.node).toJSArray)
                .onNodeDblclick = (n: TreeNode) => if (currentCode != n.code) openCode(n.code)
              codeList
            }
          ),
          PartitionSplitter(),
          Partition(
            "editor",
            content = Layout(
              isVertical = true,
              Partition(
                "tabstrip",
                "20px",
                content = {
                  tabs = new TabStrip()
                  tabs.onclose = (tab: Tab) => {
                    if (tab.code.dirty)
                      messageBox.ask(
                        "Want to save your changes?",
                        yes = () => tab.close(true),
                        no = () => tab.close()
                      )
                    else tab.close()
                  }
                  tabs.onselect = (last: Tab, curr: Tab) => {
                    if (last != null && last.code != null) last.code.tempBody = editor.getValue()
                    if (curr != null && curr.code != null)
                      try {
                        switching = true
                        editor.setValue(curr.code.tempBody)
                        editor.clearSelection()
                        currentCode = curr.code
                      }
                      finally switching = false
                  }
                  tabs
                },
                postOp = (p: Partition) => {
                  p.element.style.overflow = "hidden"
                }
              ),
              Partition(
                "editor",
                postOp = (p: Partition) => {
                  editor = Ace.edit(p.element)
                  editor.setTheme("ace/theme/tomorrow")
                  editor.session.setMode("ace/mode/scala")
                  editor.on("change", (_: Any) => if (!switching && currentCode != null) currentCode.dirty = true)
                }
              )
            ),
            postOp = (p: Partition) => editorPanel = p
          )
        )
      )
    )

  def userDefinedUI: HTMLElement = {
    layout.render().execPostOps()
    hideEditor()
    layout.container
  }

  private def hideEditor() = if (editorPanel != null) editorPanel.hide()
  private def showEditor() = if (editorPanel != null) editorPanel.show()

  private def addFunction(): Unit = {
    val c = cm.addFunction()
    codeList.addNode(c.node)
    openCode(c)
  }

  private def addAggregation(): Unit = {
    val c = cm.addAggregation()
    codeList.addNode(c.node)
    openCode(c)
  }

  private def deleteFunctions(): Unit =
    codeList
      .getCheckedNodes()
      .foreach(n => {
        if (n.code != null) {
          if (n.code.tab != null) n.code.tab.close()
          cm.codes -= n.code
        }
        n.remove()
      })

  private def validateCode(): Unit = {
    if (currentCode != null) {
      queryPlainText(
        Path("processmodel", "validatecode"),
        editor.getValue(), // todo: Temp, it only validates the code currently in the editor.
        (msg: String) => if (msg.nonEmpty) messageBox.showError(msg),
        e => messageBox.showError(e.getMessage)
      )
    }
  }

  private def openCode(code: Code): Unit =
    try {
      switching = true
      showEditor()
      if (code.tab != null)
        code.tab.select()
      else {
        code.tab = tabs.addTab(code.name).select()
        code.tab.userData = any(code)
        if (code.tempBody.isEmpty) code.tempBody = code.body
        editor.setValue(code.tempBody)
        editor.clearSelection()
        editor.moveCursorTo(0, 0)
      }
      currentCode = code
    }
    finally switching = false

  def save() =
    if (currentCode != null && currentCode.dirty) {
      currentCode.tempBody = editor.getValue()
      currentCode.save()
    }

  def fill() = {}
  def collect() = {}

  implicit class TabExtension(tab: Tab) {
    def code = tab.userData.asInstanceOf[Code]
    def close(save: Boolean = false) = {
      if (save) tab.code.save()
      if (tab.code != null) {
        tab.code.dirty = false
        tab.code.tab = null
      }
      tab.remove()
      if (tabs.isEmpty()) {
        currentCode = null
        hideEditor()
      }
    }
  }

  implicit class TreeNodeExtension(node: TreeNode) {
    def code = node.userData.asInstanceOf[Code]
  }

}
