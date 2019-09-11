package io.picnicml.doddlemodel.modelselection

import cats.syntax.option._
import io.picnicml.doddlemodel.data.{Features, IntVector, Target, TrainTestSplit}
import io.picnicml.doddlemodel.metrics.Metric
import io.picnicml.doddlemodel.typeclasses.Predictor

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Random

class CrossValidation private (val metric: Metric, val dataSplitter: DataSplitter) {

  private implicit val ec: CVExecutionContext = new CVExecutionContext()

  /** Obtain the average score of all folds.
    *
    * @param reusable indicates whether to shutdown the thread pool after the cv score is computed
    *  and by default it is, if the same CrossValidation instance is needed after the first call
    *  to score(...), bring implicit CrossValReusable(true) to scope and call CrossValidation.shutdownNow()
    *  after the instance is not needed anymore
    */
  def score[A](model: A, x: Features, y: Target, groups: Option[IntVector] = none)
              (implicit ev: Predictor[A],
               reusable: CrossValReusable = CrossValReusable(false),
               rand: Random = new Random()): Float = {

    val dataSplits =
      groups.fold(this.dataSplitter.splitData(x, y))(groups => this.dataSplitter.splitData(x, y, groups))

    val futureFoldsScores = Future.traverse(dataSplits)(split => this.foldScore(model, split))
    val completedFoldsScores = Await.result(futureFoldsScores, Duration.Inf)

    if (!reusable.yes)
      this.ec.shutdownNow()

    completedFoldsScores.sum / completedFoldsScores.length
  }

  private def foldScore[A](model: A, split: TrainTestSplit)(implicit ev: Predictor[A]): Future[Float] = Future {
    this.metric(split.yTe, ev.predict(ev.fit(model, split.xTr, split.yTr), split.xTe))
  }

  /**
    * Shuts down the current thread pool. Call this if the CrossValidation instance is not needed
    * anymore and CrossValReusable(true) is in scope.
    */
  def shutdownNow(): Unit = this.ec.shutdownNow()
 }

/** A parallel, k-fold cross validation technique. */
object CrossValidation {

  /** Create a k-fold cross validation instance.
    * @param metric a function from [[io.picnicml.doddlemodel.metrics]] used to calculate each fold's score
    * @param dataSplitter a strategy for splitting the dataset into multiple folds
    *
    * @example Perform 2-fold cross validation using logistic regression and evaluate its performance
    *          using root mean squared error.
    *   {{{
    *   import io.picnicml.doddlemodel.metrics.rmse
    *   import io.picnicml.doddlemodel.linear.LogisticRegression
    *
    *   val X: Features = DenseMatrix(List(1.0, 2.0), List(3.0, 4.0), List(5.0, 6.0), List(7.0, 8.0))
    *   val y: Target = DenseVector(0.0, 1.0, 0.0, 1.0)
    *   val model = LogisticRegression(1.0)
    *
    *   val splitter = KFoldSplitter(numFolds = 2)
    *   val cv = CrossValidation(metric = rmse, dataSplitter = splitter))
    *   cv.score(model, X, y)
    *   }}}
    *
    * @see [[io.picnicml.doddlemodel.metrics Metrics in doddle-model]]
    */
  def apply(metric: Metric, dataSplitter: DataSplitter): CrossValidation =
    new CrossValidation(metric, dataSplitter)
}
