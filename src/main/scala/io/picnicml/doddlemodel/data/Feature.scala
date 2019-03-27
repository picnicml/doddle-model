package io.picnicml.doddlemodel.data

import scala.reflect.ClassTag

object Feature {

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

    def apply(subsetNames: String*): FeatureIndex = {
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

    def categorical(n: Int): FeatureIndex =
      categorical((0 until n).toList)

    def categorical(columnIndices: List[Int]): FeatureIndex =
      apply(columnIndices.indices.map(i => s"f$i").toList, columnIndices.map(_ => CategoricalFeature), columnIndices)

    def numerical(n: Int): FeatureIndex =
      numerical((0 until n).toList)

    def numerical(columnIndices: List[Int]): FeatureIndex =
      apply(columnIndices.indices.map(i => s"f$i").toList, columnIndices.map(_ => NumericalFeature), columnIndices)

    def apply(types: List[FeatureType]): FeatureIndex =
      apply(types.indices.map(i => s"f$i").toList, types, types.indices.toList)

    def apply(types: List[FeatureType], columnIndices: List[Int]): FeatureIndex =
      apply(types.indices.map(i => s"f$i").toList, types, columnIndices)

    def apply(names: List[String], types: List[FeatureType], columnIndices: List[Int]): FeatureIndex =
      new FeatureIndex(names.toIndexedSeq, types.toIndexedSeq, columnIndices.toIndexedSeq)
  }
}
