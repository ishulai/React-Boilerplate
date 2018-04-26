package sparks.dataflow.protocols.processmodel

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import sparks.dataflow.protocols.processmodel.ActivityState.ActivityState

import scala.collection.mutable
import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox

class RunningContext(val process: Process) {

  lazy private val sysconf = ConfigFactory.load()
  lazy private val appName = sysconf.getString("sparkNode.appName")
  lazy private val master = sysconf.getString("sparkNode.master")

  lazy val conf = new SparkConf(true).setAppName(appName).setMaster(master)
  lazy val spark = SparkSession.builder.config(conf).getOrCreate()
  lazy val toolBox = universe.runtimeMirror(spark.getClass.getClassLoader).mkToolBox()

  val dataFrames = mutable.Map[Int, DataFrame]()
  val statistics = mutable.Map[Int, ActivityStatistic]()

  process.activities.foreach(a => statistics(a.id) = ActivityStatistic.fromActivity(a))

  def eval[T](code: String): T = toolBox.eval(toolBox.parse(code)).asInstanceOf[T]

  import CodeType._

  def getCode(name: String, `type`: CodeType) = process.codes.find(c => c.`type` == `type` && c.name == name).orNull
  def codeExists(name: String, `type`: CodeType) = process.codes.exists(c => c.`type` == `type` && c.name == name)

  private val _registeredCodes = mutable.Map[String, Code]()
  def registerCode(name: String, `type`: CodeType) = {
    if (!_registeredCodes.contains(name)) {
      val code = getCode(name, `type`)
      if (code != null) code.register(this)
    }
  }

  def changeState(activity: Activity, newState: ActivityState) = {
    val stat = statistics(activity.id)
    if (stat != null) statistics(activity.id) = stat.changeState(newState)
  }

  def getViewName(activity: Activity) = "%s_%s".format(process.id.toString.replace("-", "_"), activity.id)

  def cachePreviews() =
    dataFrames.values.foreach(df => {
      df.head(50)
      df.cache()
    })

  def toInstance = ProcessInstance(
    process.id,
    statistics.map(s => s._1 -> ActivityInstance(s._1, s._2.view)).toMap
  )
}