package io.picnicml.doddlemodel.impute

import breeze.linalg.{DenseMatrix, DenseVector}
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}
import io.picnicml.doddlemodel.impute.MeanValueImputer.ev
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class MeanValueImputerTest extends FlatSpec with Matchers with TestingUtils with OptionValues {

  "Mean value imputer" should "impute the numerical features" in {
    val xMissing = DenseMatrix(
      List(Float.NaN, 1.0f, 2.0f),
      List(3.0f, Float.NaN, 5.0f),
      List(6.0f, 7.0f, 8.0f)
    )

    val xImputedExpected = DenseMatrix(
      List(4.5f, 1.0f, 2.0f),
      List(3.0f, Float.NaN, 5.0f),
      List(6.0f, 7.0f, 8.0f)
    )

    val imputer = MeanValueImputer(FeatureIndex.apply(List(NumericalFeature, CategoricalFeature, NumericalFeature)))
    val fittedImputer = ev.fit(imputer, xMissing)

    breezeEqual(fittedImputer.means.value, DenseVector(4.5f, 5.0f)) shouldBe true
    breezeEqual(ev.transform(fittedImputer, xMissing), xImputedExpected) shouldBe true
  }

  it should "impute a subset of numerical features" in {
    val xMissing = DenseMatrix(
      List(Float.NaN, 1.0f, 2.0f),
      List(3.0f, Float.NaN, 5.0f),
      List(6.0f, 7.0f, 8.0f)
    )

    val xImputedExpected = DenseMatrix(
      List(4.5f, 1.0f, 2.0f),
      List(3.0f, Float.NaN, 5.0f),
      List(6.0f, 7.0f, 8.0f)
    )

    val imputer = MeanValueImputer(FeatureIndex.numerical(List(0, 2)))
    val fittedImputer = ev.fit(imputer, xMissing)

    breezeEqual(fittedImputer.means.value, DenseVector(4.5f, 5.0f)) shouldBe true
    breezeEqual(ev.transform(fittedImputer, xMissing), xImputedExpected) shouldBe true
  }
}
