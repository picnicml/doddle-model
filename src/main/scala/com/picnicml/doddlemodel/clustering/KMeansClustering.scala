package com.picnicml.doddlemodel.clustering

import java.io.Serializable

import breeze.linalg.functions.euclideanDistance
import breeze.linalg.{*, DenseMatrix, DenseVector, argmin, sum}
import breeze.stats.mean
import com.picnicml.doddlemodel.data.loadCsvDataset
import com.picnicml.doddlemodel.data.{Features, Target}

import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global
import scala.annotation.tailrec
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


@SerialVersionUID(1L)
class KMeansClustering private (val k: Int,
                                val maxIterations: Int,
                                val earlyStoppingPercentage: Double,
                                val clusterCenters: Option[DenseMatrix[Double]],
                                val labels: Option[DenseVector[Int]])
  extends RandomizableClusterer[KMeansClustering] with Serializable {

  this: Serializable =>
  /** A function that is guaranteed to be called on a fitted model. */
  override protected def predictSafe(x: Features): Target = ???

  override def isFitted: Boolean = this.clusterCenters.isDefined && this.labels.isDefined

  override def copy: KMeansClustering = new KMeansClustering(this.k, this.maxIterations, this.earlyStoppingPercentage, this.clusterCenters, this.labels)

  @tailrec
  private def step(x: Features, iter: Int, clusterCenters: DenseMatrix[Double], labels: DenseVector[Int]): (DenseMatrix[Double], DenseVector[Int]) = {
    if (iter == 0) return (clusterCenters, labels)

    val newClusterCenters = DenseMatrix.zeros[Double](this.k, x.cols)
    val newLabelsFutures = x(*, ::).toIndexedSeq map { row =>
      Future {
        argmin(clusterCenters(*, ::).map(center => euclideanDistance(row.t, center)))
      }
    }

    val newLabels: DenseVector[Int] = DenseVector[Int](Await.result(Future.sequence(newLabelsFutures), Duration.Inf).toArray)
    newLabels.toScalaVector.zipWithIndex.groupBy(_._1).foreach {
      case (labelIdx, groupedLabels) =>
        val rows = x(groupedLabels.map(_._2), ::)
        newClusterCenters(labelIdx, ::) := mean(rows(::, *))
    }

    val sameValuesCount = sum((newLabels :== labels).mapValues(b => if (b) 1 else 0))
    if (sameValuesCount / x.rows < this.earlyStoppingPercentage) {
      (newClusterCenters, newLabels)
    } else {
      step(x, iter - 1, newClusterCenters, newLabels)
    }
  }

  override def fitSafe(x: Features)(implicit rand: Random): KMeansClustering = {
    val initialClusterCenters = x((0 until k).map(_ => rand.nextInt(x.rows)), ::).toDenseMatrix
    val (clusterCenters, labels) = step(x, this.maxIterations, initialClusterCenters, DenseVector.fill[Int](x.rows){-1})
    new KMeansClustering(this.k, this.maxIterations, this.earlyStoppingPercentage, Some(clusterCenters), Some(labels))
  }
}

object KMeansClustering {

  def apply(k: Int, maxIterations: Int = 300, earlyStoppingPercentage: Double = 0.01): KMeansClustering = {
    require(k >= 2, "Number of clusters must be greater or equal to 2")
    require(maxIterations >= 1, "Number of maximum iterations must be greater or equal to 1")
    require(earlyStoppingPercentage >= 0.0 && earlyStoppingPercentage <= 1.0, "Early stopping percentage must be between 0 and 1.")
    new KMeansClustering(k, maxIterations, earlyStoppingPercentage, None, None)
  }

  def main(args: Array[String]): Unit = {
//     val kmeans = KMeansClustering(2).fit(DenseMatrix((3.0, 1.0, 5.0), (-1.0, -2.0, 5.0), (-1.3, -2.1, 5.0), (-2.0, -123.0, 5.0)), DenseVector(0.1, 0.2, 0.0, 0.5))
    val data = loadCsvDataset("/Users/rok/Downloads/mnist_train.csv", headerLine = false)
    val xTr = data(::, 1 to -1) / 255.0
    println("Starting...")
    val t0 = System.nanoTime()
    val kmeans = KMeansClustering(10).fit(xTr, DenseVector[Double]())
    val t1 = System.nanoTime() - t0
    println(t1 / Math.pow(10, 9), "seconds")

    kmeans.labels.get.toArray.zipWithIndex.groupBy(_._1).foreach {
      case (labelIdx, groupedLabels) =>
        println(labelIdx, groupedLabels.length)
    }
  }
}
