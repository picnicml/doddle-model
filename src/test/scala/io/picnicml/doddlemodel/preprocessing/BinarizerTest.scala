package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{DenseMatrix, DenseVector}
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}
import io.picnicml.doddlemodel.preprocessing.Binarizer.ev
import org.scalatest.{FlatSpec, Matchers}

class BinarizerTest extends FlatSpec with Matchers with TestingUtils {
  val xMatrix = DenseMatrix(
    List(0.0, 1.0, 0.0),
    List(0.3, -1.0, 1.0),
    List(-0.3, 2.0, 0.0)
  )

  "Binarizer" should "process the numerical columns by corresponding thresholds" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, CategoricalFeature))
    val thresholds: DenseVector[Double] = DenseVector(0.0, -1.5)

    val binarizer = Binarizer(thresholds, featureIndex)

    breezeEqual(ev.transform(binarizer, xMatrix), DenseMatrix(
      List(0.0, 1.0),
      List(1.0, 1.0),
      List(0.0, 1.0))) shouldBe true
  }

  it should "process all the numerical columns by a single threshold" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, NumericalFeature))
    val threshold: Double = 0.5

    val binarizer = Binarizer(threshold, featureIndex)

    breezeEqual(ev.transform(binarizer, xMatrix), DenseMatrix(
      List(0.0, 1.0, 0.0),
      List(0.0, 0.0, 1.0),
      List(0.0, 1.0, 0.0)
    ))
  }

  it should "fail when there are insufficient/no numeric features in data" in {
    val featureIndex1 = FeatureIndex(List(NumericalFeature, NumericalFeature, NumericalFeature))
    val featureIndex2 = FeatureIndex(List(CategoricalFeature, CategoricalFeature, CategoricalFeature))
    val thresholds: DenseVector[Double] = DenseVector(0.0, -1.5)

    // 3 numeric columns vs 2 thresholds
    an [IllegalArgumentException] should be thrownBy Binarizer(thresholds, featureIndex1)
    // 0 numeric columns
    an [IllegalArgumentException] should be thrownBy Binarizer(thresholds, featureIndex2)
  }
}
