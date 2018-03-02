package com.doddlemodel.metrics

import com.doddlemodel.data.Types.Target

object Types {

  type Metric[A] = (Target[A], Target[A]) => Double
}
