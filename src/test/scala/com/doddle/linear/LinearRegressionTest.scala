package com.doddle.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import org.scalatest.{FlatSpec, Matchers}

class LinearRegressionTest extends FlatSpec with Matchers {

  "LinearRegression" should "todo" in {
    val x = DenseMatrix.zeros[Double](5, 5)
    val y = DenseVector.zeros[Double](5)
    val model = new LinearRegression()
    val trainedModel = model.fit(x, y)
    trainedModel should be(model)
  }
}
