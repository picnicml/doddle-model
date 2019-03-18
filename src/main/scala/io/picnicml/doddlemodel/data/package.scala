package io.picnicml.doddlemodel

import breeze.linalg.{DenseMatrix, DenseVector, unique}

package object data {

  type Features = DenseMatrix[Double]
  type Target = DenseVector[Double]
  type Simplex = DenseMatrix[Double]
  type RealVector = DenseVector[Double]
  type IntVector = DenseVector[Int]
  type Dataset = (Features, Target)
  type FeatureIndex = IndexedSeq[Int]

  case class TrainTestSplit(xTr: Features, yTr: Target, xTe: Features, yTe: Target)
  case class GroupTrainTestSplit(xTr: Features,
                                 yTr: Target,
                                 groupsTr: IntVector,
                                 xTe: Features,
                                 yTe: Target,
                                 groupsTe: IntVector)

  def loadBostonDataset: Dataset = ResourceDatasetLoaders.loadBostonDataset
  def loadBreastCancerDataset: Dataset = ResourceDatasetLoaders.loadBreastCancerDataset
  def loadIrisDataset: Dataset = ResourceDatasetLoaders.loadIrisDataset
  def loadHighSchoolTestDataset: Dataset = ResourceDatasetLoaders.loadHighSchoolTestDataset

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
