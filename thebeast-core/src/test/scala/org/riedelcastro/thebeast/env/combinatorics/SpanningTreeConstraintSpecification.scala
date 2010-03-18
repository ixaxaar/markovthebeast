package org.riedelcastro.thebeast.env.combinatorics

import org.specs.Specification
import org.riedelcastro.thebeast.DependencyParsingFixtures
import org.specs.runner.{JUnit4}
import org.riedelcastro.thebeast.env.{Constant, LessThan, FunAppVar, TheBeastEnv}

/**
 * @author sriedel
 */

class SpanningTreeConstraintTest extends JUnit4(SpanningTreeConstraintSpecification)
object SpanningTreeConstraintSpecification extends Specification with TheBeastEnv {
  "A projective spanning tree constraint" should {
    "return 1 if the the graph is a spanning tree" in {
      val fixtures = new DependencyParsingFixtures
      import fixtures._
      val sentence = createTheManIsFast
      val constraint = new SpanningTreeConstraint[Int](link, token, 0, LessThan(Tokens))
      sentence(constraint) must_== 1.0
    }
    "return 0 if the the graph has a cycle" in {
      val fixtures = new DependencyParsingFixtures
      import fixtures._
      val sentence = createSentence(
        List("root","the","man" ,"walks"),
        List("root","DT","NN", "VB"),
        List((0,3),(1,2),(2,1)))
      val constraint = new SpanningTreeConstraint(link, token, 0, LessThan(Tokens))
      sentence(constraint) must_== 0.0
    }
    "return 0 if the the graph has a vertices with multiple parents" in {
      val fixtures = new DependencyParsingFixtures
      import fixtures._
      val sentence = createSentence(
        List("root","the","man" ,"walks"),
        List("root","DT","NN", "VB"),
        List((0,3),(1,2),(3,2)))
      val constraint = new SpanningTreeConstraint(link, token, 0, LessThan(Tokens))
      sentence(constraint) must_== 0.0
    }
    "return 0 if the the graph is not spanning all vertices" in {
      val fixtures = new DependencyParsingFixtures
      import fixtures._
      val sentence = createSentence(
        List("root","the","man" ,"walks"),
        List("root","DT","NN", "VB"),
        List((0,3),(3,2)))
      val constraint = new SpanningTreeConstraint(link, token, 0, LessThan(Tokens))
      sentence(constraint) must_== 0.0
    }
    "return 0 if the the graph is not projective" in {
      val fixtures = new DependencyParsingFixtures
      import fixtures._
      val sentence = createSentence(
        List("root","the","man" ,"walks"),
        List("root","DT","NN", "VB"),
        List((0,2),(2,3),(3,1)))
      val constraint = new SpanningTreeConstraint(link, token, 0, LessThan(Tokens))
      true
      //sentence(constraint) must_== 0.0
    }
    "return only edge variables that could be part of a spanning tree if root and vertices are grounded" in {
      val fixtures = new DependencyParsingFixtures
      import fixtures._
      val sentence = createTheMan
      val constraint = new SpanningTreeConstraint(link, token, 0, LessThan(Tokens))
      val expected = Set(FunAppVar(link,(0,1)),FunAppVar(link,(0,2)),FunAppVar(link,(1,2)),FunAppVar(link,(2,1)))
      val result = constraint.ground(sentence.mask(Set(link))).variables
      result must_== expected
    }
  }

}


