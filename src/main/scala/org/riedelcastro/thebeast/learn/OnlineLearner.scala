package org.riedelcastro.thebeast.learn


import env._
import solve.{ExhaustiveSearch, ArgmaxSolver}
/**
 * @author Sebastian Riedel
 */

class OnlineLearner {

  var solver: ArgmaxSolver = ExhaustiveSearch
  var updateRule: UpdateRule = new PerceptronUpdateRule

  def learn(featureVector:VectorTerm, trainingSet:Seq[MaskedEnv]) : Vector = {
    var weights = new Vector
    for (instance <- trainingSet) {
      var goldFeatures = instance.unmasked(featureVector)
      //ground observed variables in scoring function and feature function
      var conditionedScore = (featureVector dot weights).ground(instance)
      var guess = solver.argmax(conditionedScore).result
      var guessFeatures = guess(featureVector)
      updateRule.update(goldFeatures,guessFeatures, 0.0, weights)
    }
    weights
  }

  trait UpdateRule {
    def update(gold:Vector, guess:Vector, loss:Double, weights:Vector)
  }

  class PerceptronUpdateRule extends UpdateRule {

    var learningRate = 1.0

    def update(gold: Vector, guess: Vector, loss: Double, weights: Vector) = {
      weights.addInPlace(gold.add(guess, -1.0), learningRate)
    }
  }
}

