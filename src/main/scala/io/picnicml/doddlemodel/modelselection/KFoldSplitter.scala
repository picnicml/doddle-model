package io.picnicml.doddlemodel.modelselection
import io.picnicml.doddlemodel.data.{Features, IntVector, Target}

import scala.util.Random

/** K-Folds strategy for splitting data.
  *
  * @param numFolds number of folds
  * @param shuffleRows indicates whether examples should be shuffled prior to calculating the score
  *
  * Examples:
  * val dataSplitter = KFoldSplitter(folds = 3)
  * datasplitter.splitData(x, y)
  */
class KFoldSplitter private (val numFolds: Int, val shuffleRows: Boolean) extends DataSplitter {

  override def splitData(x: Features, y: Target)(implicit rand: Random = new Random()): Stream[TrainTestSplit] = {
    require(x.rows >= this.numFolds, "Number of examples must be at least the same as number of folds")

    val shuffleIndices = if (this.shuffleRows) rand.shuffle[Int, IndexedSeq](0 until y.length) else 0 until y.length
    val xShuffled = x(shuffleIndices, ::)
    val yShuffled = y(shuffleIndices)

    val splitIndices = this.calculateSplitIndices(x.rows)

    (splitIndices zip splitIndices.tail).toStream map { case (indexStart, indexEnd) =>
      val trIndices = (0 until indexStart) ++ (indexEnd until x.rows)
      val teIndices = indexStart until indexEnd

      TrainTestSplit(
        // train examples
        xShuffled(trIndices, ::).toDenseMatrix,
        yShuffled(trIndices).toDenseVector,
        // test examples
        xShuffled(teIndices, ::).toDenseMatrix,
        yShuffled(teIndices).toDenseVector
      )
    }
  }

  private def calculateSplitIndices(numExamples: Int): List[Int] = {
    val atLeastNumExamplesPerFold = List.fill(this.numFolds)(numExamples / this.numFolds)
    val numFoldsWithOneMore = numExamples % this.numFolds

    val numExamplesPerFold = atLeastNumExamplesPerFold.zipWithIndex map {
      case (num, i) if i < numFoldsWithOneMore => num + 1
      case (num, _) => num
    }

    // calculate indices by subtracting number of examples per fold from total number of examples
    numExamplesPerFold.foldRight(List(numExamples)) {
      case (num, head :: tail) => head - num :: head :: tail
      case _ => throw new IllegalStateException()
    }
  }


  override def splitData(x: Features, y: Target, groups: IntVector)(implicit rand: Random): Stream[TrainTestSplit] =
    throw new NotImplementedError("KFoldSplitter doesn't split data based on groups")
}

object KFoldSplitter {

  def apply(numFolds: Int, shuffleRows: Boolean = true): KFoldSplitter = {
    require(numFolds > 0, "Number of folds must be positive")
    new KFoldSplitter(numFolds, shuffleRows)
  }
}
