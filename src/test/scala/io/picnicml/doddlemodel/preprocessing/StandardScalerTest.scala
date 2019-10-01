package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{*, DenseMatrix, DenseVector}
import breeze.stats.{mean, stddev}
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}
import io.picnicml.doddlemodel.preprocessing.StandardScaler.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class StandardScalerTest extends FlatSpec with Matchers with TestingUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  "Standard scaler" should "preprocess the numerical features" in {
    val x = DenseMatrix.rand[Double](10, 5)
    val featureIndex = FeatureIndex(
      List(
        NumericalFeature,
        NumericalFeature,
        NumericalFeature,
        NumericalFeature,
        CategoricalFeature
      )
    )
    val scaler = StandardScaler(featureIndex)
    val trainedScaler = ev.fit(scaler, x)
    val xTransformed = ev.transform(trainedScaler, x)

    breezeEqual(mean(x(::, *)).t, DenseVector.zeros[Double](5)) shouldBe false
    breezeEqual(stddev(x(::, *)).t, DenseVector.ones[Double](5)) shouldBe false

    val expectedMeans = DenseVector.zeros[Double](5)
    expectedMeans(-1) = mean(x(::, -1))
    breezeEqual(mean(xTransformed(::, *)).t, expectedMeans) shouldBe true

    val expectedStdDevs = DenseVector.ones[Double](5)
    expectedStdDevs(-1) = stddev(x(::, -1))
    breezeEqual(stddev(xTransformed(::, *)).t, expectedStdDevs) shouldBe true
  }

  it should "handle the zero variance case" in {
    val x = DenseMatrix.ones[Double](10, 5)
    val scaler = StandardScaler(FeatureIndex.numerical(5))
    val trainedScaler = ev.fit(scaler, x)
    val xTransformed = ev.transform(trainedScaler, x)

    xTransformed.forall(_.isNaN) shouldBe false
  }

  it should "preprocess a subset of numerical features" in {
    val x = DenseMatrix.rand[Double](10, 5)
    val scaler = StandardScaler(FeatureIndex.numerical(5).subset("f0", "f2", "f4"))
    val trainedScaler = ev.fit(scaler, x)
    val xTransformed = ev.transform(trainedScaler, x)

    breezeEqual(mean(x(::, *)).t, DenseVector.zeros[Double](5)) shouldBe false
    breezeEqual(stddev(x(::, *)).t, DenseVector.ones[Double](5)) shouldBe false
    assert(mean(xTransformed(::, 0)) === 0.0 +- 1e-4)
    assert(stddev(xTransformed(::, 0)) === 1.0 +- 1e-4)
    assert(mean(xTransformed(::, 1)) !== 0.0 +- 1e-4)
    assert(stddev(xTransformed(::, 1)) !== 1.0 +- 1e-4)
    assert(mean(xTransformed(::, 2)) === 0.0 +- 1e-4)
    assert(stddev(xTransformed(::, 2)) === 1.0 +- 1e-4)
    assert(mean(xTransformed(::, 3)) !== 0.0 +- 1e-4)
    assert(stddev(xTransformed(::, 3)) !== 1.0 +- 1e-4)
    assert(mean(xTransformed(::, 4)) === 0.0 +- 1e-4)
    assert(stddev(xTransformed(::, 4)) === 1.0 +- 1e-4)
  }

  it should "not modify the domain of the data" in {
    val x = DenseMatrix.rand[Double](10, 2)
    val featureIndex = FeatureIndex(List(CategoricalFeature, NumericalFeature))
    val scaler = StandardScaler(featureIndex)
    val trainedScaler = ev.fit(scaler, x)

    assert(ev.featureIndex(trainedScaler) == featureIndex)
  }
}
