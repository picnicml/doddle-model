package io.picnicml.doddlemodel.data

import scala.reflect.ClassTag

object FeatureIndex {

  sealed trait FeatureType extends Product with Serializable {
    val headerLineString: String
  }

  final case object NumericalFeature extends FeatureType {
    override val headerLineString = "n"
  }

  final case object CategoricalFeature extends FeatureType {
    override val headerLineString = "c"
  }

  class FeatureIndex(val names: IndexedSeq[String],
                     val types: IndexedSeq[FeatureType],
                     val columnIndices: IndexedSeq[Int]) {

    def categorical: FeatureIndex = onlyFeaturesOfType[CategoricalFeature.type]

    def numerical: FeatureIndex = onlyFeaturesOfType[NumericalFeature.type]

    private def onlyFeaturesOfType[A <: FeatureType: ClassTag]: FeatureIndex = {
      val cls = implicitly[ClassTag[A]].runtimeClass
      val subsetIndices = this.types.zipWithIndex.flatMap { case (t, i) => if (cls.isInstance(t)) Some(i) else None }
      this.subset(subsetIndices)
    }

    def apply(subsetNames: List[String]): FeatureIndex = {
      val nameToIndex = this.names.zipWithIndex.toMap
      this.subset(subsetNames.map(n => nameToIndex(n)).toIndexedSeq)
    }

    private def subset(indices: IndexedSeq[Int]): FeatureIndex = new FeatureIndex(
      indices.map(i => this.names(i)),
      indices.map(i => this.types(i)),
      indices.map(i => this.columnIndices(i))
    )
  }

  object FeatureIndex {

    def dummyCategorical(columnIndices: IndexedSeq[Int]): FeatureIndex =
      dummy(columnIndices, CategoricalFeature)

    def dummyNumerical(columnIndices: IndexedSeq[Int]): FeatureIndex =
      dummy(columnIndices, NumericalFeature)

    private def dummy(columnIndices: IndexedSeq[Int], featureType: FeatureType): FeatureIndex = new FeatureIndex(
      columnIndices.map(i => s"f$i"),
      columnIndices.map(_ => featureType),
      columnIndices
    )
  }
}
