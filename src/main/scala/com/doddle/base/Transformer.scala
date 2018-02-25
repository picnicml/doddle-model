package com.doddle.base

import com.doddle.data.DataTypes.Features

abstract class Transformer extends Estimator {

  def fit(x: Features): Transformer
  def transform(x: Features): Features
}
