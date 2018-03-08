package com.doddlemodel.base

import com.doddlemodel.data.Types.{Features, Target}

abstract class Regressor[A] extends Predictor[A] {

  def fit(x: Features, y: Target[A]): Regressor[A]
}
