package io.picnicml.doddlemodel.cluster

import breeze.linalg.DenseMatrix
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.cluster.DBSCAN.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class DBSCANTest extends FlatSpec with Matchers with TestingUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  "DBSCAN" should "calculate the label of the data points" in {
    val x = DenseMatrix((1.0, 1.0), (0.0, 2.0), (2.0, 0.0),
      (8.0, 1.0), (7.0, 2.0), (9.0, 0.0))

    val model = DBSCAN(eps = 3.0, min_samples = 1)
    ev.fit_predict(model, x) shouldEqual Array(0, 0, 0, 1, 1, 1)
  }

  it should "prevent the usage of negative eps" in {
    an [IllegalArgumentException] shouldBe thrownBy(DBSCAN(eps = -0.5))
  }
}
