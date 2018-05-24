package com.picnicml.doddlemodel.modelselection

import com.picnicml.doddlemodel.base.Predictor
import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.executionContext

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


/** A parallel hyperparameter search using n-fold cross validation.
  *
  * @param crossVal com.picnicml.doddlemodel.modelselection.CrossValidation object used to score predictor candidates
  * @param numIterations number of predictors for which the cross validation score is calculated
  *
  * Examples:
  * val search = HyperparameterSearch[LogisticRegression](crossVal = CrossValidation(...), folds = 3)
  * val bestModel = search.bestOf(x, y) {
  *   LogisticRegression(lambda = gamma.draw())
  * }
  */
class HyperparameterSearch[A <: Predictor[A]] private (val crossVal: CrossValidation[A], val numIterations: Int) {

  case class PredictorWithScore(predictor: A, score: Double)

  def bestOf(x: Features, y: Target)(generatePredictor: => A): A = {
    val scores = (0 until this.numIterations).map(_ => crossValidationScore(generatePredictor, x, y))
    val bestPredictor = Await.result(Future.sequence(scores).map(getBestPredictor), Duration.Inf)
    bestPredictor.fit(x, y)
  }

  private def crossValidationScore(predictor: A, x: Features, y: Target): Future[PredictorWithScore] = Future {
    PredictorWithScore(predictor, crossVal.score(predictor, x, y))
  }

  private def getBestPredictor(scores: IndexedSeq[PredictorWithScore]): A = {
    if (this.crossVal.metric.higherValueIsBetter) {
      scores.maxBy(_.score).predictor
    }
    else {
      scores.minBy(_.score).predictor
    }
  }
}

object HyperparameterSearch {

  def apply[A <: Predictor[A]](crossVal: CrossValidation[A], numIterations: Int): HyperparameterSearch[A] = {
    require(numIterations > 0, "Number of iterations must be positive")
    new HyperparameterSearch[A](crossVal, numIterations)
  }
}
