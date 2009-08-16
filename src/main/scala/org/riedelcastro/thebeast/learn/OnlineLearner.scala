package org.riedelcastro.thebeast.learn


import env._
import solve.{ExhaustiveSearch, ArgmaxSolver}
import util.Trackable
import vectors.{VectorTerm, Vector}
/**
 * @author Sebastian Riedel
 */

class OnlineLearner extends Trackable {
  var solver: ArgmaxSolver = ExhaustiveSearch
  var updateRule: UpdateRule = new PerceptronUpdateRule
  var maxEpochs: Int = 1

  def learn(featureVector: VectorTerm, trainingSet: Seq[MaskedEnv]): Vector = {
    |**("Learning"); 
    var weights = new Vector
    for (epoch <- 0 until maxEpochs) {
      for (instance <- trainingSet) {
        |**("Instance")
        var goldFeatures = instance.unmasked(featureVector)
        var conditionedScore = (featureVector dot weights).ground(instance)
        var guess = solver.argmax(conditionedScore).result
        var guessFeatures = instance.overlay(guess)(featureVector)
        updateRule.update(goldFeatures, guessFeatures, 0.0, weights)
        **|
      }
    }
    **|
    weights
  }

  trait UpdateRule {
    def update(gold: Vector, guess: Vector, loss: Double, weights: Vector)
  }

  class PerceptronUpdateRule extends UpdateRule {
    var learningRate = 1.0

    def update(gold: Vector, guess: Vector, loss: Double, weights: Vector) = {
      weights.addInPlace(gold.add(guess, -1.0), learningRate)
    }
  }
}

