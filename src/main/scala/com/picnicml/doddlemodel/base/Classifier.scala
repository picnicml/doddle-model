package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.{Features, Simplex, Target}

abstract class Classifier extends Predictor {
  this: Serializable =>

  val numClasses: Option[Int]
  def fit(x: Features, y: Target): Classifier
  def predictProba(x: Features): Simplex
}
