package com.picnicml.doddlemodel.base

import com.picnicml.doddlemodel.data.{Features, Simplex, Target}

abstract class Classifier extends Predictor {

  def fit(x: Features, y: Target): Classifier
  def predictProba(x: Features): Simplex
}
