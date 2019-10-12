package io.picnicml.doddlemodel.modelselection

import cats.syntax.option._
import io.picnicml.doddlemodel.CrossScalaCompat.{floatOrdering, lazyListCompatFromSeq}
import io.picnicml.doddlemodel.data.{Features, IntVector, Target}
import io.picnicml.doddlemodel.typeclasses.Predictor

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Random


/** A parallel hyperparameter search using n-fold cross validation.
  *
  * @param numIterations number of predictors for which the cross validation score is calculated
  * @param verbose flag that specifies whether validation score of the selected model is
  *                printed to standard output
  *
  * Examples:
  * val splitter = KFoldSplitter(numFolds = 3)
  * val cv: CrossValidation = CrossValidation(metric = accuracy, dataSplitter = splitter)
  * val search = HyperparameterSearch(numIterations = 3, crossValidation = cv)
  * val bestModel = search.bestOf(x, y) {
  *   LogisticRegression(lambda = gamma.draw())
  * }
  */
class HyperparameterSearch private (val numIterations: Int, val crossVal: CrossValidation, verbose: Boolean) {

  private implicit val ec: CVExecutionContext = new CVExecutionContext()

  def bestOf[A](x: Features, y: Target, groups: Option[IntVector] = none)(generatePredictor: => A)
               (implicit ev: Predictor[A], rand: Random = new Random()): A = {
    val allFolds = lazyListCompatFromSeq(0 until this.numIterations).flatMap { iterationId =>
      this.crossVal.folds(generatePredictor, x, y, crossValId = iterationId, groups)
    }

    val predictorsScores: Seq[PredictorWithScore[A]] = Await
      .result(Future.sequence(allFolds), Duration.Inf)
      .groupBy(_.crossValId)
      .toSeq
      .map { case (_, folds) =>
        PredictorWithScore[A](folds(0).predictor, folds.map(_.score).sum / folds.length)
      }

    this.ec.shutdownNow()

    val bestScorePredictor = if (this.crossVal.metric.higherValueIsBetter)
      predictorsScores.maxBy(_.score)
    else
      predictorsScores.minBy(_.score)

    if (verbose)
      println(f"Validation ${this.crossVal.metric} of the selected model: ${bestScorePredictor.score}%1.4f")

    ev.fit(bestScorePredictor.predictor, x, y)
  }

  case class PredictorWithScore[A: Predictor](predictor: A, score: Float)
}

object HyperparameterSearch {

  def apply(numIterations: Int, crossValidation: CrossValidation, verbose: Boolean = true): HyperparameterSearch = {
    require(numIterations > 0, "Number of iterations must be positive")
    new HyperparameterSearch(numIterations, crossValidation, verbose)
  }
}
