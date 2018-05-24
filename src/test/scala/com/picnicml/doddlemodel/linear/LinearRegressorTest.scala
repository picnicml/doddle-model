package com.picnicml.doddlemodel.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import com.picnicml.doddlemodel.data.{Features, RealVector, Target}
import org.scalatest.{FlatSpec, Matchers}

class LinearRegressorTest extends FlatSpec with Matchers {

  private class DummyLinearRegressor(val w: Option[RealVector])
    extends LinearRegressor[DummyLinearRegressor] with Serializable {

    override protected def copy: DummyLinearRegressor = this

    override protected def copy(w: RealVector): DummyLinearRegressor = new DummyLinearRegressor(Some(w))

    override protected def targetVariableAppropriate(y: Target): Boolean = true

    override protected def predict(w: RealVector, x: Features): Target = x * w

    override protected[linear] def loss(w: RealVector, x: Features, y: Target): Double = 0

    override protected[linear] def lossGrad(w: RealVector, x: Features, y: Target): RealVector = w
  }

  private val x = DenseMatrix.rand[Double](10, 5)
  private val y = DenseVector.rand[Double](10)
  private val model = new DummyLinearRegressor(None)

  "Linear regressor" should "throw an exception when using fit, predict on trained, untrained models" in {
    an [IllegalArgumentException] should be thrownBy model.predict(x)
    val trainedModel = model.fit(x, y)
    an [IllegalArgumentException] should be thrownBy trainedModel.fit(x, y)
  }

  it should "implement predictor functions" in {
    model.isFitted shouldBe false
    val trainedModel = model.fit(x, y)
    trainedModel.isFitted shouldBe true
    val yPred = trainedModel.predict(x)
    yPred.length shouldEqual y.length
  }
}
