package io.picnicml.doddlemodel.cluster

import breeze.linalg.{DenseMatrix, DenseVector}
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.cluster.DBSCAN.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class DBSCANTest extends FlatSpec with Matchers with TestingUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  private val x = DenseMatrix(
    List(1.0, 1.0),
    List(0.0, 2.0),
    List(2.0, 0.0),
    List(8.0, 1.0),
    List(7.0, 2.0),
    List(9.0, 0.0)
  )

  "DBSCAN" should "cluster the datapoints" in {
    val model = DBSCAN(eps = 3.0, minSamples = 1)
    breezeEqual(ev.fitPredict(model, x), DenseVector(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)) shouldEqual true
  }

  it should "cluster each datapoint into it's own group when eps is too small" in {
    val model = DBSCAN()
    breezeEqual(ev.fitPredict(model, x), DenseVector(0.0, 1.0, 2.0, 3.0, 4.0, 5.0)) shouldEqual true
  }

  it should "cluster all data points into a single group when eps is too large" in {
    val model = DBSCAN(eps = 10.0)
    breezeEqual(ev.fitPredict(model, x), DenseVector(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)) shouldEqual true
  }

  it should "label all points as outliers when min samples is too large" in {
    val model = DBSCAN(minSamples = 7)
    breezeEqual(ev.fitPredict(model, x), DenseVector(-1.0, -1.0, -1.0, -1.0, -1.0, -1.0)) shouldEqual true
  }

  it should "cluster all datapoints into a single group when eps equals the distance between points" in {
    val smallX = DenseMatrix(
      List(0.0, 0.0),
      List(3.0, 0.0)
    )
    val model = DBSCAN(eps = 3.0)
    breezeEqual(ev.fitPredict(model, smallX), DenseVector(0.0, 0.0)) shouldEqual true
  }

  it should "cluster all datapoints into a single group" in {
    val d1X = DenseMatrix(
      List(0.0, 12.0),
      List(0.0, 9.0),
      List(0.0, 6.0),
      List(0.0, 3.0),
      List(0.0, 0.0)
    )
    val model = DBSCAN(eps = 3.0, minSamples = 3)
    breezeEqual(ev.fitPredict(model, d1X), DenseVector(0.0, 0.0, 0.0, 0.0, 0.0)) shouldEqual true
  }

  it should "prevent the usage of negative eps" in {
    an [IllegalArgumentException] shouldBe thrownBy(DBSCAN(eps = -0.5))
  }

  it should "prevent the usage of negative min samples" in {
    an [IllegalArgumentException] shouldBe thrownBy(DBSCAN(minSamples = -1))
  }
}
