package com.picnicml.doddlemodel.data

import breeze.linalg.shuffle

object Utils {

  def shuffleDataset(x: Features, y: Target): Dataset = {
    val shuffleIndices = shuffle(0 until y.length)
    (x(shuffleIndices, ::).toDenseMatrix, y(shuffleIndices).toDenseVector)
  }
}
