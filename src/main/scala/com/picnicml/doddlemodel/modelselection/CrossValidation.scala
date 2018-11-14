package com.picnicml.doddlemodel.modelselection

import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.metrics.Metric
import com.picnicml.doddlemodel.typeclasses.Predictor

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Random

/** A parallel, n-fold cross validation technique.
  *
  * @param metric a function from com.picnicml.doddlemodel.metrics used to calculate each fold's score
  * @param folds number of folds
  * @param shuffleRows indicates whether examples should be shuffled prior to calculating the score
  *
  * Examples:
  * val cv = CrossValidation(metric = rmse, folds = 10)
  * cv.score(model, x, y)
  */
class CrossValidation private (val metric: Metric, val folds: Int, val shuffleRows: Boolean) {

  private implicit val ec: CVExecutionContext = new CVExecutionContext()

  /**
    * @param reusable indicates whether to shutdown the thread pool after the cv score is computed
    *  and by default it is, if the same CrossValidation instance is needed after the first call
    *  to score(...), bring implicit CrossValReusable(true) to scope and call CrossValidation.shutdownNow()
    *  after the instance is not needed anymore
    */
  def score[A](model: A, x: Features, y: Target)
              (implicit ev: Predictor[A],
               reusable: CrossValReusable = CrossValReusable(false),
               rand: Random = new Random()): Double = {
    val futureFoldsScores = Future.traverse(splitData(x, y))(split => this.foldScore(model, split))
    val completedFoldsScores = Await.result(futureFoldsScores, Duration.Inf)
    if (!reusable.yes) this.ec.shutdownNow()
    completedFoldsScores.sum / completedFoldsScores.length
  }

  private[modelselection] def splitData(x: Features, y: Target)
                                       (implicit rand: Random): Stream[TrainTestSplit] = {
    require(x.rows >= this.folds, "Number of examples must be at least the same as number of folds")

    val shuffleIndices = if (shuffleRows) rand.shuffle[Int, IndexedSeq](0 until y.length) else 0 until y.length
    val xShuffled = x(shuffleIndices, ::)
    val yShuffled = y(shuffleIndices)

    val splitIndices = this.calculateSplitIndices(x.rows)

    (splitIndices zip splitIndices.tail).toStream map {
      case (indexStart, indexEnd) =>
        val trIndices = (0 until indexStart) ++ (indexEnd until x.rows)
        val teIndices = indexStart until indexEnd

        TrainTestSplit(
          // train examples
          xShuffled(trIndices, ::).toDenseMatrix,
          yShuffled(trIndices).toDenseVector,
          // test examples
          xShuffled(teIndices, ::).toDenseMatrix,
          yShuffled(teIndices).toDenseVector)
    }
  }

  private def calculateSplitIndices(numExamples: Int): List[Int] = {
    val atLeastNumExamplesPerFold = List.fill(this.folds)(numExamples / this.folds)
    val numFoldsWithOneMore = numExamples % this.folds

    val numExamplesPerFold = atLeastNumExamplesPerFold.zipWithIndex map {
      case (num, i) if i < numFoldsWithOneMore => num + 1
      case (num, _) => num
    }

    // calculate indices by subtracting number of examples per fold from total number of examples
    numExamplesPerFold.foldRight(List(numExamples)) {
      case (num, head :: tail) => head - num :: head :: tail
      case _ => throw new IllegalStateException("Non-exhaustive match was expected to handle all cases")
    }
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

  def apply(metric: Metric, folds: Int, shuffleRows: Boolean = true): CrossValidation = {
    require(folds > 0, "Number of folds must be positive")
    new CrossValidation(metric, folds, shuffleRows)
  }
}
