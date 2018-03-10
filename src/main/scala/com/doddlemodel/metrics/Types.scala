package com.doddlemodel.metrics

import com.doddlemodel.data.Types.Target

object Types {

  type Metric = (Target, Target) => Double
}
