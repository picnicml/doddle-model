package io.picnicml.doddlemodel.cluster

import breeze.linalg.DenseMatrix
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.cluster.DBSCAN.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class DBSCANTest extends FlatSpec with Matchers with TestingUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  val x = DenseMatrix((1.0, 1.0), (0.0, 2.0), (2.0, 0.0),
    (8.0, 1.0), (7.0, 2.0), (9.0, 0.0))

  "DBSCAN" should "calculate the label of the data points" in {
    val model = DBSCAN(eps = 3.0, min_samples = 1)
    ev.fit_predict(model, x) shouldEqual Array(0, 0, 0, 1, 1, 1)
    ev.label(ev.fit(model, x)) shouldEqual Array(0, 0, 0, 1, 1, 1)
  }

  it should "cluster one data point to one group when eps is too small" in {
    val model = DBSCAN()
    ev.fit_predict(model, x) shouldEqual Array(0, 1, 2, 3, 4, 5)
  }

  it should "cluster all data points to one group when eps is too large" in {
    val model = DBSCAN(eps = 10.0)
    ev.fit_predict(model, x) shouldEqual Array(0, 0, 0, 0, 0, 0)
  }

  it should "cluster all points to outliers when min samples is too large" in {
    val model = DBSCAN(min_samples = 7)
    ev.fit_predict(model, x) shouldEqual Array(-1, -1, -1, -1, -1, -1)
  }

  it should "prevent the usage of negative eps" in {
    an [IllegalArgumentException] shouldBe thrownBy(DBSCAN(eps = -0.5))
  }

  it should "prevent the usage of negative min samples" in {
    an [IllegalArgumentException] shouldBe thrownBy(DBSCAN(min_samples = -1))
  }
}
