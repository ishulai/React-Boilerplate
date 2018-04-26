package sparks.dataflow.protocols.processmodel

import sparks.dataflow.protocols.processmodel.ActivityState._

case class ActivityStatistic(
                              id: Int,
                              state: ActivityState,
                              duration: Long,
                              progress: Double,
                              resultSize: Long,
                              message: String,
                              view: String
                            ) {
  def changeState(newState: ActivityState) = ActivityStatistic(id, newState, duration, progress, resultSize, message, view)

  def color: String = state match {
    case ActivityState.Complete => "green"
    case ActivityState.Failed => "red"
    case _ => ""
  }

  def label = state match {
    case ActivityState.Complete => f"duration=${duration / 1000000000.0}%1.2f sec\nnum of rows=$resultSize"
    case ActivityState.Failed => message
    case _ => ""
  }

}

object ActivityStatistic {
  def fromActivity(activity: Activity) = ActivityStatistic(activity.id, ActivityState.Idle, 0, 0, 0, "", "")
}