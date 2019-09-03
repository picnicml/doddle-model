package io.picnicml.doddlemodel.modelselection

import breeze.linalg.argmin
import breeze.stats.hist
import io.picnicml.doddlemodel.CrossScalaCompat.{LazyListCompat, lazyListCompatFromSeq}
import io.picnicml.doddlemodel.data._
import io.picnicml.doddlemodel.modelselection.GroupKFoldSplitter.{TestFolds, TrainTestIndices}

import scala.util.Random

/** K-Folds strategy for splitting data that makes sure groups in each fold are non-overlapping,
  * i.e no group is present in both training and testing splits. Folds try to be as balanced
  * as possible, i.e. the number of test examples in each fold is approximately the same.
  *
  * @param numFolds number of folds
  *
  * Examples:
  * val dataSplitter = GroupKFoldSplitter(folds = 3)
  * datasplitter.splitData(x, y, groups)
  */
class GroupKFoldSplitter private (val numFolds: Int) extends DataSplitter {

  override def splitData(x: Features, y: Target, groups: IntVector)
                        (implicit rand: Random = new Random()): LazyListCompat[TrainTestSplit] = {
    val testFolds = calculateTestFolds(groups)

    lazyListCompatFromSeq(0 until numFolds).map { foldIndex =>
      val indices = groups.iterator.foldLeft(TrainTestIndices()) { case (acc, (exampleIndex, group)) =>
        if (testFolds.groupToTestFoldIndex(group) == foldIndex)
          acc.addToTestIndex(exampleIndex)
        else
          acc.addToTrainIndex(exampleIndex)
      }

      TrainTestSplit(
        x(indices.trIndices, ::).toDenseMatrix,
        y(indices.trIndices).toDenseVector,
        x(indices.teIndices, ::).toDenseMatrix,
        y(indices.teIndices).toDenseVector
      )
    }
  }

  private def calculateTestFolds(groups: IntVector): TestFolds = {
    val numGroups = numberOfUniqueGroups(groups)
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

  override def splitData(x: Features, y: Target)(implicit rand: Random): LazyListCompat[TrainTestSplit] =
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
