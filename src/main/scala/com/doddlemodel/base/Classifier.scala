package com.doddlemodel.base

import com.doddlemodel.data.Types.{Features, Simplex, Target}

abstract class Classifier extends Predictor {

  def fit(x: Features, y: Target): Classifier
  def predictProba(x: Features): Simplex
}
