package io.picnicml.doddlemodel.data

import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}
import org.scalatest.{FlatSpec, Matchers}

class FeatureTest extends FlatSpec with Matchers {

  "Feature index" should "return a subset of categorical features" in {
    val featureIndex = FeatureIndex(List(CategoricalFeature, NumericalFeature, CategoricalFeature))
    val categorical = featureIndex.categorical
    categorical.names shouldBe IndexedSeq("f0", "f2")
    categorical.types shouldBe IndexedSeq(CategoricalFeature, CategoricalFeature)
    categorical.columnIndices shouldBe IndexedSeq(0, 2)
  }

  it should "return a subset of numerical features" in {
    val featureIndex = FeatureIndex(List(CategoricalFeature, NumericalFeature, CategoricalFeature))
    val numerical = featureIndex.numerical
    numerical.names shouldBe IndexedSeq("f1")
    numerical.types shouldBe IndexedSeq(NumericalFeature)
    numerical.columnIndices shouldBe IndexedSeq(1)
  }

  it should "return a subset of features based on feature names" in {
    val types = List(
      CategoricalFeature,
      NumericalFeature,
      CategoricalFeature,
      CategoricalFeature,
      NumericalFeature,
      CategoricalFeature,
      NumericalFeature,
      CategoricalFeature,
      NumericalFeature,
      NumericalFeature
    )
    val featureIndex = FeatureIndex(types)
    val subset = featureIndex.subset("f1", "f3", "f4", "f5", "f8")
    subset.names shouldBe IndexedSeq("f1", "f3", "f4", "f5", "f8")
    subset.types shouldBe IndexedSeq(
      NumericalFeature,
      CategoricalFeature,
      NumericalFeature,
      CategoricalFeature,
      NumericalFeature
    )
    subset.columnIndices shouldBe IndexedSeq(1, 3, 4, 5, 8)
  }

  it should "return a subset of features based on indices" in {
    val types = List(
      CategoricalFeature,
      NumericalFeature,
      CategoricalFeature,
      CategoricalFeature,
      NumericalFeature,
      CategoricalFeature,
      NumericalFeature,
      CategoricalFeature,
      NumericalFeature,
      NumericalFeature
    )
    val featureIndex = FeatureIndex(types)

    val subset0 = featureIndex.subset(1 to 3)
    subset0.names shouldBe IndexedSeq("f1", "f2", "f3")
    subset0.types shouldBe IndexedSeq(
      NumericalFeature,
      CategoricalFeature,
      CategoricalFeature
    )
    subset0.columnIndices shouldBe IndexedSeq(1, 2, 3)

    val subset1 = featureIndex.subset(3, 4, 5)
    subset1.names shouldBe IndexedSeq("f3", "f4", "f5")
    subset1.types shouldBe IndexedSeq(
      CategoricalFeature,
      NumericalFeature,
      CategoricalFeature
    )
    subset1.columnIndices shouldBe IndexedSeq(3, 4, 5)
  }

  it should "drop a column" in {
    val featureIndex = FeatureIndex(List(CategoricalFeature, NumericalFeature, CategoricalFeature))
    val droppedFirst = featureIndex.drop(0)
    droppedFirst.names shouldBe IndexedSeq("f1", "f2")
    droppedFirst.types shouldBe IndexedSeq(NumericalFeature, CategoricalFeature)
    droppedFirst.columnIndices shouldBe IndexedSeq(0, 1)
    val droppedMiddle = featureIndex.drop(1)
    droppedMiddle.names shouldBe IndexedSeq("f0", "f2")
    droppedMiddle.types shouldBe IndexedSeq(CategoricalFeature, CategoricalFeature)
    droppedMiddle.columnIndices shouldBe IndexedSeq(0, 1)
    val droppedLast = featureIndex.drop(2)
    droppedLast.names shouldBe IndexedSeq("f0", "f1")
    droppedLast.types shouldBe IndexedSeq(CategoricalFeature, NumericalFeature)
    droppedLast.columnIndices shouldBe IndexedSeq(0, 1)
  }
}
