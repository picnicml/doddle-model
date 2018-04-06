package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.{Features, Simplex, Target}

abstract class Classifier[A <: Classifier[A]] extends Predictor[A] {
  this: Serializable =>

  val numClasses: Option[Int]
  def fit(x: Features, y: Target): A
  def predictProba(x: Features): Simplex
}
