package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.DenseMatrix
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}
import io.picnicml.doddlemodel.preprocessing.Normalizer.ev
import io.picnicml.doddlemodel.preprocessing.Norms.{L1Norm, MaxNorm}
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class NormalizerTest extends FlatSpec with Matchers with TestingUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  "Normalizer" should "scale rows to unit norm using various norms" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, CategoricalFeature, NumericalFeature))
    val x = DenseMatrix(
      List(1.0, 2.0, 2.0),
      List(-1.0, 1.0, 0.5),
      List(-2.0, 0.0, 0.0)
    )
    val l2Normalizer = Normalizer(featureIndex = featureIndex)
    val l1Normalizer = Normalizer(L1Norm, featureIndex)
    val maxNormalizer = Normalizer(MaxNorm, featureIndex)

    breezeEqual(ev.transform(l2Normalizer, x),
      DenseMatrix(
        List(0.3333, 0.6666, 0.6666),
        List(-0.6666, 0.6666, 0.3333),
        List(-1.0, 0.0, 0.0)
      )
    ) shouldBe true

    breezeEqual(ev.transform(l1Normalizer, x),
      DenseMatrix(
        List(0.2, 0.4, 0.4),
        List(-0.4, 0.4, 0.2),
        List(-1.0, 0.0, 0.0)
      )
    ) shouldBe true

    breezeEqual(ev.transform(maxNormalizer, x),
      DenseMatrix(
        List(0.5, 1.0, 1.0),
        List(-1.0, 1.0, 0.5),
        List(-1.0, 0.0, 0.0)
      )
    ) shouldBe true
  }

  it should "handle rows with zero norm" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, CategoricalFeature, NumericalFeature))
    val l2Normalizer = Normalizer(featureIndex = featureIndex)
    val x = DenseMatrix(
      List(0.0, 0.0, 0.0),
      List(0.0, 3.0, 4.0)
    )
    val xNormalizedExpected = DenseMatrix(
      List(0.0, 0.0, 0.0),
      List(0.0, 0.6, 0.8)
    )

    breezeEqual(ev.transform(l2Normalizer, x), xNormalizedExpected) shouldBe true
  }

  it should "not modify the domain of the data" in {
    val featureIndex = FeatureIndex(List(CategoricalFeature, NumericalFeature))
    val l2Normalizer = Normalizer(featureIndex = featureIndex)
    assert(ev.featureIndex(l2Normalizer) == featureIndex)
  }
}
