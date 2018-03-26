package com.picnicml.doddlemodel.integration

import breeze.stats.distributions.RandBasis
import com.picnicml.doddlemodel.data.loadHighSchoolTestDataset
import com.picnicml.doddlemodel.linear.PoissonRegression
import com.picnicml.doddlemodel.metrics.rmse
import com.picnicml.doddlemodel.modelselection.CrossValidation
import org.scalatest.{FlatSpec, Matchers}

class PoissonRegressionTest extends FlatSpec with Matchers {

  implicit val randBasis: RandBasis = RandBasis.withSeed(0)

  "Poisson regression" should "achieve a reasonable score on the high school test dataset" in {
    val (x, y) = loadHighSchoolTestDataset
    val model = PoissonRegression()
    val cv = CrossValidation(rmse, folds = 10)

    cv.score(model, x, y) should be < 7.3
  }
}
