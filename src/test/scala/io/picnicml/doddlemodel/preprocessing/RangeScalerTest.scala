package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.DenseMatrix
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}
import io.picnicml.doddlemodel.preprocessing.RangeScaler.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class RangeScalerTest extends FlatSpec with Matchers with TestingUtils {

  implicit val tolerance: Equality[Float] = TolerantNumerics.tolerantFloatEquality(1e-4f)

  private val x = DenseMatrix(
    List(-3.0f, 2.0f, 1.0f),
    List(-3.0f, 3.0f, 0.0f),
    List(-3.0f, 0.0f, 0.0f),
    List(-3.0f, 5.0f, 1.0f)
  )

  "Range scaler" should "scale numerical features to specified range" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, CategoricalFeature))
    val rangeScaler1 = RangeScaler((0.0f, 1.0f), featureIndex)
    val trainedRangeScaler1 = ev.fit(rangeScaler1, x)
    val rangeScaler2 = RangeScaler((-5.0f, 15.0f), featureIndex)
    val trainedRangeScaler2 = ev.fit(rangeScaler2, x)

    breezeEqual(
      ev.transform(trainedRangeScaler1, x),
      DenseMatrix(
        List(0.0f, 0.4f, 1.0f),
        List(0.0f, 0.6f, 0.0f),
        List(0.0f, 0.0f, 0.0f),
        List(0.0f, 1.0f, 1.0f)
      )
    ) shouldBe true

    breezeEqual(
      ev.transform(trainedRangeScaler2, x),
      DenseMatrix(
        List(-5.0f, 3.0f, 1.0f),
        List(-5.0f, 7.0f, 0.0f),
        List(-5.0f, -5.0f, 0.0f),
        List(-5.0f, 15.0f, 1.0f)
      )
    ) shouldBe true
  }

  it should "scale selected subset of numerical features to specified range" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, CategoricalFeature))
    val rangeScaler1 = RangeScaler((0.0f, 1.0f), featureIndex.subset(1 to 1))
    val trainedRangeScaler1 = ev.fit(rangeScaler1, x)
    val rangeScaler2 = RangeScaler((-5.0f, 15.0f), featureIndex.subset(1 to 1))
    val trainedRangeScaler2 = ev.fit(rangeScaler2, x)

    breezeEqual(
      ev.transform(trainedRangeScaler1, x),
      DenseMatrix(
        List(-3.0f, 0.4f, 1.0f),
        List(-3.0f, 0.6f, 0.0f),
        List(-3.0f, 0.0f, 0.0f),
        List(-3.0f, 1.0f, 1.0f)
      )
    ) shouldBe true

    breezeEqual(
      ev.transform(trainedRangeScaler2, x),
      DenseMatrix(
        List(-3.0f, 3.0f, 1.0f),
        List(-3.0f, 7.0f, 0.0f),
        List(-3.0f, -5.0f, 0.0f),
        List(-3.0f, 15.0f, 1.0f)
      )
    ) shouldBe true
  }

  it should "amount to no-op if there are no numerical features in data" in {
    val featureIndex = FeatureIndex(List(CategoricalFeature, CategoricalFeature, CategoricalFeature))
    val rangeScaler = RangeScaler((0.0f, 1.0f), featureIndex)
    val trainedRangeScaler = ev.fit(rangeScaler, x)

    breezeEqual(ev.transform(trainedRangeScaler, x), x) shouldBe true
  }
}
