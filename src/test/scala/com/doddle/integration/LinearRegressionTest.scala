package com.doddle.integration

import com.doddle.data.DataLoaders.loadBoston
import com.doddle.linear.LinearRegression
import com.doddle.metrics.RegressionMetrics.rmse
import org.scalatest.{FlatSpec, Matchers}

class LinearRegressionTest extends FlatSpec with Matchers {

  "LinearRegression" should "achieve a resonable score on the Boston housing dataset" in {
    // todo: evaluate on test set
    val (x, y) = loadBoston
    val model = LinearRegression()
    model.fit(x, y)
    val yPred = model.predict(x)
    rmse(y, yPred) should be < 5.0
  }
}
