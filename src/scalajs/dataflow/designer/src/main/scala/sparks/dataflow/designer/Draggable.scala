package sparks.dataflow.designer

import mxGraph._
import org.scalajs.dom.raw.HTMLElement
import sparks.dataflow.graph.Node
import sparks.dataflow.protocols.Component.{Component, PortSpec}
import sparks.dataflow.proxy.BaseProxy

trait Draggable extends DesignerAuxiliary {

  // Node icon size = 32
  // Port icon size = 8
  // Unit of port position = 8(port) / 32(node) / 2(center of port) = 0.125
  private val portPositions = Array(
    Array(0.0),
    Array(0.375), // 0.5 - unit
    Array(-0.125, 0.875), // (0 - unit, 1 - unit)
    Array(-0.125, 0.375, 0.875) // ...
  )

  case class DragSource(
                         element: HTMLElement,
                         compSpec: Component,
                         caption: String = "",
                         proxy: BaseProxy = null,
                         init: (Node) => Unit = null
                       )

  protected def makeDraggable(ds: DragSource): Unit = {

    val f = (g: MxGraph, evt: MxMouseEvent, cell: MxCell, x: Double, y: Double) => {
      val parent = g.getDefaultParent()
      val model = g.getModel()

      var vertex: MxCell = null
      model.beginUpdate()
      try {

        val proxy = if (ds.proxy == null)
          newInstance[BaseProxy](ds.compSpec.interfaceDesc.jarDesc.qualifiedName)(any(ds.compSpec))
        else
          ds.proxy

        val caption = if (ds.caption.nonEmpty) ds.caption else ds.compSpec.interfaceDesc.toolboxDesc.label

        // todo: temp mage path
        vertex = g.insertVertex(
          parent, null, caption, x, y, 32, 32,
          "image=/images/icons/dataflow/" + ds.compSpec.interfaceDesc.toolboxDesc.icon + ";"
        )
        vertex.setConnectable(false)

        val node = designer.diagram.addNode(vertex, proxy)
        node.caption = caption
        proxy.editCompleted = () => {
          model.setValue(vertex, node.caption)
          proxy.node.validate()
        }

        // todo: temp mage path
        val imgPath = "/images/icons/dataflow/port/"
        val spec = ds.compSpec.interfaceDesc.vertexDesc
        addPorts(node, spec.inPorts, 0)
        addPorts(node, spec.outPorts, 1)

        // x = 0: inPorts / 1: outPorts
        def addPorts(parentNode: Node, ports: Seq[PortSpec], x: Int): Unit = {
          // Port definitions after the 3rd will be discarded
          val len = ports.length
          val pc = if (len <= 3) len else 3
          val pos = portPositions(ports.length)
          for (i <- 0 until pc) {
            val current = ports(i)
            val port = g.insertVertex(
              vertex, null, null,
              x, pos(i), 8, 8,
              s"port;image=$imgPath" + current.icon, true
            )
            port.geometry.offset = new MxPoint(if (x == 0) -10 else 2, 0)
            if (x == 0) node.addInPort(port) else node.addOutPort(port)
          }
        }

        if (ds.init != null) ds.init(node)
        node.validate()

        proxy.runToHere = () => designer.diagram.runToNode(node)
        proxy.showResult = () => designer.diagram.showNodeResult(node)
      }
      catch {
        case e: Exception => messageBox.showError(s"This component is not available. ${e.getMessage}")
      }
      finally {
        model.endUpdate()
      }

      if (vertex != null) g.setSelectionCell(vertex)

    }: Unit

    MxUtils.makeDraggable(ds.element, designer.canvas.diagram.graph, f, ds.element, 0, 0, true, true)
  }

}
