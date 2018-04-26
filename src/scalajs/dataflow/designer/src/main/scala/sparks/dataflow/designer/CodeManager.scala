package sparks.dataflow.designer

import sparks.dataflow.implicits._
import uifrwk.TabStrip.Tab
import uifrwk.TreeNode
import sparks.dataflow.protocols.processmodel
import sparks.dataflow.protocols.processmodel.CodeType

import scala.collection.mutable.ListBuffer
import scala.scalajs.js

object CodeManager {

  import CodeType._

  class CodeManager {

    private val _codes: ListBuffer[Code] = ListBuffer()
    def codes: ListBuffer[Code] = _codes

    def dirty = _codes.exists(_.dirty)

    def clearUIElements() = _codes.foreach(_.clearUIElements())

    def saveAll() = _codes.foreach(_.save())

    def addFunction() = {
      val c = Code(getAvailableName(CodeType.Function), CodeType.Function)
      c.body = StringBuilder
        .newBuilder
        .appendLine(s"def ${c.name}: (paramType=>returnType) = value => {")
        .appendLine("\t// Write your code here")
        .appendLine("\t// For example: value.split(',')")
        .appendLine("}")
        .toString
      c.tempBody = c.body
      _codes += c
      c
    }

    def addAggregation() = {
      val c = Code(getAvailableName(CodeType.Aggregation), CodeType.Aggregation)
      c.body = StringBuilder
        .newBuilder
        .appendLine("import org.apache.spark.sql.expressions.MutableAggregationBuffer")
        .appendLine("import org.apache.spark.sql.expressions.UserDefinedAggregateFunction")
        .appendLine("import org.apache.spark.sql.types._")
        .appendLine("import org.apache.spark.sql.Row")
        .appendLine("import org.apache.spark.sql.SparkSession")
        .appendLine("//Please refer to https://spark.apache.org/docs/latest/sql-programming-guide.html#aggregations")
        .appendLine(s"object ${c.name} extends UserDefinedAggregateFunction {")
        .appendLine("\t// Data types of input arguments of this aggregate function")
        .appendLine("\tdef inputSchema: StructType = ???")
        .appendLine("\t// Data types of values in the aggregation buffer")
        .appendLine("\tdef bufferSchema: StructType = ???")
        .appendLine("\t// The data type of the returned value")
        .appendLine("\tdef dataType: DataType = ???")
        .appendLine("\t// Whether this function always returns the same output on the identical input")
        .appendLine("\tdef deterministic: Boolean = true")
        .appendLine("\t// Initializes the given aggregation buffer. The buffer itself is a `Row` that in addition to")
        .appendLine("\t// standard methods like retrieving a value at an index (e.g., get(), getBoolean()), provides")
        .appendLine("\t// the opportunity to update its values. Note that arrays and maps inside the buffer are still")
        .appendLine("\t// immutable.")
        .appendLine("\tdef initialize(buffer: MutableAggregationBuffer): Unit = {}")
        .appendLine("\t// Updates the given aggregation buffer `buffer` with new input data from `input`")
        .appendLine("\tdef update(buffer: MutableAggregationBuffer, input: Row): Unit = {}")
        .appendLine("\t// Merges two aggregation buffers and stores the updated buffer values back to `buffer1`")
        .appendLine("\tdef merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {}")
        .appendLine("\t// Calculates the final result")
        .appendLine("\tdef evaluate(buffer: Row): ??? = ???")
        .appendLine("}")
        .toString
      c.tempBody = c.body
      _codes += c
      c
    }

    def find(name: String): Code = _codes.find(_.name == name).orNull
    def exists(name: String) = _codes.exists(_.name == name)

    def toProtocol = _codes.map(_.toProtocol)


    private def getAvailableName(`type`: CodeType) = {
      var i = 1
      val prefix = `type`.toString.toLowerCase
      while (exists(s"$prefix$i")) i = i + 1
      s"$prefix$i"
    }
  }

  import CodeType._

  class Code {

    var name: String = ""
    var description: String = ""
    var body: String = ""
    var tempBody: String = ""
    var `type`: CodeType = CodeType.Function

    private var _dirty: Boolean = false
    def dirty: Boolean = _dirty
    def dirty_=(v: Boolean) = {
      _dirty = v
      tab.setCaption(s"$name${if (_dirty) "*" else ""}")
    }

    var tab: Tab = _

    def icon = s"${if (`type` == CodeType.Function) "func" else "agg"}.png"

    private var _node: TreeNode = _
    def node: TreeNode = {
      if (_node == null) _node = TreeNode(name, icon, checkbox = true, this.asInstanceOf[js.Any])
      _node
    }
    def node_=(v: TreeNode) = _node = v

    def findName(s: String) = {
      val key = if (`type` == CodeType.Function) "def" else "object"
      val line = s.split(System.lineSeparator).find(_.trim.startsWith(key)).orNull
      if (line != null) {
        val words = line.split("\\s|\\(|:")
        val keyIndex = words.indexOf(key)
        if (words.lengthCompare(keyIndex + 1) >= 0) words(keyIndex + 1) else ""
      }
      else ""
    }

    def save(): Unit =
      if (dirty) {
        body = tempBody
        val newName = findName(body)
        if (newName != name) {
          name = newName
          if (_node != null) _node.setText(name)
          if (tab != null) tab.setCaption(name)
        }
        dirty = false
      }

    def clearUIElements() = {
      _node = null
      tab = null
      tempBody = ""
      _dirty = false
    }

    import sparks.dataflow.protocols.processmodel
    def toProtocol = processmodel.Code(name, body, `type`)
  }

  object Code {
    def apply(name: String, `type`: CodeType) = {
      val c = new Code()
      c.name = name
      c.`type` = `type`
      c
    }
  }

}
