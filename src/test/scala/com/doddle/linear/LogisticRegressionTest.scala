package com.doddle.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import org.scalatest.{FlatSpec, Matchers}

class LogisticRegressionTest extends FlatSpec with Matchers {

  "LogisticRegression" should "todo" in {
    val x = DenseMatrix.zeros[Double](5, 5)
    val y = DenseVector.zeros[Int](5)
    val model = new LogisticRegression()
    val trainedModel = model.fit(x, y)
    trainedModel should be(model)
  }
}
