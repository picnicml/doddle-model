package com.picnicml.doddlemodel.base

import com.picnicml.doddlemodel.data.Features

abstract class Transformer extends Estimator {

  def fit(x: Features): Transformer
  def transform(x: Features): Features
}
