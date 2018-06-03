package com.picnicml.doddlemodel.integration

import com.picnicml.doddlemodel.data.loadHighSchoolTestDataset
import com.picnicml.doddlemodel.linear.PoissonRegression
import com.picnicml.doddlemodel.metrics.rmse
import com.picnicml.doddlemodel.modelselection.CrossValidation
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class PoissonRegressionTest extends FlatSpec with Matchers {

  "Poisson regression" should "achieve a reasonable score on the high school test dataset" in {
    val (x, y) = loadHighSchoolTestDataset
    val model = PoissonRegression()
    val cv = CrossValidation[PoissonRegression](rmse, folds = 10)
    implicit val rand: Random = new Random(42)

    cv.score(model, x, y) should be < 7.3
  }
}
