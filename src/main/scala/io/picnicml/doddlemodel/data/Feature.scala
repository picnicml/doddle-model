package io.picnicml.doddlemodel.data

import cats.syntax.option._

import scala.reflect.ClassTag

object Feature {

  sealed trait FeatureType {
    val headerLineString: String
  }

  final case object NumericalFeature extends FeatureType {
    override val headerLineString = "n"
  }

  final case object CategoricalFeature extends FeatureType {
    override val headerLineString = "c"
  }

  @SerialVersionUID(0L)
  class FeatureIndex(val names: IndexedSeq[String],
                     val types: IndexedSeq[FeatureType],
                     val columnIndices: IndexedSeq[Int]) extends Serializable {

    def categorical: FeatureIndex = onlyFeaturesOfType[CategoricalFeature.type]

    def numerical: FeatureIndex = onlyFeaturesOfType[NumericalFeature.type]

    private def onlyFeaturesOfType[A <: FeatureType: ClassTag]: FeatureIndex = {
      val cls = implicitly[ClassTag[A]].runtimeClass
      val subsetIndices = this.types.zipWithIndex.flatMap {
        case (t, i) => if (cls.isInstance(t)) i.some else none[Int]
      }
      subset(subsetIndices:_*)
    }

    /** Create a feature index with subset of features, provided by feature names.
      * @param names subset of features to be selected
      *
      * @example Create feature index based on features "f1" and "f3" from a constructed feature index.
      *   {{{
      *     import io.picnicml.doddlemodel.data.Feature.{FeatureIndex, NumericalFeature}
      *
      *     val featureIndex = FeatureIndex(List("f1", "f2", "f3"), List(NumericalFeature, NumericalFeature,
      *       NumericalFeature), List(0, 1, 2))
      *     val subIndex = featureIndex.subset("f1", "f3")
      *   }}}
      *
      */
    def subset(names: String*): FeatureIndex = {
      val nameToIndex = this.names.zipWithIndex.toMap
      subset(names.map(n => nameToIndex(n)):_*)
    }

    /** Create a feature index with subset of features, provided by feature indices.
      * @param indices column indices for subset of features to be selected
      *
      * @example Create feature index based on second and third (i.e. indices 1, 2) features from a constructed
      *          feature index.
      *   {{{
      *     import io.picnicml.doddlemodel.data.Feature.{FeatureIndex, NumericalFeature}
      *
      *     val featureIndex = FeatureIndex(List("f1", "f2", "f3"), List(NumericalFeature, NumericalFeature,
      *       NumericalFeature), List(0, 1, 2))
      *     val subIndex = featureIndex.subset(1 to 2)
      *   }}}
      */
    def subset(indices: IndexedSeq[Int]): FeatureIndex = subset(indices:_*)

    /** Create a feature index with subset of features, provided by feature indices. Alternative interface to do same
      * as with `FeatureIndex.subset(indices: IndexedSeq[Int])`.
      * @param indices column indices for subset of features to be selected
      *
      * @example Create feature index based on second and third (i.e. indices 1, 2) features from a constructed
      *          feature index.
      * {{{
      *   import io.picnicml.doddlemodel.data.Feature.{FeatureIndex, NumericalFeature}
      *
      *   val featureIndex = FeatureIndex(List("f1", "f2", "f3"), List(NumericalFeature, NumericalFeature,
      *     NumericalFeature), List(0, 1, 2))
      *   val subIndex = featureIndex.subset(1, 2)
      * }}}
      */
    // DummyImplicit is needed to avoid the same type as String* after erasure
    def subset(indices: Int*)(implicit di: DummyImplicit): FeatureIndex = new FeatureIndex(
      indices.toIndexedSeq.map(i => this.names(i)),
      indices.toIndexedSeq.map(i => this.types(i)),
      indices.toIndexedSeq.map(i => this.columnIndices(i))
    )

    /** Create a feature index by dropping a feature by column index.
      * @param index index of column to be dropped
      * @example Drop the third (index 2) feature from a feature index.
      *   {{{
      *     import io.picnicml.doddlemodel.data.Feature.{FeatureIndex, NumericalFeature}
      *
      *     val featureIndex = FeatureIndex(List("f1", "f2", "f3"), List(NumericalFeature, NumericalFeature,
      *       NumericalFeature), List(0, 1, 2))
      *     val subIndex = featureIndex.drop(2)
      *   }}}
      */
    def drop(index: Int): FeatureIndex = new FeatureIndex(
      this.names.zipWithIndex.flatMap { case (n, i) => if (i != index) n.some else none[String] },
      this.types.zipWithIndex.flatMap { case (t, i) => if (i != index) t.some else none[FeatureType] },
      this.columnIndices.zipWithIndex.flatMap { case (ci, i) =>
        if (i == index) none[Int] else if (i > index) (ci - 1).some else ci.some
      }
    )

    override def toString: String =
      this.names.zip(this.types).map { case (n, t) => s"$n (${t.headerLineString})" } mkString ", "
  }

