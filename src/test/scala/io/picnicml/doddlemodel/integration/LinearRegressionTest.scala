package io.picnicml.doddlemodel.integration

import io.picnicml.doddlemodel.CrossScalaCompat.floatOrdering
import io.picnicml.doddlemodel.data.loadBostonDataset
import io.picnicml.doddlemodel.linear.LinearRegression
import io.picnicml.doddlemodel.metrics.rmse
import io.picnicml.doddlemodel.modelselection.{CrossValidation, KFoldSplitter}
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class LinearRegressionTest extends FlatSpec with Matchers {

  "Linear regression" should "achieve a reasonable score on the Boston housing dataset" in {
    val (x, y, _) = loadBostonDataset
    val model = LinearRegression()
    val cv = CrossValidation(rmse, KFoldSplitter(numFolds = 10))
    implicit val rand: Random = new Random(42)

    cv.score(model, x, y) should be < 6.0f
  }
}
