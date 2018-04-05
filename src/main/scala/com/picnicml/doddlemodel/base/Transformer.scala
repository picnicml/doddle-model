package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.Features

abstract class Transformer extends Estimator {
  this: Serializable =>

  def fit(x: Features): Transformer
  def transform(x: Features): Features
}
