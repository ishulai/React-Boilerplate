package sparks.dataflow.protocols

package object split {

  object SplitBy extends Enumeration {
    type SplitBy = Value
    val Default, RegEx, Delimiters = Value
  }

}
