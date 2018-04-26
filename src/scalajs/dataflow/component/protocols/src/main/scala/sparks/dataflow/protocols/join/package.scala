package sparks.dataflow.protocols

package object join {

  object JoinOperator extends Enumeration {
    type JoinOperator = Value
    val EqualTo = Value("=")
    val LessThan = Value("<")
    val LessThanOrEqualTo = Value("<=")
    val GreaterThan = Value(">")
    val GreaterThanOrEqualTo = Value(">=")
  }

  object JoinType extends Enumeration {
    type JoinType = Value
    val Inner, LeftOuter, RightOuter, Outer, Cross = Value
  }

  import JoinOperator._
  case class KeyMapping(left: String, leftAlias: String, right: String, rightAlias: String, operator: JoinOperator) {

    def leftName = if (leftAlias.nonEmpty) leftAlias else left

     def rightName = if (rightAlias.nonEmpty) rightAlias else right

    def isEmpty = left.isEmpty && right.isEmpty

    override def toString =
      "%s %s %s".format(
        if (leftAlias.nonEmpty) leftAlias else left,
        operator.toString,
        if (rightAlias.nonEmpty) rightAlias else right
      )
  }

  object KeyMapping {
    def empty = KeyMapping("", "", "", "", JoinOperator.EqualTo)
  }

}
