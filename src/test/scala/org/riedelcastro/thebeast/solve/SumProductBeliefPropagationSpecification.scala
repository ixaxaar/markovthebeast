package org.riedelcastro.thebeast.solve


import env.doubles.{CPD, Product}
import env.{TheBeastEnv, Singleton}
import specs.Specification

/**
 * @author Sebastian Riedel
 */

class SumProductBeliefPropagationSpecification extends Specification with TheBeastEnv {
  "Sum Product BP" should {
    "calculate exact marginals in a tree" in {
      val Sick = "Sick" <~ Bools
      val Dry = "Dry" <~ Bools
      val Loses = "Loses" <~ Bools
      val SickParameters = Map((true, Singleton.Singleton)->0.1, (false,Singleton.Singleton) -> 0.9)
      val DryParameters = Map((true, Singleton.Singleton)-> 0.5, (false,Singleton.Singleton) -> 0.9)
      val LosesParameters = Map((true, (false,false))-> 0.5, (true, (false,true)) -> 0.1)
      val model = Product(Seq(
        CPD(Sick, SickParameters),
        CPD(Dry, DryParameters),
        CPD(Loses|||(Sick, Dry), LosesParameters)
        ))    
      println(model)
    }
  }

}