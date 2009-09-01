package org.riedelcastro.thebeast.solve

import env.doubles._
import env.{TheBeastEnv}
import specs.Specification
import org.specs.runner.JUnit4


/**
 * @author Sebastian Riedel
 */
class mySpecTest extends JUnit4(SumProductBeliefPropagationSpecification)

object SumProductBeliefPropagationSpecification extends Specification with TheBeastEnv with AppleTreeFixtures {
  "Sum Product BP" should {
    "calculate exact marginals in a tree" in {

      val inference = new SumProductBeliefPropagation
      val result = inference.infer(appleTreeModel)
      result.belief(Sick).belief(true) must beCloseTo (beliefForSick, 0.00001)
      result.belief(Loses).belief(true) must beCloseTo (beliefForLoses, 0.00001)

    }
  }

}