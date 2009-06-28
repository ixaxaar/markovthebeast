package org.riedelcastro.thebeast.learn


import env._
import solve.{GeneralizedMaxWalkSAT, ArgmaxSolver}
/**
 * @author Sebastian Riedel
 */

class OnlineLearner {

  var solver: ArgmaxSolver = new GeneralizedMaxWalkSAT
  var hiddenVariables: Set[EnvVar[_]] = Set()
  var updateRule: UpdateRule = null

  def learn(score:DoubleTerm, featureVector:VectorTerm, trainingSet:Seq[Env]) : Vector = {
    var result = new Vector
    for (instance <- trainingSet) {
      var goldFeatures = instance(featureVector)
      //ground observed variables in scoring function and feature function
      var conditionedScore = score.ground(instance.mask(hiddenVariables))
      var guess = solver.argmax(conditionedScore).result
      var guessFeatures = guess(featureVector)
      updateRule.update(goldFeatures,guessFeatures, 0.0, result)
    }
    result
  }

  trait UpdateRule {
    def update(gold:Vector, guess:Vector, loss:Double, weights:Vector)
  }

}

