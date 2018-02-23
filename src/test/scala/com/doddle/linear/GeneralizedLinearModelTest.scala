package com.doddle.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import com.doddle.TypeAliases.{RealMatrix, RealVector}
import com.doddle.base.{Predictor, Regressor}
import org.scalatest.{FlatSpec, Matchers}

class GeneralizedLinearModelTest extends FlatSpec with Matchers {

  private class DummyLinear(val w: RealVector) extends Regressor with GeneralizedLinearModel[Double] {
    protected def newInstance(w: RealVector): Predictor[Double] = new DummyLinear(w)
    protected[linear] def meanFunction(latent: RealVector): RealVector = latent
    protected[linear] def loss(w: RealVector, x: RealMatrix, y: RealVector): Double = 0
    protected[linear] def lossGrad(w: RealVector, x: RealMatrix, y: RealVector): RealVector = w
  }

  "Generalized linear model" should "throw exception when using fit, predict on trained, untrained models" in {
    val x = DenseMatrix.rand[Double](10, 5)
    val y = DenseVector.rand[Double](10)
    val model = new DummyLinear(w = null)

    an [IllegalArgumentException] should be thrownBy model.predict(x)
    val trainedModel = model.fit(x, y)
    an [IllegalArgumentException] should be thrownBy trainedModel.fit(x, y)
  }

  it should "implement predictor functions" in {
    val x = DenseMatrix.rand[Double](10, 5)
    val y = DenseVector.rand[Double](10)
    val model = new DummyLinear(w = null)

    val trainedModel = model.fit(x, y)
    val yPred = trainedModel.predict(x)
    yPred.length shouldEqual y.length
  }
}
