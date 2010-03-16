package org.riedelcastro.thebeast.env.combinatorics

import org.specs.Specification
import org.riedelcastro.thebeast.DependencyParsingFixtures
import org.specs.runner.{JUnit4}
import org.riedelcastro.thebeast.env.{FunAppVar, TheBeastEnv}

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
      val constraint = new SpanningTreeConstraint(link, token, 0)
      sentence(constraint) must_== 1.0
    }
    "return only edge variables that could be part of a spanning tree if root and vertices are grounded" in {
      val fixtures = new DependencyParsingFixtures
      import fixtures._
      val sentence = createTheMan
      val constraint = new SpanningTreeConstraint(link, token, 0)
      val expected = Set(FunAppVar(link,(0,1)),FunAppVar(link,(0,2)),FunAppVar(link,(1,2)),FunAppVar(link,(2,1)))
      val result = constraint.ground(sentence.mask(Set(link))).variables
      result must_== expected


    }
  }

}


