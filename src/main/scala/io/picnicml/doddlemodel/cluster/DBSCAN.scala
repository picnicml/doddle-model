package io.picnicml.doddlemodel.cluster

import breeze.linalg.DenseVector
import breeze.linalg.functions.euclideanDistance
import cats.syntax.option._
import io.picnicml.doddlemodel.data.Features
import io.picnicml.doddlemodel.syntax.OptionSyntax._
import io.picnicml.doddlemodel.typeclasses.Clusterer

import scala.collection.mutable

/** An immutable DBSCAN clustering model.
  *
  * @param eps: the maximum distance between two datapoints to be considered in a common neighborhood
  * @param minSamples: the minimum number of datapoints in a neighborhood for a point to be considered the core point
  */
case class DBSCAN private (eps: Double, minSamples: Int, private val labels: Option[DenseVector[Double]])

object DBSCAN {

  def apply(eps: Double = 0.5, minSamples: Int = 5): DBSCAN = {
    require(eps > 0.0, "Maximum distance eps needs to be larger than 0")
    require(minSamples > 0, "Minimum number of samples needs to be larger than 0")
    DBSCAN(eps, minSamples, none)
  }

  implicit lazy val ev: Clusterer[DBSCAN] = new Clusterer[DBSCAN] {

    override protected def copy(model: DBSCAN): DBSCAN =
      model.copy()

    override def isFitted(model: DBSCAN): Boolean = model.labels.isDefined

    override protected def fitSafe(model: DBSCAN, x: Features): DBSCAN = {
      val distances = computeDistances(x)
      println(distances)
      ???
    }

    private def computeDistances(x: Features): Distances = {
      val distanceMatrix = mutable.AnyRefMap[(Int, Int), Double]()
      (0 until x.rows).combinations(2).foreach { case rowIndex0 +: rowIndex1 +: IndexedSeq() =>
        distanceMatrix((rowIndex0, rowIndex1)) = euclideanDistance(x(rowIndex0, ::).t, x(rowIndex1, ::).t)
      }
      new Distances(distanceMatrix)
    }

    override protected def labelsSafe(model: DBSCAN): DenseVector[Double] = model.labels.getOrBreak
  }

  private class Distances(private val distanceMatrix: mutable.AnyRefMap[(Int, Int), Double]) {
    def get(x: Int, y: Int): Double = if (x > y) distanceMatrix((y, x)) else distanceMatrix((x, y))
  }
}
