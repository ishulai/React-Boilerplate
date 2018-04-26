package sparks.dataflow.protocols

import sparks.dataflow.protocols.processmodel.RunningContext

package object userscript {

  object ScriptLanguage extends Enumeration {
    type ScriptLanguage = Value
    val Scala, Java, Python = Value
  }

  trait UserScriptInterface {
    def run(rc: RunningContext): Unit
  }

}
