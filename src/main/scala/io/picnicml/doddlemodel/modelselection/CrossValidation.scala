package io.picnicml.doddlemodel.modelselection

import cats.syntax.option._
import io.picnicml.doddlemodel.CrossScalaCompat.LazyListCompat
import io.picnicml.doddlemodel.data.{Features, IntVector, Target, TrainTestSplit}
import io.picnicml.doddlemodel.metrics.Metric
import io.picnicml.doddlemodel.typeclasses.Predictor

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Random

/** A parallel, n-fold cross validation technique.
  *
  * @param metric a function from io.picnicml.doddlemodel.metrics used to calculate each fold's score
  * @param dataSplitter a strategy for splitting the dataset into multiple folds
  *
  * Examples:
  * val splitter = KFoldSplitter(folds = 3)
  * val cv = CrossValidation(metric = rmse, dataSplitter = splitter))
  * cv.score(model, x, y)
  */
class CrossValidation private (val metric: Metric, val dataSplitter: DataSplitter) {

  private implicit val ec: CVExecutionContext = new CVExecutionContext()

  def score[A](model: A, x: Features, y: Target, groups: Option[IntVector] = none)
              (implicit ev: Predictor[A], rand: Random = new Random()): Float = {
    val completedFolds = Await.result(Future.sequence(this.folds(model, x, y, groups = groups)), Duration.Inf)
    this.ec.shutdownNow()
    completedFolds.map(_.score).sum / completedFolds.length
  }

  private[modelselection] def folds[A](model: A,
                                       x: Features,
                                       y: Target,
                                       crossValId: Int = 0,
                                       groups: Option[IntVector] = none)
                                      (implicit ev: Predictor[A],
                                       rand: Random = new Random()): LazyListCompat[Future[CVFold[A]]] = {
    val dataSplits =
      groups.fold(this.dataSplitter.splitData(x, y))(groups => this.dataSplitter.splitData(x, y, groups))
    dataSplits.map(split => this.fold(model, split, crossValId))
  }

  private def fold[A](model: A,
                      split: TrainTestSplit,
                      crossValId: Int)
                     (implicit ev: Predictor[A]): Future[CVFold[A]] = Future {
    CVFold(model, this.metric(split.yTe, ev.predict(ev.fit(model, split.xTr, split.yTr), split.xTe)), crossValId)
  }
 }

object CrossValidation {

  def apply(metric: Metric, dataSplitter: DataSplitter): CrossValidation =
    new CrossValidation(metric, dataSplitter)
}
