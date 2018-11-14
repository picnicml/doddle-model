package com.picnicml.doddlemodel.integration

import com.picnicml.doddlemodel.data.loadBostonDataset
import com.picnicml.doddlemodel.linear.LinearRegression
import com.picnicml.doddlemodel.metrics.rmse
import com.picnicml.doddlemodel.modelselection.{CrossValidation, KFoldSplitter}
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class LinearRegressionTest extends FlatSpec with Matchers {

  "Linear regression" should "achieve a reasonable score on the Boston housing dataset" in {
    val (x, y) = loadBostonDataset
    val model = LinearRegression()
    val cv = CrossValidation(rmse, KFoldSplitter(folds = 10))
    implicit val rand: Random = new Random(42)

    cv.score(model, x, y) should be < 6.0
  }
}
