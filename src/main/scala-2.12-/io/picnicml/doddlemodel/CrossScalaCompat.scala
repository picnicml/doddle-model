package io.picnicml.doddlemodel

import scala.math.Ordering

object CrossScalaCompat {

  type LazyListCompat[A] = Stream[A]
  def lazyListCompatFromSeq[A](seq: Seq[A]): LazyListCompat[A] = seq.to[Stream]

  implicit lazy val floatOrdering: Ordering[Float] = Ordering.Float
  implicit lazy val doubleOrdering: Ordering[Double] = Ordering.Double
}
