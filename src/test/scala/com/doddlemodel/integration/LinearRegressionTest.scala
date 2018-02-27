package com.doddlemodel.integration

import com.doddlemodel.data.DataLoaders.loadBostonDataset
import com.doddlemodel.linear.LinearRegression
import com.doddlemodel.metrics.RegressionMetrics.rmse
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
