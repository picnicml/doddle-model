package io.picnicml.doddlemodel.cluster

import breeze.linalg.functions.euclideanDistance
import cats.syntax.option._
import io.picnicml.doddlemodel.data.Features
import io.picnicml.doddlemodel.typeclasses.Clusterer

/** An immutable DBSCAN model.
  *
  * @param eps: the maximum distance between points in a group
  * @param min_samples: the minimum number of point in a core group
  *
  * Examples:
  * val model = DBSCAN()
  * val model = DBSCAN(eps = 1.5)
  * val model = DBSCAN(min_samples = 3)
  * val model = DBSCAN(eps = 2.0, min_samples = 3)
  */
case class DBSCAN private(eps: Double, min_samples: Int,
  private val label: Option[Array[Int]]) {}

object DBSCAN {

  def apply(): DBSCAN = DBSCAN(1.0, 1, none)

  def apply(eps: Double): DBSCAN = {
    require(eps > 0, "maximum distance need to be larger than 0")
    DBSCAN(eps, 1, none)
  }
  def apply(min_samples: Int): DBSCAN = {
    require(min_samples > 0,
      "minimum number of points in a group need to be larger than 0")
    DBSCAN(1.0, min_samples, none)
  }
  def apply(eps: Double, min_samples: Int): DBSCAN = {
    require(eps > 0, "maximum distance need to be larger than 0")
    require(min_samples > 0,
      "minimum number of points in a group need to be larger than 0")
    DBSCAN(eps, min_samples, none)
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
              euclideanDistance(x(i, ::).t, x(pointId, ::).t) < model.eps) {
            label(i) = groupId
            groupQueue += i
            groupCount += 1
          }
        }
        if (groupCount < model.min_samples) {
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
