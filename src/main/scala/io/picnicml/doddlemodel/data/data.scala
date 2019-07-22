package io.picnicml.doddlemodel.data

case class TrainTestSplit(xTr: Features, yTr: Target, xTe: Features, yTe: Target)

case class GroupTrainTestSplit(xTr: Features,
                               yTr: Target,
                               groupsTr: IntVector,
                               xTe: Features,
                               yTe: Target,
                               groupsTe: IntVector)
