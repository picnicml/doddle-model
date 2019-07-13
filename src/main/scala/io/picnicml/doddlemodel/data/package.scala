package io.picnicml.doddlemodel

import breeze.linalg.{DenseMatrix, DenseVector, unique}
import io.picnicml.doddlemodel.CrossScalaCompat.floatOrdering
import io.picnicml.doddlemodel.data.Feature.FeatureIndex

/** Provides data management utilities and definitions of custom doddle-model data types. */
package object data {

  type RealVector = DenseVector[Float]
  type IntVector = DenseVector[Int]
  type Simplex = DenseMatrix[Float]

  type Features = DenseMatrix[Float]
  type Target = DenseVector[Float]

  type FeaturesWithIndex = (Features, FeatureIndex)
  type Dataset = (Features, Target)
  type DatasetWithIndex = (Features, Target, FeatureIndex)

  /** Loads and returns the Boston Housing prices dataset. */
  def loadBostonDataset: DatasetWithIndex = ResourceDatasetLoaders.loadBostonDataset
  /** Loads and returns the Breast cancer Wisconsin (diagnostic) dataset.
    *
    * @see <a href="https://archive.ics.uci.edu/ml/datasets/Breast+Cancer+Wisconsin+%28Diagnostic%29" target="_blank">
    *        Breast cancer dataset on UCI Machine Learning Repository </a>
    * */
  def loadBreastCancerDataset: DatasetWithIndex = ResourceDatasetLoaders.loadBreastCancerDataset
  /** Loads and returns the Iris dataset.
    *
    * @see <a href="https://archive.ics.uci.edu/ml/datasets/Breast+Cancer+Wisconsin+%28Diagnostic%29" target="_blank">
    *        Iris dataset on UCI Machine Learning Repository </a>
    * */
  def loadIrisDataset: DatasetWithIndex = ResourceDatasetLoaders.loadIrisDataset

  /** Loads and returns an artificial dataset with a Poisson target variable. */
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
