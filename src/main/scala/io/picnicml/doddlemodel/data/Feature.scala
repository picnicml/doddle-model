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

    def subset(names: String*): FeatureIndex = {
      val nameToIndex = this.names.zipWithIndex.toMap
      subset(names.map(n => nameToIndex(n)):_*)
    }

    def subset(indices: IndexedSeq[Int]): FeatureIndex = subset(indices:_*)

    // DummyImplicit is needed to avoid the same type as String* after erasure
    def subset(indices: Int*)(implicit di: DummyImplicit): FeatureIndex = new FeatureIndex(
      indices.toIndexedSeq.map(i => this.names(i)),
      indices.toIndexedSeq.map(i => this.types(i)),
      indices.toIndexedSeq.map(i => this.columnIndices(i))
    )

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
