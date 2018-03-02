package com.doddlemodel.base

import com.doddlemodel.data.Types.Features

abstract class Transformer extends Estimator {

  def fit(x: Features): Transformer
  def transform(x: Features): Features
}
