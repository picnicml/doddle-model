package io.picnicml.doddlemodel.modelselection

import cats.syntax.option._
import io.picnicml.doddlemodel.CrossScalaCompat.floatOrdering
import io.picnicml.doddlemodel.data.{Features, IntVector, Target}
import io.picnicml.doddlemodel.typeclasses.Predictor

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

  implicit val cvReusable: CrossValReusable = CrossValReusable(true)

  def bestOf[A](x: Features, y: Target, groups: Option[IntVector] = none)(generatePredictor: => A)
               (implicit ev: Predictor[A], rand: Random = new Random()): A = {

    case class PredictorWithScore(predictor: A, score: Float)

    val scoresPredictors = (0 until this.numIterations).map { _ =>
      val predictor = generatePredictor
      PredictorWithScore(predictor, this.crossVal.score(predictor, x, y, groups))
    }

    // this is needed because of cvReusable, see
    // io.picnicml.doddlemodel.modelselection.CrossValidation for details
    this.crossVal.shutdownNow()

    val bestScorePredictor = if (this.crossVal.metric.higherValueIsBetter)
      scoresPredictors.maxBy(_.score)
    else
      scoresPredictors.minBy(_.score)

    if (verbose)
      println(f"Validation ${this.crossVal.metric} of the selected model: ${bestScorePredictor.score}%1.4f")

    ev.fit(bestScorePredictor.predictor, x, y)
  }
}

/** A parallel hyperparameter search using k-fold cross validation. */
object HyperparameterSearch {

  /** Create a hyperparameter search instance.
    * @param numIterations number of predictors for which the cross validation score is calculated
    * @param crossValidation k-fold cross validation instance
    * @param verbose flag that specifies whether validation score of the selected model is printed to standard output
    *
    * @example Search among 3 different regularization values (0.1, 0.2, 0.5) for logistic regression using
    *          3-fold cross validation and store the (re-fitted on entire dataset) model that obtains highest accuracy.
    * {{{
    *   import io.picnicml.doddlemodel.metrics.accuracy
    *   import io.picnicml.doddlemodel.linear.LogisticRegression
    *
    *   val x = DenseMatrix.rand(10, 3)
    *   val y = DenseVector(0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0)
    *
    *   val splitter = KFoldSplitter(numFolds = 3)
    *   val cv = CrossValidation(metric = accuracy, dataSplitter = splitter)
    *   val search = HyperparameterSearch(numIterations = 3, crossValidation = cv)
    *   val lambdas = List(0.1, 0.2, 0.5).iterator
    *
    *   val modelBestParams = search.bestOf(x, y) {
    *     LogisticRegression(lambda = lambdas.next)
    *   }
    * }}}
    */
  def apply(numIterations: Int, crossValidation: CrossValidation, verbose: Boolean = true): HyperparameterSearch = {
    require(numIterations > 0, "Number of iterations must be positive")
    new HyperparameterSearch(numIterations, crossValidation, verbose)
  }
}
