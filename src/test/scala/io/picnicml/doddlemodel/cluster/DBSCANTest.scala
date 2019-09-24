package io.picnicml.doddlemodel.cluster

import breeze.linalg.DenseMatrix
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

  "DBSCAN" should "calculate the label of the data points" in {
    val model = DBSCAN(eps = 3.0, minSamples = 1)
    ev.fitPredict(model, x) shouldEqual Array(0, 0, 0, 1, 1, 1)
    ev.labels(ev.fit(model, x)) shouldEqual Array(0, 0, 0, 1, 1, 1)
  }

  it should "cluster one data point to one group when eps is too small" in {
    val model = DBSCAN()
    ev.fitPredict(model, x) shouldEqual Array(0, 1, 2, 3, 4, 5)
  }

  it should "cluster all data points to one group when eps is too large" in {
    val model = DBSCAN(eps = 10.0)
    ev.fitPredict(model, x) shouldEqual Array(0, 0, 0, 0, 0, 0)
  }

  it should "cluster all points to outliers when min samples is too large" in {
    val model = DBSCAN(minSamples = 7)
    ev.fitPredict(model, x) shouldEqual Array(-1, -1, -1, -1, -1, -1)
  }

  it should "cluster all data points to one group when eps is equal to the distance among points" in {
    val smallX = DenseMatrix(
      List(0.0, 0.0),
      List(3.0, 0.0)
    )
    val model = DBSCAN(eps = 3.0)
    ev.fitPredict(model, smallX) shouldEqual Array(0, 0)
  }

  it should "cluster all data points to one group in an 1D array of points that match min sample size" in {
    val d1X = DenseMatrix(
      List(0.0, 12.0),
      List(0.0, 9.0),
      List(0.0, 6.0),
      List(0.0, 3.0),
      List(0.0, 0.0)
    )
    val model = DBSCAN(eps = 3.0, minSamples = 3)
    ev.fitPredict(model, d1X) shouldEqual Array(0, 0, 0, 0, 0)
  }

  it should "prevent the usage of negative eps" in {
    an [IllegalArgumentException] shouldBe thrownBy(DBSCAN(eps = -0.5))
  }

  it should "prevent the usage of negative min samples" in {
    an [IllegalArgumentException] shouldBe thrownBy(DBSCAN(minSamples = -1))
  }
}
