package com.picnicml.doddlemodel.modelselection

import breeze.linalg.{argmin, unique}
import breeze.stats.hist
import com.picnicml.doddlemodel.data.{Features, IntVector, Target}
import com.picnicml.doddlemodel.modelselection.GroupKFoldSplitter.{TestFolds, TrainTestIndices}

import scala.util.Random

/** K-Folds strategy for splitting data that makes sure groups in each fold are non-overlapping,
  * i.e no group is present in both training and testing splits.
  *
  * @param numFolds number of folds
  *
  * Examples:
  * val dataSplitter = GroupKFoldSplitter(folds = 3)
  * datasplitter.splitData(x, y, groups)
  */
class GroupKFoldSplitter private (val numFolds: Int) extends DataSplitter {

  override def splitData(x: Features, y: Target, groups: IntVector)
                        (implicit rand: Random = new Random()): Stream[TrainTestSplit] = {
    val uniqueGroups = unique(groups)
    require(uniqueGroups.toArray.sorted sameElements Array.range(0, uniqueGroups.length),
      "Invalid encoding of groups, all group indices in [0, numGroups) have to exist")

    val testFolds = calculateTestFolds(groups, uniqueGroups.length)

    (0 until numFolds).toStream.map { foldIndex =>
      val indices = groups.iterator.foldLeft(TrainTestIndices()) { case (acc, (exampleIndex, group)) =>
        if (testFolds.groupToTestFoldIndex(group) == foldIndex)
          acc.addToTestIndex(exampleIndex)
        else
          acc.addToTrainIndex(exampleIndex)
      }

      TrainTestSplit(
        // train examples
        x(indices.trIndices, ::).toDenseMatrix,
        y(indices.trIndices).toDenseVector,
        // test examples
        x(indices.teIndices, ::).toDenseMatrix,
        y(indices.teIndices).toDenseVector
      )
    }
  }

  private def calculateTestFolds(groups: IntVector, numGroups: Int): TestFolds = {
    val numSamplesPerGroup = hist(groups, numGroups).hist.toArray

    implicit val ordering: Ordering[Int] = Ordering.Int.reverse
    val (sortedNumSamplesPerGroup, toOriginalGroupIndex) = numSamplesPerGroup.zipWithIndex.sorted.unzip

    sortedNumSamplesPerGroup.zipWithIndex.foldLeft(TestFolds(numFolds, numGroups)) {
      case (acc, (numSamples, group)) =>
        val smallestFoldIndex = argmin(acc.numTestSamplesPerFold)
        acc.addNumSamplesToFold(numSamples, smallestFoldIndex)
        acc.setGroupToTestFoldIndex(toOriginalGroupIndex(group), smallestFoldIndex)
        acc
    }
  }

  override def splitData(x: Features, y: Target)(implicit rand: Random): Stream[TrainTestSplit] =
    throw new NotImplementedError("GroupKFoldSplitter only splits data based on groups")
}


object GroupKFoldSplitter {

  def apply(numFolds: Int): GroupKFoldSplitter = {
    require(numFolds > 0, "Number of folds must be positive")
    new GroupKFoldSplitter(numFolds)
  }

  private case class TrainTestIndices(trIndices: IndexedSeq[Int] = IndexedSeq(),
                                      teIndices: IndexedSeq[Int] = IndexedSeq()) {
    def addToTrainIndex(x: Int): TrainTestIndices = this.copy(trIndices = this.trIndices :+ x)
    def addToTestIndex(x: Int): TrainTestIndices = this.copy(teIndices = this.teIndices :+ x)
  }

  private case class TestFolds(numTestSamplesPerFold: Array[Int], groupToTestFoldIndex: Array[Int]) {
    def addNumSamplesToFold(numSamples: Int, foldIndex: Int): Unit =
      this.numTestSamplesPerFold(foldIndex) += numSamples

    def setGroupToTestFoldIndex(group: Int, foldIndex: Int): Unit =
      this.groupToTestFoldIndex(group) = foldIndex
  }

  private object TestFolds {
    def apply(numFolds: Int, numGroups: Int): TestFolds =
      TestFolds(new Array[Int](numFolds), new Array[Int](numGroups))
  }
}
