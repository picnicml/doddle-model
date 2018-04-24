package com.picnicml.doddlemodel.modelselection

import breeze.linalg.shuffle
import com.picnicml.doddlemodel.base.Predictor
import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.metrics.Metric

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

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
class CrossValidation[A <: Predictor[A]] private (val metric: Metric, val folds: Int, val shuffleRows: Boolean) {

  case class TrainTestSplit(xTr: Features, yTr: Target, xTe: Features, yTe: Target)

  def score(model: Predictor[A], x: Features, y: Target): Double = {
    val foldsScores = splitData(x, y).map(split => this.foldScore(model, split))
    val scoreAvg = Future.sequence(foldsScores).map(scores => scores.sum / scores.length)
    Await.result(scoreAvg, Duration.Inf)
  }

  private[modelselection] def splitData(x: Features, y: Target): List[TrainTestSplit] = {
    require(x.rows >= this.folds, "Number of examples must be at least the same as number of folds")

    val shuffleIndices = if (shuffleRows) shuffle(0 until y.length) else 0 until y.length
    val xShuffled = x(shuffleIndices, ::)
    val yShuffled = y(shuffleIndices)

    val splitIndices = this.calculateSplitIndices(x.rows)

    splitIndices zip splitIndices.tail map {
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

  private def foldScore(model: Predictor[A], split: TrainTestSplit): Future[Double] = Future {
    this.metric(split.yTe, model.fit(split.xTr, split.yTr).predict(split.xTe))
  }
}

object CrossValidation {

  def apply[A <: Predictor[A]](metric: Metric, folds: Int, shuffleRows: Boolean = true): CrossValidation[A] = {
    require(folds > 0, "Number of folds must be positive")
    new CrossValidation[A](metric, folds, shuffleRows)
  }
}
