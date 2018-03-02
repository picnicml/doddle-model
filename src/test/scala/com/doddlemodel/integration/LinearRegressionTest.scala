package com.doddlemodel.integration

import com.doddlemodel.data.DataLoaders.loadBostonDataset
import com.doddlemodel.linear.LinearRegression
import com.doddlemodel.metrics.RegressionMetrics.rmse
import com.doddlemodel.modelselection.CrossValidation
import org.scalatest.{FlatSpec, Matchers}

class LinearRegressionTest extends FlatSpec with Matchers {

  "Linear regression" should "achieve a reasonable score on the Boston housing dataset" in {
    val (x, y) = loadBostonDataset
    val model = LinearRegression()
    val cv = CrossValidation(rmse, folds = 10)

    cv.score(model, x, y) should be < 6.0
  }
}
