package com.picnicml.doddlemodel.integration

import breeze.stats.distributions.RandBasis
import com.picnicml.doddlemodel.data.loadBostonDataset
import com.picnicml.doddlemodel.linear.LinearRegression
import com.picnicml.doddlemodel.metrics.rmse
import com.picnicml.doddlemodel.modelselection.CrossValidation
import org.scalatest.{FlatSpec, Matchers}

class LinearRegressionTest extends FlatSpec with Matchers {

  implicit val randBasis: RandBasis = RandBasis.mt0

  "Linear regression" should "achieve a reasonable score on the Boston housing dataset" in {
    val (x, y) = loadBostonDataset
    val model = LinearRegression()
    val cv = CrossValidation[LinearRegression](rmse, folds = 10)

    cv.score(model, x, y) should be < 6.0
  }
}
