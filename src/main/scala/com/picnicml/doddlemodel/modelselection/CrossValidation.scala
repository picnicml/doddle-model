package com.picnicml.doddlemodel.modelselection

import com.picnicml.doddlemodel.data.{Features, IntVector, Target}
import com.picnicml.doddlemodel.metrics.Metric
import com.picnicml.doddlemodel.typeclasses.Predictor

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Random

/** A parallel, n-fold cross validation technique.
  *
  * @param metric a function from com.picnicml.doddlemodel.metrics used to calculate each fold's score
  * @param dataSplitter a strategy for splitting the dataset into multiple folds
  *
  * Examples:
  * val splitter = KFoldSplitter(folds = 3)
  * val cv = CrossValidation(metric = rmse, dataSplitter = splitter))
  * cv.score(model, x, y)
  */
class CrossValidation private (val metric: Metric, val dataSplitter: DataSplitter) {

  private implicit val ec: CVExecutionContext = new CVExecutionContext()

  /**
    * @param reusable indicates whether to shutdown the thread pool after the cv score is computed
    *  and by default it is, if the same CrossValidation instance is needed after the first call
    *  to score(...), bring implicit CrossValReusable(true) to scope and call CrossValidation.shutdownNow()
    *  after the instance is not needed anymore
    */
  def score[A](model: A, x: Features, y: Target, groups: Option[IntVector] = None)
              (implicit ev: Predictor[A],
               reusable: CrossValReusable = CrossValReusable(false),
               rand: Random = new Random()): Double = {

    val dataSplits =
      groups.fold(this.dataSplitter.splitData(x, y))(groups => this.dataSplitter.splitData(x, y, groups))

    val futureFoldsScores = Future.traverse(dataSplits)(split => this.foldScore(model, split))
    val completedFoldsScores = Await.result(futureFoldsScores, Duration.Inf)

    if (!reusable.yes)
      this.ec.shutdownNow()

    completedFoldsScores.sum / completedFoldsScores.length
  }

  private def foldScore[A](model: A, split: TrainTestSplit)(implicit ev: Predictor[A]): Future[Double] = Future {
    this.metric(split.yTe, ev.predict(ev.fit(model, split.xTr, split.yTr), split.xTe))
  }

  /**
    * Shuts down the current thread pool. Call this if the CrossValidation instance is not needed
    * anymore and CrossValReusable(true) is in scope.
    */
  def shutdownNow(): Unit = this.ec.shutdownNow()
 }

object CrossValidation {

  def apply(metric: Metric, dataSplitter: DataSplitter): CrossValidation =
    new CrossValidation(metric, dataSplitter)
}
