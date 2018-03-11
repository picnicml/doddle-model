package com.picnicml.doddlemodel.metrics

import com.picnicml.doddlemodel.data.Types.Target

object Types {

  type Metric = (Target, Target) => Double
}
