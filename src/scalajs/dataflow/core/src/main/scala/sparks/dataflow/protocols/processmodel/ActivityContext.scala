package sparks.dataflow.protocols.processmodel

import java.lang.reflect.InvocationTargetException

import sparks.dataflow.protocols.Component.JarSpec
import sparks.dataflow.protocols.DynamicClassProxy

case class ActivityContext(jar: JarSpec, context: String) {

  private def invoke(methodName: String, args: Object*) = {
    var dcp: DynamicClassProxy = null
    try {
      dcp = new DynamicClassProxy(jar.classPath, jar.qualifiedName)
      val obj = dcp.invoke("fromJson", classOf[String])(context)
      if (obj != null) {
        val gsMethod = obj.getClass.getMethod(methodName, args.map(a => a.getClass):_*)
        gsMethod.invoke(obj, args:_*)
      }
      else
        throw new Exception("Unable to access %s".format(dcp.className))
    }
    finally {
      if (dcp != null) dcp.close()
    }
  }

  def run(activity: Activity, rc: RunningContext) = {
    val t = System.nanoTime()
    try {
      rc.changeState(activity, ActivityState.Running)
      val obj = invoke("run", activity, rc)
      val viewName = rc.getViewName(activity)
      val thisDF = rc.dataFrames(activity.id)

      /**
        * The call to cache indicates that the contents of the DF should be stored in memory the next time it's
        * comupted. The call to count computes the dDF initially and triggers the cache.
        */
      thisDF.cache()
      val count = thisDF.count()
      thisDF.createOrReplaceTempView(viewName)

      rc.statistics(activity.id) = ActivityStatistic(
        activity.id,
        ActivityState.Complete,
        System.nanoTime() - t,
        0, count, "", viewName
      )
      obj
    }
    catch {
      case ite: InvocationTargetException =>
        val err = {
          val cause = ite.getCause
          if (cause != null) cause.getMessage else ite.getMessage
        }
        rc.statistics(activity.id) = ActivityStatistic(
          activity.id,
          ActivityState.Failed,
          System.nanoTime() - t,
          0, 0, err, ""
        )
      case e: Exception =>
        rc.statistics(activity.id) = ActivityStatistic(
        activity.id,
        ActivityState.Failed,
        System.nanoTime() - t,
        0, 0, e.getMessage, ""
      )
    }
  }
}

object ActivityContext {
  def empty = ActivityContext(JarSpec.empty, "")
}