package com.picnicml.doddlemodel.preprocessing

import breeze.linalg.{*, DenseMatrix, DenseVector}
import breeze.stats.{mean, stddev}
import com.picnicml.doddlemodel.TestUtils
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class StandardScalerTest extends FlatSpec with Matchers with TestUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  "Standard scaler" should "subtract the mean and scale to unit variance" in {
    val x = DenseMatrix.rand[Double](10, 5)
    val scaler = StandardScaler()
    val trainedScaler = scaler.fit(x)
    val xTransformed = trainedScaler.transform(x)

    breezeEqual(mean(x(::, *)).t, DenseVector.zeros(5)) shouldBe false
    breezeEqual(stddev(x(::, *)).t, DenseVector.ones(5)) shouldBe false
    breezeEqual(mean(xTransformed(::, *)).t, DenseVector.zeros(5)) shouldBe true
    breezeEqual(stddev(xTransformed(::, *)).t, DenseVector.ones(5)) shouldBe true
  }

  it should "handle the zero variance case" in {
    val x = DenseMatrix.ones[Double](10, 5)
    val scaler = StandardScaler()
    val trainedScaler = scaler.fit(x)
    val xTransformed = trainedScaler.transform(x)

    xTransformed.forall(_.isNaN) shouldBe false
  }
}
