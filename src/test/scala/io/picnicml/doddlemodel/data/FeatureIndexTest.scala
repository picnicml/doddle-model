package io.picnicml.doddlemodel.data

import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}
import org.scalatest.{FlatSpec, Matchers}

class FeatureIndexTest extends FlatSpec with Matchers {

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
    val subset = featureIndex("f1", "f3", "f4", "f5", "f8")
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
}
