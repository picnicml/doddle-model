package com.picnicml.doddlemodel.data

import scala.util.Random

object Utils {

  def shuffleDataset(x: Features, y: Target)(implicit rand: Random = new Random()): Dataset = {
    val shuffleIndices = rand.shuffle[Int, IndexedSeq](0 until y.length)
    (x(shuffleIndices, ::).toDenseMatrix, y(shuffleIndices).toDenseVector)
  }
}
