package org.riedelcastro.thebeast.learn

import env._
import org.specs._
import solve.ExhaustiveSearch
import vectors.VectorOne
/**
 * @author Sebastian Riedel
 */

object OnlineLearnerSpecification extends Specification with TheBeastEnv {


  "An Online Learner" should {
    "converge with separable data and perceptron learning rule " in {
      val Bools = Values(false,true)
      val Citations = Values("A","B","C")
      val same = "same" <~ (Citations x Citations)-> Bools
      val similar = "similar" <~ (Citations x Citations) -> Bools

      //todo: factor out this training set
      var y1 = new MutableEnv
      y1.close(same, true)
      y1.close(similar, true)
      y1.mapTo(same)(("A","B")) -> true
      y1.mapTo(similar)(("A","B")) -> true

      var y2 = new MutableEnv
      y2.close(same, true)
      y2.close(similar, true)
      y2.mapTo(same)(("A","B")) -> true
      y2.mapTo(same)(("B","C")) -> true
      y2.mapTo(same)(("A","C")) -> true
      y2.mapTo(similar)(("A","B")) -> true
      y2.mapTo(similar)(("B","C")) -> true
      
      var features = 
        vectorSum(Citations,Citations)
                  {(c1,c2)=>$(similar(c1,c2) ~> same(c1,c2)) * VectorOne("similar")} +
        vectorSum(Citations,Citations,Citations)
                  {(c1,c2,c3)=>$((same(c1,c2) && same(c2,c3)) ~> same(c1,c3)) * VectorOne("trans")}
      var trainingSet = Seq(y1,y2).map(y => y.mask(Set(same)))

      var learner = new OnlineLearner

      learner.maxEpochs = 1

      var weights = learner.learn(features,trainingSet)

      for (y <- trainingSet){
        var score = (features dot weights).ground(y)
        var guess = ExhaustiveSearch.argmax(score).result
        println(y.unmasked(same).getSources(Some(true)))
        println(guess(same).getSources(Some(true)))
        y.unmasked(same).getSources(Some(true)) must_== guess(same).getSources(Some(true))

//        println(guess(same).)
      }

      println(weights)

      null
    }
  }
}