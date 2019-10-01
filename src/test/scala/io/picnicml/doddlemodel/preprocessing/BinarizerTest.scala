package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{DenseMatrix, DenseVector}
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}
import io.picnicml.doddlemodel.preprocessing.Binarizer.ev
import org.scalatest.{FlatSpec, Matchers}

class BinarizerTest extends FlatSpec with Matchers with TestingUtils {

  private val x = DenseMatrix(
    List(0.0, 1.0, 0.0),
    List(0.3, -1.0, 1.0),
    List(-0.3, 2.0, 0.0)
  )

  "Binarizer" should "process the numerical columns by corresponding thresholds" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, CategoricalFeature))
    val thresholds: DenseVector[Double] = DenseVector(0.0, -1.5)

    val binarizer = Binarizer(thresholds, featureIndex)
    val xBinarizedExpected = DenseMatrix(
      List(0.0, 1.0, 0.0),
      List(1.0, 1.0, 1.0),
      List(0.0, 1.0, 0.0)
    )

    breezeEqual(ev.transform(binarizer, x), xBinarizedExpected) shouldBe true
  }

  it should "process all the numerical columns by a single threshold" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, NumericalFeature))
    val threshold: Double = 0.5

    val binarizer = Binarizer(threshold, featureIndex)
    val xBinarizedExpected = DenseMatrix(
      List(0.0, 1.0, 0.0),
      List(0.0, 0.0, 1.0),
      List(0.0, 1.0, 0.0)
    )

    breezeEqual(ev.transform(binarizer, x), xBinarizedExpected) shouldBe true
  }

  it should "amount to no-op if there are no numerical features in data" in {
    val featureIndex = FeatureIndex(List(CategoricalFeature, CategoricalFeature, CategoricalFeature))
    val thresholds1: DenseVector[Double] = DenseVector(0.0, -1.5)
    val thresholds2: Double = 0.5

    val binarizer1 = Binarizer(thresholds1, featureIndex)
    val binarizer2 = Binarizer(thresholds2, featureIndex)

    breezeEqual(ev.transform(binarizer1, x), x) shouldBe true
    breezeEqual(ev.transform(binarizer2, x), x) shouldBe true
  }

  it should "fail when the amount of passed thresholds is different to number of numerical features in data" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, NumericalFeature))
    val thresholds: DenseVector[Double] = DenseVector(0.0, -1.5)

    // 3 numeric columns vs 2 thresholds
    an [IllegalArgumentException] should be thrownBy Binarizer(thresholds, featureIndex)
  }

  it should "convert numerical features to categorical features" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, CategoricalFeature))
    val thresholds: DenseVector[Double] = DenseVector(0.0, -1.5)
    val binarizer = Binarizer(thresholds, featureIndex)

    assert(ev.featureIndex(binarizer).types == List(CategoricalFeature, CategoricalFeature, CategoricalFeature))
  }
}
