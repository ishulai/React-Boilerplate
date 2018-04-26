package sparks.dataflow.designer

import sparks.dataflow.designer.CodeManager._
import sparks.dataflow.designer.Toolbox._
import sparks.dataflow.graph.Diagram
import sparks.webui.forms.{BaseDesigner, EntityDialog}
import uifrwk.ClientToolbar._
import uifrwk.Layout._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("flowDesigner")
object DataFlowDesigner extends BaseDesigner with js.JSApp {

  var canvas: Canvas = _
  def diagram: Diagram = if (canvas != null) canvas.diagram else null

  private var _console: Console = _
  def console: Console = _console

  private var _toolbox: Toolbox = _
  def toolbox: Toolbox = _toolbox

  private var _dataSources: SourceManager = _
  def dataSources: SourceManager = _dataSources

  private val _library: CodeManager = new CodeManager()
  def library: CodeManager = _library

  private var layout: Layout = _

  @JSExport def main() = {
    layout = Layout(
      createContainer(parent = body),
      true,
      (layout: Layout) => {
        layout.cellPadding = 2
        layout.cellSpacing = 1
      },
      Partition(
        "toolbar",
        "32px",
        content = ClientToolbar(
          (bar: ClientToolbar) => bar.useBackgroundColor = true,
          Button("save.png", "Save", () => {}),
          Button("saveas2.png", "Save as...", () => {}),
          Button("close.png", "Close", () => {}),
          Seperator(),
          Button("settings2.png", "Settings", () => {}),
          Button("play.png", "Run", () => canvas.diagram.run()),
          Seperator(),
          Button("toolpane.png", "Switch Tool Panel", () => {}),
          Button("zoomin.png", "Zoom In", () => {}),
          Button("zoomout.png", "Zoom Out", () => {}),
          Seperator(),
          Button("undo.png", "Undo", () => {}),
          Button("redo.png", "Redo", () => {}),
          Seperator(),
          Button("code.png", "Code Studio", () => editCodes()),
          Button("callout.png", "Toggle descriptions", () => canvas.diagram.toggleDescriptions()),
          Button("callout2.png", "Toggle statistics", () => canvas.diagram.toggleStatistics())
        )
      ),
      Partition(
        id = "context",
        size = null,
        content = Layout(
          Partition(
            "toolbox",
            "20%",
            postOp = (p: Partition) => {
              _toolbox = new Toolbox(this, p.element)
              _dataSources = new SourceManager(this)
            }),
          Partition(
            "canvas",
            postOp = (p: Partition) => canvas = new Canvas(this, p.element, "")
          )
        )
      ),
      PartitionSplitter(),
      Partition(
        id = "console",
        size = "30%",
        postOp = (p: Partition) => _console = new Console(this, p.element)
      )
    ).render().execPostOps()

    hideConsole()
  }

  def editCodes() = {
    val editor = new CodeEditor(_library)
    val dlg = EntityDialog(editor)
    dlg
      .show()
      .onclose = () => {
        if (_library.dirty) {
          messageBox.ask(
            "Want to save your changes?",
            yes = () => {
              editor.save()
              _library.saveAll()
              _library.clearUIElements()
              dlg.close()
            },
            no = () => {
              _library.clearUIElements()
              dlg.close()
            }
          )
          false
        }
        else {
          _library.clearUIElements()
          true
        }
      }
  }

  def hideConsole() = layout.hidePartition("console")
  def showConsole() = layout.showPartition("console")
  def clearConsole() = _console.display("")
  def isConsoleVisible = {
    val p = layout.findPartition("console")
    if (p != null) p.visible else false
  }
}
