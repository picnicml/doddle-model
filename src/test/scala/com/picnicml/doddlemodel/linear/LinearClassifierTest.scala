package com.picnicml.doddlemodel.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.numerics.sigmoid
import com.picnicml.doddlemodel.base.Classifier
import com.picnicml.doddlemodel.data.{Features, RealVector, Simplex, Target}
import org.scalatest.{FlatSpec, Matchers}

class LinearClassifierTest extends FlatSpec with Matchers {

  private class DummyLinearClassifier(val numClasses: Option[Int], val w: Option[RealVector])
    extends LinearClassifier with Serializable {

    override protected def copy(numClasses: Int): LinearClassifier =
      new DummyLinearClassifier(Some(numClasses), this.w)

    override protected def copy(w: RealVector): Classifier =
      new DummyLinearClassifier(this.numClasses, Some(w))

    override protected def predict(w: RealVector, x: Features): Target = x * w

    override protected def predictProba(w: RealVector, x: Features): Simplex =
      sigmoid(x * w).asDenseMatrix.t

    override protected[linear] def loss(w: RealVector, x: Features, y: Target): Double = 0

    override protected[linear] def lossGrad(w: RealVector, x: Features, y: Target): RealVector = w
  }

  private val x = DenseMatrix.rand[Double](10, 5)
  private val y = DenseVector.vertcat(DenseVector.zeros[Double](5), DenseVector.ones[Double](5))
  private val model = new DummyLinearClassifier(None, None)

  "Linear classifier" should "throw an exception when using fit, predict on trained, untrained models" in {
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

  it should "set the correct number of classes after fit" in {
    model.numClasses.isEmpty shouldBe true
    val trainedModel = model.fit(x, y)
    trainedModel.numClasses.get shouldBe 2
  }

  it should "throw an exception if fitting a model with an invalid target variable" in {
    val invalidCategoricalY = DenseVector.zeros[Double](10)
    an [IllegalArgumentException] should be thrownBy model.fit(x, invalidCategoricalY)
    val invalidRealY = DenseVector.rand[Double](10)
    an [IllegalArgumentException] should be thrownBy model.fit(x, invalidRealY)
  }
}
