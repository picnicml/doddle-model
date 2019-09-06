package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{DenseMatrix, DenseVector}
import io.picnicml.doddlemodel.TestingUtils
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class NormsTest extends FlatSpec with Matchers with TestingUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  private val x = DenseMatrix(
    List(0.0, 0.0, 0.0),
    List(1.0, 2.0, 2.0),
    List(-2.0, 0.0, 0.0)
  )

  "Norms" should "calculate the L2 norm of each row" in {
    val xExpected = DenseVector(0.0, 3.0, 2.0)
    breezeEqual(Norms.L2Norm(x), xExpected) shouldBe true
  }

  "Norms" should "calculate the L1 norm of each row" in {
    val xExpected = DenseVector(0.0, 5.0, 2.0)
    breezeEqual(Norms.L1Norm(x), xExpected) shouldBe true
  }

  "Norms" should "calculate the max norm of each row" in {
    val xExpected = DenseVector(0.0, 2.0, 2.0)
    breezeEqual(Norms.MaxNorm(x), xExpected) shouldBe true
  }

}
