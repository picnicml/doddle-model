package com.doddle.base

import com.doddle.data.DataTypes.{Features, Simplex}

abstract class Classifier extends Predictor[Int] {

  def predictProba(x: Features): Simplex
}
