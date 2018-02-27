package com.doddlemodel.base

import com.doddlemodel.data.DataTypes.Features

abstract class Transformer extends Estimator {

  def fit(x: Features): Transformer
  def transform(x: Features): Features
}
