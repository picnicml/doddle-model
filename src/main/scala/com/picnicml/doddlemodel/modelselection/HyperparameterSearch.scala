package com.picnicml.doddlemodel.modelselection

import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.typeclasses.Predictor

import scala.util.Random


/** A parallel hyperparameter search using n-fold cross validation.
  *
  * @param numIterations number of predictors for which the cross validation score is calculated
  *
  * Examples:
  * val splitter = KFoldSplitter(numFolds = 3)
  * val cv: CrossValidation = CrossValidation(metric = accuracy, dataSplitter = splitter)
  * val search = HyperparameterSearch(numIterations = 3, crossValidation = cv)
  * val bestModel = search.bestOf(x, y) {
  *   LogisticRegression(lambda = gamma.draw())
  * }
  */
class HyperparameterSearch private (val numIterations: Int, val crossVal: CrossValidation) {

  implicit val cvReusable: CrossValReusable = CrossValReusable(true)

  def bestOf[A](x: Features, y: Target)(generatePredictor: => A)
               (implicit ev: Predictor[A], rand: Random = new Random()): A = {

    case class PredictorWithScore(predictor: A, score: Double)

    val scores = (0 until this.numIterations).map { _ =>
      val predictor = generatePredictor
      PredictorWithScore(predictor, this.crossVal.score(predictor, x, y))
    }

    // this is needed because of cvReusable, see
    // com.picnicml.doddlemodel.modelselection.CrossValidation for details
    this.crossVal.shutdownNow()

    if (this.crossVal.metric.higherValueIsBetter)
      ev.fit(scores.maxBy(_.score).predictor, x, y)
    else
      ev.fit(scores.minBy(_.score).predictor, x, y)
  }
}

object HyperparameterSearch {

  def apply(numIterations: Int, crossValidation: CrossValidation): HyperparameterSearch = {
    require(numIterations > 0, "Number of iterations must be positive")
    new HyperparameterSearch(numIterations, crossValidation)
  }
}
