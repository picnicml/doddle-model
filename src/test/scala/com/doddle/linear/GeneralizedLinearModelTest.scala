package com.doddle.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import com.doddle.Types.{RealMatrix, RealVector}
import com.doddle.base.Regressor
import org.scalatest.{FlatSpec, Matchers}

class GeneralizedLinearModelTest extends FlatSpec with Matchers {

  private class DummyLinear extends Regressor with GeneralizedLinearModel[Double] {

    protected[linear] def meanFunction(latent: RealVector): RealVector = latent
    protected[linear] def loss(w: RealVector, x: RealMatrix, y: RealVector): Double = 0
    protected[linear] def lossGrad(w: RealVector, x: RealMatrix, y: RealVector): RealVector = w
  }

  "GLM" should "prevent invalid model states" in {
    val x = DenseMatrix.rand[Double](10, 5)
    val y = DenseVector.rand[Double](10)
    val model = new DummyLinear()

    an [IllegalArgumentException] should be thrownBy model.predict(x)
    model.fit(x, y)
    an [IllegalArgumentException] should be thrownBy model.fit(x, y)
  }

  it should "implement predictor functions" in {
    val x = DenseMatrix.rand[Double](10, 5)
    val y = DenseVector.rand[Double](10)
    val model = new DummyLinear()

    model.fit(x, y)
    val yPred = model.predict(x)
    yPred.length shouldEqual y.length
  }
}
