package com.doddlemodel.base

import com.doddlemodel.data.DataTypes.{Features, Simplex}

abstract class Classifier extends Predictor[Int] {

  def predictProba(x: Features): Simplex
}
