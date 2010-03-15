package org.riedelcastro.thebeast.env.combinatorics

import org.riedelcastro.thebeast.env.TheBeastEnv
import org.specs.Specification
import org.riedelcastro.thebeast.DependencyParsingFixtures
import org.specs.runner.{JUnit4}

/**
 * @author sriedel
 */

class SpanningTreeConstraintTest extends JUnit4(SpanningTreeConstraintSpecification)
object SpanningTreeConstraintSpecification extends Specification with TheBeastEnv {

  "A spanning tree constraint" should {
    "return 1 if the the graph is a spanning tree" in {
      val fixtures = new DependencyParsingFixtures
      import fixtures._
      val sentence = createTheManIsFast
      val constraint = new SpanningTreeConstraint(link,token)
      println(sentence(constraint))

    }
  }

}