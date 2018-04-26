package sparks.dataflow

package object implicits {

  implicit class StringBuilderExtension(sb: StringBuilder) {
    def appendLine(): StringBuilder = sb.append(System.lineSeparator())
    def appendLine(s: String): StringBuilder = sb.append(s).appendLine()
  }


}
