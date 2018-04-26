package uifrwk

import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation._

object Layout {

  @js.native
  @JSGlobal("layout")
  class Layout protected() extends UxObject {
    def this(container: js.Any, isVertical: Boolean = false, width: js.Any = null, height: js.Any = null) = this()

    @JSName("_container")
    var container: HTMLElement = js.native
    var cellPadding: Int = js.native
    var cellSpacing: Int = js.native
    var autoFill: Boolean = js.native
    var isVertical: Boolean = js.native
    var partitions: js.Array[Partition] = js.native
    var element: HTMLElement = js.native

    def addItem(item: LayoutItem): LayoutItem = js.native
    def addPartition(p: Partition): Partition = js.native
    def addSplitter(): Splitter = js.native
    def clearPartition(pIndex: Int): Unit = js.native
    def clearPartitions(): Unit = js.native
    def findPartition(id: String): Partition = js.native
    def showPartition(id: String): Partition = js.native
    def hidePartition(id: String): Partition = js.native
    def render(): Layout = js.native
    def execPostOps(): Layout = js.native
  }

  object Layout {

    def apply(partitions: LayoutItem*): Layout =
      apply(null, false, null, null, null, partitions: _*)

    def apply(isVertical: Boolean, partitions: LayoutItem*): Layout =
      apply(null, isVertical, null, null, null, partitions: _*)

    def apply(container: js.Any, isVertical: Boolean, init: (Layout) => _, partitions: LayoutItem*): Layout =
      apply(container, isVertical, width = null, height = null, init, partitions: _*)

    def apply(container: js.Any, isVertical: Boolean = false, width: js.Any = null, height: js.Any = null): Layout =
      apply(container, isVertical, width, height, null)

    def apply(
               container: js.Any,
               isVertical: Boolean,
               width: js.Any,
               height: js.Any,
               init: (Layout) => _,
               partitions: LayoutItem*
             ): Layout = {
      val layout = new Layout(container, isVertical, width, height)
      if (init != null) init(layout)
      partitions.foreach(layout.addItem)
      layout
    }
  }

  @ScalaJSDefined
  trait LayoutItem extends js.Object

  @js.native
  @JSGlobal("partition")
  class Partition protected() extends LayoutItem {
    def this(
              id: String,
              size: js.Any = null,
              content: UxObject,
              postOp: js.Function1[Partition, _],
              backgroundColor: String,
              shadow: Boolean,
              margin: String
            ) = this()

    var id: String = js.native
    var size: js.Any = js.native
    var content: UxObject = js.native
    var element: HTMLElement = js.native
    var visible: Boolean = js.native
    var backgroundColor: String = js.native
    var shadow: Boolean = js.native
    var margin: String = js.native
    var layout: Layout = js.native

    def clear(): Unit = js.native
    def show(): Unit = js.native
    def hide(): Unit = js.native
  }

  object Partition {
    def apply(
               id: String = "",
               size: js.Any = null,
               content: UxObject = null,
               postOp: js.Function1[Partition, _] = null,
               backgroundColor: String = null,
               shadow: Boolean = false,
               margin: String = null
             ): Partition = new Partition(id, size, content, postOp, backgroundColor, shadow, margin)
  }

  @js.native
  @JSGlobal("partitionSplitter")
  class PartitionSplitter protected() extends LayoutItem

  object PartitionSplitter {
    def apply(): PartitionSplitter = new PartitionSplitter()
  }

}