package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.DenseMatrix
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.preprocessing.Normalizer.ev
import io.picnicml.doddlemodel.preprocessing.Norms.{L1Norm, MaxNorm}
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class NormalizerTest extends FlatSpec with Matchers with TestingUtils {

  implicit val tolerance: Equality[Float] = TolerantNumerics.tolerantFloatEquality(1e-4f)

  "Normalizer" should "scale rows to unit norm using various norms" in {
    val x = DenseMatrix(
      List(1.0f, 2.0f, 2.0f),
      List(-1.0f, 1.0f, 0.5f),
      List(-2.0f, 0.0f, 0.0f)
    )
    val l2Normalizer = Normalizer()
    val l1Normalizer = Normalizer(L1Norm)
    val maxNormalizer = Normalizer(MaxNorm)

    breezeEqual(ev.transform(l2Normalizer, x),
      DenseMatrix(
        List(0.3333f, 0.6666f, 0.6666f),
        List(-0.6666f, 0.6666f, 0.3333f),
        List(-1.0f, 0.0f, 0.0f)
      )
    ) shouldBe true

    breezeEqual(ev.transform(l1Normalizer, x),
      DenseMatrix(
        List(0.2f, 0.4f, 0.4f),
        List(-0.4f, 0.4f, 0.2f),
        List(-1.0f, 0.0f, 0.0f)
      )
    ) shouldBe true

    breezeEqual(ev.transform(maxNormalizer, x),
      DenseMatrix(
        List(0.5f, 1.0f, 1.0f),
        List(-1.0f, 1.0f, 0.5f),
        List(-1.0f, 0.0f, 0.0f)
      )
    ) shouldBe true
  }

  it should "handle rows with zero norm" in {
    val l2Normalizer = Normalizer()
    val x = DenseMatrix(
      List(0.0f, 0.0f, 0.0f),
      List(0.0f, 3.0f, 4.0f)
    )
    val xNormalizedExpected = DenseMatrix(
      List(0.0f, 0.0f, 0.0f),
      List(0.0f, 0.6f, 0.8f)
    )

    breezeEqual(ev.transform(l2Normalizer, x), xNormalizedExpected) shouldBe true
  }
}
