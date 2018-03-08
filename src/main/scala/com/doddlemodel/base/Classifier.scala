package com.doddlemodel.base

import com.doddlemodel.data.Types.{Features, Simplex, Target}

abstract class Classifier extends Predictor[Int] {

  def fit(x: Features, y: Target[Int]): Classifier
  def predictProba(x: Features): Simplex
}
