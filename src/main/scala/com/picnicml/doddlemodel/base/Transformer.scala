package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.Features

abstract class Transformer[A <: Transformer[A]] extends Estimator {
  this: Serializable =>

  def fit(x: Features): A
  def transform(x: Features): Features
}
