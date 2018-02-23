package com.doddle.integration

import com.doddle.data.DataLoaders.loadBostonDataset
import com.doddle.linear.LinearRegression
import com.doddle.metrics.RegressionMetrics.rmse
import org.scalatest.{FlatSpec, Matchers}

class LinearRegressionTest extends FlatSpec with Matchers {

  "Linear regression" should "achieve a reasonable score on the Boston housing dataset" in {
    // todo: calculate and test a CV score
    val (x, y) = loadBostonDataset
    val trainedModel = LinearRegression().fit(x, y)
    val yPred = trainedModel.predict(x)

    rmse(y, yPred) should be < 5.0
  }
}
