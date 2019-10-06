package io.picnicml.doddlemodel

import scala.math.Ordering.Double.{TotalOrdering => DoubleTotalOrdering}
import scala.math.Ordering.Float.{TotalOrdering => FloatTotalOrdering}

object CrossScalaCompat {

  type LazyListCompat[A] = LazyList[A]
  def lazyListCompatFromSeq[A](seq: Seq[A]): LazyListCompat[A] = seq.to(LazyList)

  implicit lazy val floatOrdering: FloatTotalOrdering = FloatTotalOrdering
  implicit lazy val doubleOrdering: DoubleTotalOrdering = DoubleTotalOrdering
}
