package com.picnicml.doddlemodel.modelselection

import com.picnicml.doddlemodel.typeclasses.Predictor
import com.picnicml.doddlemodel.data.{Features, Target}

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
class HyperparameterSearch[A <: Predictor[A]] private (val numIterations: Int) {

  implicit val cvReusable: CrossValReusable = CrossValReusable(true)

  case class PredictorWithScore(predictor: A, score: Double)

  def bestOf(x: Features, y: Target)
            (generatePredictor: => A)
            (implicit crossVal: CrossValidation, rand: Random = new Random()): A = {
    val scores = (0 until this.numIterations).map { _ =>
      val predictor = generatePredictor
      PredictorWithScore(predictor, crossVal.score(predictor, x, y))
    }

    // this is needed because of cvReusable, see
    // com.picnicml.doddlemodel.modelselection.CrossValidation for details
    crossVal.shutdownNow()

    if (crossVal.metric.higherValueIsBetter) {
      scores.maxBy(_.score).predictor.fit(x, y)
    }
    else {
      scores.minBy(_.score).predictor.fit(x, y)
    }
  }
}

object HyperparameterSearch {

  def apply[A <: Predictor[A]](numIterations: Int): HyperparameterSearch[A] = {
    require(numIterations > 0, "Number of iterations must be positive")
    new HyperparameterSearch[A](numIterations)
  }
}
