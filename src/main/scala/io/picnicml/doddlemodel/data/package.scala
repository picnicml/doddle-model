package io.picnicml.doddlemodel

import java.io.File

import breeze.linalg.{DenseMatrix, DenseVector, unique}
import breeze.stats.hist
import com.github.tototoshi.csv.CSVReader

import scala.util.Random
import scala.util.control.Exception.nonFatalCatch

package object data {

  type Features = DenseMatrix[Double]
  type Target = DenseVector[Double]
  type Simplex = DenseMatrix[Double]
  type RealVector = DenseVector[Double]
  type IntVector = DenseVector[Int]
  type Dataset = (Features, Target)

  case class TrainTestSplit(xTr: Features, yTr: Target, xTe: Features, yTe: Target)
  case class GroupTrainTestSplit(xTr: Features,
                                 yTr: Target,
                                 groupsTr: IntVector,
                                 xTe: Features,
                                 yTe: Target,
                                 groupsTe: IntVector)

  def loadBostonDataset: Dataset = DatasetsLoaders.loadBostonDataset
  def loadBreastCancerDataset: Dataset = DatasetsLoaders.loadBreastCancerDataset
  def loadIrisDataset: Dataset = DatasetsLoaders.loadIrisDataset
  def loadHighSchoolTestDataset: Dataset = DatasetsLoaders.loadHighSchoolTestDataset

  /** Loads a csv dataset, if headerLine = true the first line is skipped, if element is not Double it becomes NaN. */
  def loadCsvDataset(filePath: String, headerLine: Boolean = true): DenseMatrix[Double] = {
    val reader = CSVReader.open(new File(filePath))
    if (headerLine) reader.readNext()
    val data = DenseMatrix(
      reader.toStream.map(_.map(x => (nonFatalCatch opt x.toDouble).getOrElse(Double.NaN)).toArray):_*
    )
    reader.close()
    data
  }

  /** Shuffles rows of the dataset. */
  def shuffleDataset(x: Features, y: Target)(implicit rand: Random = new Random()): Dataset = {
    val shuffleIndices = rand.shuffle[Int, IndexedSeq](0 until y.length)
    (x(shuffleIndices, ::).toDenseMatrix, y(shuffleIndices).toDenseVector)
  }

  /** Splits the dataset into two subsets for training and testing. */
  def splitDataset(x: Features, y: Target, proportionTrain: Double = 0.5): TrainTestSplit = {
    val numTrain = numberOfTrainExamplesBasedOnProportion(x.rows, proportionTrain)
    val trIndices = 0 until numTrain
    val teIndices = numTrain until x.rows
    TrainTestSplit(x(trIndices, ::), y(trIndices), x(teIndices, ::), y(teIndices))
  }

  /** Splits the dataset into two subsets for training and testing and makes sure groups in each are non-overlapping. */
  def splitDatasetWithGroups(x: Features,
                             y: Target,
                             groups: IntVector,
                             proportionTrain: Double = 0.5): GroupTrainTestSplit = {
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

  private def numberOfTrainExamplesBasedOnProportion(numTotal: Int, proportionTrain: Double): Int = {
    require(proportionTrain > 0.0 && proportionTrain < 1.0, "proportionTrain must be between 0 and 1")
    val numTrain = (proportionTrain * numTotal.toDouble).toInt
    require(numTrain > 0 && numTrain < numTotal, "the value of proportionTrain is either too high or too low")
    numTrain
  }

  def numberOfUniqueGroups(groups: IntVector): Int = {
    val uniqueGroups = unique(groups)
    require(uniqueGroups.toArray.sorted sameElements Array.range(0, uniqueGroups.length),
      "Invalid encoding of groups, all group indices in [0, numGroups) have to exist")
    uniqueGroups.length
  }

  def numberOfTargetClasses(y: Target): Int = {
    val targetClasses = unique(y)
    require(targetClasses.length >= 2,
      "Target variable must be comprised of at least two categories")
    require(targetClasses.toArray.sorted sameElements Array.range(0, targetClasses.length),
      "Invalid encoding of categories in the target variable")
    targetClasses.length
  }
}
