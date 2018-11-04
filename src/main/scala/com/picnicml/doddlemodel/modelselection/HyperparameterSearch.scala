package com.picnicml.doddlemodel.modelselection

import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.typeclasses.Predictor

import scala.util.Random


/** A parallel hyperparameter search using n-fold cross validation.
  *
  * @param numIterations number of predictors for which the cross validation score is calculated
  *
  * Examples:
  * implicit val cv: CrossValidation = CrossValidation(metric = accuracy, folds = 5)
  * val search = HyperparameterSearch[LogisticRegression](folds = 3)
  * val bestModel = search.bestOf(x, y) {
  *   LogisticRegression(lambda = gamma.draw())
  * }
  */
class HyperparameterSearch private (val numIterations: Int) {

  implicit val cvReusable: CrossValReusable = CrossValReusable(true)

  def bestOf[A](x: Features, y: Target)(generatePredictor: => A)
               (implicit ev: Predictor[A], crossVal: CrossValidation, rand: Random = new Random()): A = {

    case class PredictorWithScore(predictor: A, score: Double)

    val scores = (0 until this.numIterations).map { _ =>
      val predictor = generatePredictor
      PredictorWithScore(predictor, crossVal.score(predictor, x, y))
    }

    // this is needed because of cvReusable, see
    // com.picnicml.doddlemodel.modelselection.CrossValidation for details
    crossVal.shutdownNow()

    if (crossVal.metric.higherValueIsBetter)
      ev.fit(scores.maxBy(_.score).predictor, x, y)
    else
      ev.fit(scores.minBy(_.score).predictor, x, y)
  }
}

object HyperparameterSearch {

  def apply(numIterations: Int): HyperparameterSearch = {
    require(numIterations > 0, "Number of iterations must be positive")
    new HyperparameterSearch(numIterations)
  }
}
