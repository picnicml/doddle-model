package io.picnicml.doddlemodel.cluster

import breeze.linalg.functions.euclideanDistance
import cats.syntax.option._
import io.picnicml.doddlemodel.data.Features
import io.picnicml.doddlemodel.typeclasses.Clusterer

/** An immutable DBSCAN model.
  *
  * @param eps: the maximum distance between points in a group
  * @param minSamples: the minimum number of point in a core group
  *
  * Examples:
  * val model = DBSCAN()
  * val model = DBSCAN(eps = 1.5)
  * val model = DBSCAN(minSamples = 3)
  * val model = DBSCAN(eps = 2.0, minSamples = 3)
  */
case class DBSCAN private(eps: Double, minSamples: Int, private val label: Option[Array[Int]])

object DBSCAN {

  def apply(eps: Double = 1.0, minSamples: Int = 1): DBSCAN = {
    require(eps > 0.0, "Maximum distance needs to be larger than 0")
    require(minSamples > 0, "Minimum number of samples needs to be larger than 0")
    DBSCAN(eps, minSamples, none)
  }

  implicit lazy val ev: Clusterer[DBSCAN] = new Clusterer[DBSCAN] {

    override def isFitted(model: DBSCAN): Boolean = model.label.isDefined

    override protected def labelSafe(model: DBSCAN): Array[Int] = model.label.get

    override protected def copy(model: DBSCAN): DBSCAN =
      model.copy()

    override protected def copy(model: DBSCAN, label: Array[Int]): DBSCAN =
      model.copy(label = label.some)

    override protected def fitSafe(model: DBSCAN, x: Features): DBSCAN = {
      val xSize = x.rows
      val label = Array.fill[Int](xSize)(Int.MaxValue)
      var groupStartId = 0
      var groupId = 0
      while (groupStartId < xSize) {
        var groupQueue = Set(groupStartId)
        var groupCount = 1
        label(groupStartId) = groupId
        while (groupQueue.size > 0) {
          val tmpGroupQueue = groupQueue
          groupQueue = Set[Int]()
          for (pointId <- tmpGroupQueue; i <- 0 until xSize
            if label(i) > groupId &&
              euclideanDistance(x(i, ::).t, x(pointId, ::).t) <= model.eps) {
            label(i) = groupId
            groupQueue += i
            groupCount += 1
          }
        }
        if (groupCount < model.minSamples) {
          for (i <- 0 until xSize if label(i) == groupId) {
            label(i) = -1
          }
          groupId -= 1
        }
        groupStartId = findUnidentifiedPoint(label)
        groupId += 1
      }
      copy(model, label)
    }

    override protected def fitPredictSafe(model: DBSCAN, x: Features): Array[Int] = {
      labelSafe(fitSafe(model, x))
    }

    private def findUnidentifiedPoint(label: Array[Int]): Int = {
      var i = 0
      while (i < label.length && label(i) < Int.MaxValue) {
        i += 1
      }
      i
    }
  }
}
