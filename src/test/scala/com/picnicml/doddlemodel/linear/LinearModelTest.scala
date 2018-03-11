package com.picnicml.doddlemodel.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import com.picnicml.doddlemodel.base.Regressor
import com.picnicml.doddlemodel.data.Types.{Features, RealVector, Target}
import org.scalatest.{FlatSpec, Matchers}

class LinearModelTest extends FlatSpec with Matchers {

  private class DummyLinear(val w: Option[RealVector])
    extends Regressor with LinearModel with LinearRegressor {
    protected def copy(w: RealVector): Regressor = new DummyLinear(Some(w))
    protected def predict(w: RealVector, x: Features): Target = x * w
    protected[linear] def loss(w: RealVector, x: Features, y: Target): Double = 0
    protected[linear] def lossGrad(w: RealVector, x: Features, y: Target): RealVector = w
  }

  "Linear model" should "throw exception when using fit, predict on trained, untrained models" in {
    val x = DenseMatrix.rand[Double](10, 5)
    val y = DenseVector.rand[Double](10)
    val model = new DummyLinear(None)

    an [IllegalArgumentException] should be thrownBy model.predict(x)
    val trainedModel = model.fit(x, y)
    an [IllegalArgumentException] should be thrownBy trainedModel.fit(x, y)
  }

  it should "implement predictor functions" in {
    val x = DenseMatrix.rand[Double](10, 5)
    val y = DenseVector.rand[Double](10)
    val model = new DummyLinear(None)

    val trainedModel = model.fit(x, y)
    val yPred = trainedModel.predict(x)
    yPred.length shouldEqual y.length
  }
}
