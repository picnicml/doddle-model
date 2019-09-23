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
case class DBSCAN private(eps: Double, minSamples: Int, private val labels: Option[Array[Int]])

object DBSCAN {

  val NOISE: Int = -1
  val UNASSIGNED: Int = Int.MaxValue

  def apply(eps: Double = 1.0, minSamples: Int = 1): DBSCAN = {
    require(eps > 0.0, "Maximum distance needs to be larger than 0")
    require(minSamples > 0, "Minimum number of samples needs to be larger than 0")
    DBSCAN(eps, minSamples, none)
  }

  implicit lazy val ev: Clusterer[DBSCAN] = new Clusterer[DBSCAN] {

    override def isFitted(model: DBSCAN): Boolean = model.labels.isDefined

    override protected def labelSafe(model: DBSCAN): Array[Int] = model.labels.get

    override protected def copy(model: DBSCAN): DBSCAN =
      model.copy()

    override protected def fitSafe(model: DBSCAN, x: Features): DBSCAN = {
      val xSize = x.rows
      val distanceMap = (0 until xSize - 1).flatMap { i1 =>
        (i1 + 1 until xSize).map { i2 =>
          (i1, i2) -> euclideanDistance(x(i1, ::).t, x(i2, ::).t)
        }
      }.toMap
      val labels = Array.fill[Int](xSize)(UNASSIGNED)
      var groupId = 0
      for (pointId <- 0 until xSize if labels(pointId) == UNASSIGNED) {
        var groupQueue = findNeighbors(pointId, distanceMap, xSize, model.eps)
        if (groupQueue.size + 1 < model.minSamples) {
          labels(pointId) = NOISE
        } else {
          labels(pointId) = groupId
          while (groupQueue.nonEmpty) {
            val tmpGroupQueue = groupQueue
            groupQueue = Set[Int]()
            tmpGroupQueue.foreach { i =>
              if (labels(i) == NOISE) labels(i) = groupId
              else if (labels(i) == UNASSIGNED) {
                labels(i) = groupId
                val neighbors = findNeighbors(i, distanceMap, xSize, model.eps)
                if (neighbors.size + 1 >= model.minSamples)
                  groupQueue ++= neighbors
              }
            }
          }
          groupId += 1
        }
      }
      model.copy(labels = labels.some)
    }

    private def findNeighbors(
      pointId: Int,
      distanceMap: Map[(Int, Int), Double],
      xSize: Int,
      eps: Double
    ): Set[Int] = {
      def findDistance(i1: Int, i2: Int): Double = distanceMap(
        if (i1 < i2) (i1, i2) else (i2, i1)
      )
      (0 until xSize).filter { i =>
        i != pointId && findDistance(i, pointId) <= eps
      }.toSet
    }
  }
}
