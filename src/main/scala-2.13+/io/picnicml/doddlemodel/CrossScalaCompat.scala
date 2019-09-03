package io.picnicml.doddlemodel

import scala.math.Ordering.Double.TotalOrdering

object CrossScalaCompat {

  type LazyListCompat[A] = LazyList[A]
  def lazyListCompatFromSeq[A](seq: Seq[A]): LazyListCompat[A] = seq.to(LazyList)

  implicit lazy val doubleOrdering: TotalOrdering = TotalOrdering
}
