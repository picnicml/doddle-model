package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.DenseMatrix
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}
import io.picnicml.doddlemodel.preprocessing.RangeScaler.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class RangeScalerTest extends FlatSpec with Matchers with TestingUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  private val x = DenseMatrix(
    List(-3.0, 2.0, 1.0),
    List(-3.0, 3.0, 0.0),
    List(-3.0, 0.0, 0.0),
    List(-3.0, 5.0, 1.0)
  )

  "Range scaler" should "scale numerical features to specified range" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, CategoricalFeature))
    val rangeScaler = RangeScaler((0.0, 1.0), featureIndex)
    val trainedRangeScaler = ev.fit(rangeScaler, x)

    val xScaledExpected = DenseMatrix(
      List(0.0, 0.4, 1.0),
      List(0.0, 0.6, 0.0),
      List(0.0, 0.0, 0.0),
      List(0.0, 1.0, 1.0)
    )
    breezeEqual(ev.transform(trainedRangeScaler, x), xScaledExpected) shouldBe true
  }

  it should "scale selected subset of numerical features to specified range" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, CategoricalFeature))
    val rangeScaler = RangeScaler((0.0, 1.0), featureIndex.subset(1 to 1))
    val trainedRangeScaler = ev.fit(rangeScaler, x)

    val xScaledExpected = DenseMatrix(
      List(-3.0, 0.4, 1.0),
      List(-3.0, 0.6, 0.0),
      List(-3.0, 0.0, 0.0),
      List(-3.0, 1.0, 1.0)
    )
    breezeEqual(ev.transform(trainedRangeScaler, x), xScaledExpected) shouldBe true
  }

  it should "amount to no-op if there are no numerical features in data" in {
    val featureIndex = FeatureIndex(List(CategoricalFeature, CategoricalFeature, CategoricalFeature))
    val rangeScaler = RangeScaler((0.0, 1.0), featureIndex)
    val trainedRangeScaler = ev.fit(rangeScaler, x)

    breezeEqual(ev.transform(trainedRangeScaler, x), x) shouldBe true
  }
}