  /** A structure that keeps track of feature metadata (names, types and indices). This is needed
    * because some methods are only applicable to a certain type of features, e.g. [0, 1] scaling
    * only makes sense for numerical features. */
  object FeatureIndex {

    /** Construct feature index with `n` categorical features. Feature names are generated automatically - `i`th
      * feature gets assigned the name "f`i`" (using 0-based counting).
      * @param n number of categorical features in feature index
      */
    def categorical(n: Int): FeatureIndex =
      categorical((0 until n).toList)

    def categorical(columnIndices: List[Int]): FeatureIndex =
      apply(columnIndices.indices.map(i => s"f$i").toList, columnIndices.map(_ => CategoricalFeature), columnIndices)

    /** Construct feature index with `n` numerical features. Feature names are generated automatically - `i`th
      * feature gets assigned the name "f`i`" (using 0-based counting).
      * @param n number of numerical features in feature index
      */
    def numerical(n: Int): FeatureIndex =
      numerical((0 until n).toList)

    def numerical(columnIndices: List[Int]): FeatureIndex =
      apply(columnIndices.indices.map(i => s"f$i").toList, columnIndices.map(_ => NumericalFeature), columnIndices)

    /** Construct feature index from feature types. Feature names are generated automatically - `i`th
      * feature gets assigned the name "f`i`" (using 0-based counting).
      * @param types list of feature types
      *
      * @example Construct a feature index with one numerical and two categorical features.
      *   {{{
      *     import io.picnicml.doddlemodel.data.Feature.{FeatureIndex, NumericalFeature, CategoricalFeature}
      *     val featureIndex = FeatureIndex(List(CategoricalFeature, NumericalFeature, CategoricalFeature))
      *   }}}
      */
    def apply(types: List[FeatureType]): FeatureIndex =
      apply(types.indices.map(i => s"f$i").toList, types, types.indices.toList)

    def apply(types: List[FeatureType], columnIndices: List[Int]): FeatureIndex =
      apply(types.indices.map(i => s"f$i").toList, types, columnIndices)

    /** Construct a feature index with custom feature names, types and column indices.
      * @param names feature names
      * @param types feature types
      * @param columnIndices column index for each feature
      *
      * @example Construct a feature index with three features, named "age" (numerical), "height" (numerical)
      *          and "group" (categorical).
      *   {{{
      *     import io.picnicml.doddlemodel.data.Feature.{FeatureIndex, NumericalFeature, CategoricalFeature}
      *     val featureIndex = FeatureIndex(List("age", "height", "group"), List(NumericalFeature,
      *       NumericalFeature, CategoricalFeature), List(0, 1, 2))
      *   }}}
      */
    def apply(names: List[String], types: List[FeatureType], columnIndices: List[Int]): FeatureIndex =
      new FeatureIndex(names.toIndexedSeq, types.toIndexedSeq, columnIndices.toIndexedSeq)
  }
}
