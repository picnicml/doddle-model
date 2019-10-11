package io.picnicml.doddlemodel.integration

import io.picnicml.doddlemodel.CrossScalaCompat.floatOrdering
import io.picnicml.doddlemodel.data.loadHighSchoolTestDataset
import io.picnicml.doddlemodel.linear.PoissonRegression
import io.picnicml.doddlemodel.metrics.rmse
import io.picnicml.doddlemodel.modelselection.{CrossValidation, KFoldSplitter}
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class PoissonRegressionTest extends FlatSpec with Matchers {

  "Poisson regression" should "achieve a reasonable score on the high school test dataset" in {
    val (x, y, _) = loadHighSchoolTestDataset
    val model = PoissonRegression()
    val cv = CrossValidation(rmse, KFoldSplitter(numFolds = 10))
    implicit val rand: Random = new Random(42)

    cv.score(model, x, y) should be < 7.3f
  }
}
