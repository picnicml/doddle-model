package io.picnicml.doddlemodel

import io.picnicml.doddlemodel.pipeline.Pipeline.Transformable

package object pipeline {

  type PipelineTransformers = List[Transformable[_]]
}
