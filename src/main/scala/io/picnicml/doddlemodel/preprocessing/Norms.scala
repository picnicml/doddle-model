package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{Axis, max, sum}
import breeze.numerics.{abs, pow, sqrt}
import io.picnicml.doddlemodel.data.{Features, RealVector}

object Norms {

  sealed trait Norm {
    def apply(x: Features): RealVector
  }

  final case object L1Norm extends Norm {
    override def apply(x: Features): RealVector = sum(abs(x), Axis._1)
  }

  final case object L2Norm extends Norm {
    override def apply(x: Features): RealVector = sqrt(sum(pow(x, 2), Axis._1))
  }

  final case object MaxNorm extends Norm {
    override def apply(x: Features): RealVector = max(abs(x), Axis._1)
  }
}
