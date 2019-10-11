package io.picnicml.doddlemodel.data

import breeze.stats.hist

import scala.util.Random

object DatasetUtils {

  /** Shuffles rows of the dataset. */
  def shuffleDataset(x: Features, y: Target)(implicit rand: Random = new Random()): Dataset = {
    val shuffleIndices = rand.shuffle((0 until y.length).toIndexedSeq)
    (x(shuffleIndices, ::).toDenseMatrix, y(shuffleIndices).toDenseVector)
  }

  /** Splits the dataset into two subsets for training and testing. */
  def splitDataset(x: Features, y: Target, proportionTrain: Float = 0.5f): TrainTestSplit = {
    val numTrain = numberOfTrainExamplesBasedOnProportion(x.rows, proportionTrain)
    val trIndices = 0 until numTrain
    val teIndices = numTrain until x.rows
    TrainTestSplit(x(trIndices, ::), y(trIndices), x(teIndices, ::), y(teIndices))
  }

  /** Splits the dataset into two subsets for training and testing and makes sure groups in each are non-overlapping. */
  def splitDatasetWithGroups(x: Features,
                             y: Target,
                             groups: IntVector,
                             proportionTrain: Float = 0.5f): GroupTrainTestSplit = {
    val numTrain = numberOfTrainExamplesBasedOnProportion(x.rows, proportionTrain)
    val numSamplesPerGroup = hist(groups, numberOfUniqueGroups(groups)).hist.toArray
    val (sortedNumSamplesPerGroup, toOriginalGroupIndex) = numSamplesPerGroup.zipWithIndex.sorted.unzip

    val numGroupsInTrain = sortedNumSamplesPerGroup
      .foldLeft(List(0)) { case (acc, currGroupSize) => (acc(0) + currGroupSize) :: acc }.reverse.drop(1)
      .takeWhile(cumulativeNumSamples => cumulativeNumSamples <= numTrain)
      .length

    val groupsInTrain = (0 until numGroupsInTrain).map(group => toOriginalGroupIndex(group))

    val (trIndices, teIndices) = (0 until groups.length).foldLeft((IndexedSeq[Int](), IndexedSeq[Int]())) {
      case ((currTrIndices, currTeIndices), groupIndex) =>
        if (groupsInTrain.contains(groups(groupIndex)))
          (currTrIndices :+ groupIndex, currTeIndices)
        else
          (currTrIndices, currTeIndices :+ groupIndex)
    }

    GroupTrainTestSplit(
      x(trIndices, ::).toDenseMatrix,
      y(trIndices).toDenseVector,
      groups(trIndices).toDenseVector,
      x(teIndices, ::).toDenseMatrix,
      y(teIndices).toDenseVector,
      groups(teIndices).toDenseVector
    )
  }

  private def numberOfTrainExamplesBasedOnProportion(numTotal: Int, proportionTrain: Float): Int = {
    require(proportionTrain > 0.0 && proportionTrain < 1.0, "proportionTrain must be between 0 and 1")
    val numTrain = (proportionTrain * numTotal.toFloat).toInt
    require(numTrain > 0 && numTrain < numTotal, "the value of proportionTrain is either too high or too low")
    numTrain
  }
}
