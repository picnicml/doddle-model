package com.doddle.base

import com.doddle.Types.RealMatrix

abstract class Transformer extends Estimator {

  def fit(x: RealMatrix): Unit
  def transform(x: RealMatrix): RealMatrix
}
