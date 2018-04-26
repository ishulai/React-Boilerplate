package sparks.dataflow.proxy.topic

import io.circe.syntax._
import sparks.dataflow.protocols.Component.Component
import sparks.dataflow.protocols.processmodel.ActivityContext
import sparks.dataflow.protocols.topic.TopicContext
import sparks.dataflow.proxy.BaseProxy

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("sparks.dataflow.proxy.topic.TopicProxy")
case class TopicProxy(spec: Component) extends BaseProxy {

  var stopWords: String = """a, about, above, after, again, against, all, am, an, and, any, are, arent, as, at, be, because, been, before, being, below, between, both, but, by,
                            can, cant, come, could, couldnt,d, did, didn, do, does, doesnt, doing, dont, down, during, each, few, finally, for, from, further, had, hadnt, has, hasnt, have, havent, having, he, her, here, hers, herself, him, himself, his, how,
                            i, if, in, into, is, isnt, it, its, itself, just, ll, m, me, might, more, most, must, my, myself, no, nor, not, now, o, of, off, on, once, only, or, other, our, ours, ourselves, out, over, own,
                            r, re, s, said, same, she, should, shouldnt, so, some, such, t, than, that, thats, the, their, theirs, them, themselves, then, there, these, they, this, those, through, to, too,
                            under, until, up, very, was, wasnt, we, were, werent, what, when, where, which, while, who, whom, why, will, with, wont, would, y, yo, your, yours, yourself, yourselves"""


  var numberOfTopics : Int = 12


  override def editor = TopicEditor(this)

  def validate() = node.inputSchema != null && node.isValid

  def toProtocol: ActivityContext =
    ActivityContext(
      spec.jarDesc,
      TopicContext(stopWords, numberOfTopics).asJson.noSpaces
    )
}
