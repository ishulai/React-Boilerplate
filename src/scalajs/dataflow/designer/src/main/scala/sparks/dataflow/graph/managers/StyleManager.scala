package sparks.dataflow.graph.managers

import mxGraph.MxConstants
import sparks.dataflow.graph.Diagram

import scala.scalajs.js

class StyleManager(diagram: Diagram) extends Manager(diagram) {

  val fontSize = 13

  graph.getStylesheet().getDefaultEdgeStyle()("edgeStyle") = js.Dynamic.global.mxEdgeStyle.EntityRelation

  private val vs = graph.getStylesheet().getDefaultVertexStyle()

  vs(MxConstants.STYLE_SHAPE) = "image"
  vs(MxConstants.STYLE_FOLDABLE) = "0"
  vs(MxConstants.STYLE_EDITABLE) = "0"
  vs(MxConstants.STYLE_VERTICAL_ALIGN) = "top"
  vs(MxConstants.STYLE_VERTICAL_LABEL_POSITION) = "bottom"
  vs(MxConstants.STYLE_FONTFAMILY) = "Segoe UI;sans-serif" // todo: Temp
  vs(MxConstants.STYLE_FONTSIZE) = fontSize
  vs(MxConstants.STYLE_FONTCOLOR) = "#333333"
}

object StyleManager {
  def apply(diagram: Diagram) = new StyleManager(diagram)
}