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
    * @param reusable indicates whether to shutdown the thread pool after the cv score is computed.
    *  By default it is shutdown.
    *
    *  @note If the same `CrossValidation` instance is needed after the first call to `score(...)`, bring an implicit
    *        `CrossValReusable(true)` to scope and call `CrossValidation.shutdownNow()` after the instance is not
    *        needed anymore.
    *
    *  @example Reuse a `CrossValidation` instance.
    *   {{{
    *     import breeze.linalg.{DenseMatrix, DenseVector}
    *     import io.picnicml.doddlemodel.metrics.rmse
    *     import io.picnicml.doddlemodel.linear.LogisticRegression
    *     import io.picnicml.doddlemodel.modelselection.{CrossValidation, KFoldSplitter}
    *     import io.picnicml.doddlemodel.modelselection.CrossValReusable
    *
    *     implicit val cvReusable = CrossValReusable(true)
    *     val X = DenseMatrix(List(1.0f, 2.0f), List(3.0f, 4.0f), List(5.0f, 6.0f), List(7.0f, 8.0f))
    *     val y = DenseVector(0.0f, 1.0f, 0.0f, 1.0f)
    *     val model = LogisticRegression(1.0f)
    *
    *     val splitter = KFoldSplitter(numFolds = 2)
    *     val cv = CrossValidation(metric = rmse, dataSplitter = splitter)
    *     cv.score(model, X, y)
    *     // would throw a `RejectedExecutionException` if an implicit `CrossValReusable` instance was not defined
    *     cv.score(model, X, y)
    *     cv.shutdownNow()
    *   }}}
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
    * anymore and `CrossValReusable(true)` is in scope.
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
    *     import breeze.linalg.{DenseMatrix, DenseVector}
    *     import io.picnicml.doddlemodel.metrics.rmse
    *     import io.picnicml.doddlemodel.linear.LogisticRegression
    *     import io.picnicml.doddlemodel.modelselection.{CrossValidation, KFoldSplitter}
    *
    *     val X = DenseMatrix(List(1.0f, 2.0f), List(3.0f, 4.0f), List(5.0f, 6.0f), List(7.0f, 8.0f))
    *     val y = DenseVector(0.0f, 1.0f, 0.0f, 1.0f)
    *     val model = LogisticRegression(1.0f)
    *
    *     val splitter = KFoldSplitter(numFolds = 2)
    *     val cv = CrossValidation(metric = rmse, dataSplitter = splitter)
    *     cv.score(model, X, y)
    *   }}}
    *
    * @see [[io.picnicml.doddlemodel.metrics Metrics in doddle-model]]
    */
  def apply(metric: Metric, dataSplitter: DataSplitter): CrossValidation =
    new CrossValidation(metric, dataSplitter)
}
