package com.doddle

import breeze.linalg.zipValues
import com.doddle.data.DataTypes.RealVector
import org.scalactic.Equality

trait TestUtils {

  def breezeEqual(x0: RealVector, x1: RealVector)(implicit tol: Equality[Double]): Boolean =
    zipValues(x0, x1).forall((v0, v1) => tol.areEquivalent(v0, v1))
}
