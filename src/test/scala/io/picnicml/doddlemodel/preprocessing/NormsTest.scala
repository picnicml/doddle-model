package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{DenseMatrix, DenseVector}
import io.picnicml.doddlemodel.TestingUtils
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class NormsTest extends FlatSpec with Matchers with TestingUtils {

  implicit val tolerance: Equality[Float] = TolerantNumerics.tolerantFloatEquality(1e-4f)

  private val x = DenseMatrix(
    List(0.0f, 0.0f, 0.0f),
    List(1.0f, 2.0f, 2.0f),
    List(-2.0f, 0.0f, 0.0f)
  )

  "Norms" should "calculate the L2 norm of each row" in {
    val xExpected = DenseVector(0.0f, 3.0f, 2.0f)
    breezeEqual(Norms.L2Norm(x), xExpected) shouldBe true
  }

  "Norms" should "calculate the L1 norm of each row" in {
    val xExpected = DenseVector(0.0f, 5.0f, 2.0f)
    breezeEqual(Norms.L1Norm(x), xExpected) shouldBe true
  }

  "Norms" should "calculate the max norm of each row" in {
    val xExpected = DenseVector(0.0f, 2.0f, 2.0f)
    breezeEqual(Norms.MaxNorm(x), xExpected) shouldBe true
  }

}
