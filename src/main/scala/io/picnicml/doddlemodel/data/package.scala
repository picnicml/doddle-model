package io.picnicml.doddlemodel

import breeze.linalg.{DenseMatrix, DenseVector, unique}
import io.picnicml.doddlemodel.data.Feature.FeatureIndex

package object data {

  type RealVector = DenseVector[Double]
  type IntVector = DenseVector[Int]
  type Simplex = DenseMatrix[Double]

  type Features = DenseMatrix[Double]
  type Target = DenseVector[Double]

  type FeaturesWithIndex = (Features, FeatureIndex)
  type Dataset = (Features, Target)
  type DatasetWithIndex = (Features, Target, FeatureIndex)

  def loadBostonDataset: DatasetWithIndex = ResourceDatasetLoaders.loadBostonDataset
  def loadBreastCancerDataset: DatasetWithIndex = ResourceDatasetLoaders.loadBreastCancerDataset
  def loadIrisDataset: DatasetWithIndex = ResourceDatasetLoaders.loadIrisDataset
  def loadHighSchoolTestDataset: DatasetWithIndex = ResourceDatasetLoaders.loadHighSchoolTestDataset

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
