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

  private type Neighbors = mutable.Queue[Int]

  private val UNASSIGNED = Double.MaxValue
  private val NOISE = -1.0

  def apply(eps: Double = 0.5, minSamples: Int = 5): DBSCAN = {
    require(eps > 0.0, "Maximum distance eps needs to be larger than 0")
    require(minSamples > 0, "Minimum number of samples needs to be larger than 0")
    DBSCAN(eps, minSamples, none)
  }

  implicit lazy val ev: Clusterer[DBSCAN] = new Clusterer[DBSCAN] {

    override protected def copy(model: DBSCAN): DBSCAN = model.copy()

    override def isFitted(model: DBSCAN): Boolean = model.labels.isDefined

    override protected def fitSafe(model: DBSCAN, x: Features): DBSCAN = {
      val nn = NearestNeighbors(x)
      val finalState = (0 until x.rows).foldLeft(State.initial(x.rows)) { case (state, rowIdx) =>
        if (state.labels(rowIdx) == UNASSIGNED)
          handleUnassignedPoint(model, rowIdx, nn, state)
        else
          state
      }
      model.copy(labels = finalState.labels.some)
    }

    private def handleUnassignedPoint(model: DBSCAN, rowIdx: Int, nn: NearestNeighbors, s: State): State = {
      val neighbors = nn.getNeighbors(rowIdx, model.eps)
      if (neighbors.length < model.minSamples) {
        s.labels(rowIdx) = NOISE
        s
      }
      else
        expandPoint(model, rowIdx, mutable.Queue(neighbors:_*), nn, s.copy(clusterId = s.clusterId + 1))
    }

    private def expandPoint(model: DBSCAN, rowIdx: Int, neighbors: Neighbors, nn: NearestNeighbors, s: State): State = {
      s.labels(rowIdx) = s.clusterId.toDouble
      while (neighbors.nonEmpty) {
        val neighbor = neighbors.dequeue
        if (s.labels(neighbor) == NOISE)
          s.labels(neighbor) = s.clusterId.toDouble
        else if (s.labels(neighbor) == UNASSIGNED)
          neighbors.enqueueAll(processUnassignedNeighbor(model, neighbor, nn, s))
      }
      s
    }

    private def processUnassignedNeighbor(model: DBSCAN, neighbor: Int, nn: NearestNeighbors, s: State): Neighbors = {
      s.labels(neighbor) = s.clusterId.toDouble
      val neighborNeighbors = nn.getNeighbors(neighbor, model.eps)
      if (neighborNeighbors.length >= model.minSamples)
        mutable.Queue(neighborNeighbors:_*)
      else
        mutable.Queue.empty
    }

    override protected def labelsSafe(model: DBSCAN): DenseVector[Double] = model.labels.getOrBreak

    private case class State (labels: DenseVector[Double], clusterId: Int)
    private object State {
      def initial(numPoints: Int): State = State(DenseVector.fill[Double](numPoints)(UNASSIGNED), -1)
    }
  }

  // todo: implement NearestNeighbors with a kd-tree / ball-tree data structure and move into a separate file
  // distanceMatrix is a mapping from pairs of points indices (row indices in x) to their distances
  private class NearestNeighbors (val distanceMatrix: mutable.AnyRefMap[(Int, Int), Double], val numPoints: Int) {

    def getNeighbors(rowIdx: Int, eps: Double): Seq[Int] = {
      (0 until numPoints).filter { candidateIndex =>
        candidateIndex != rowIdx && getDistance(candidateIndex, rowIdx) <= eps
      }
    }

    def getDistance(rowIdx0: Int, rowIdx1: Int): Double = {
      if (rowIdx0 > rowIdx1)
        distanceMatrix((rowIdx1, rowIdx0))
      else
        distanceMatrix((rowIdx0, rowIdx1))
    }
  }

  private object NearestNeighbors {
    def apply(x: Features): NearestNeighbors = {
      val distanceMatrix = mutable.AnyRefMap[(Int, Int), Double]()
      (0 until x.rows).combinations(2).foreach { case rowIdx0 +: rowIdx1 +: IndexedSeq() =>
        distanceMatrix((rowIdx0, rowIdx1)) = euclideanDistance(x(rowIdx0, ::).t, x(rowIdx1, ::).t)
      }
      new NearestNeighbors(distanceMatrix, x.rows)
    }
  }
}
