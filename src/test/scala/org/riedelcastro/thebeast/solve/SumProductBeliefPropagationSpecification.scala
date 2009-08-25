package org.riedelcastro.thebeast.solve


import env.doubles._
import env.tuples.TupleValues2
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
      val SickParameters = PriorPDParams(Bools,(true->0.1))
      val DryParameters = PriorPDParams(Bools,(true->0.1))
      val LosesParameters = CPDParams(Bools, Bools x Bools,
        (true, (false,false))-> 0.5, (true, (false,true)) -> 0.1,
        (true, (true,false)) -> 0.1, (true, (true,true)) -> 0.1)
      val model = Multiplication(Seq(
        PriorPD(Sick, SickParameters),
        PriorPD(Dry, DryParameters),
        CPD(Loses|||(Sick, Dry), LosesParameters)
        ))

      val inference = new SumProductBeliefPropagation
      println(model)
      println(inference.infer(model))
    }
  }

}