package org.riedelcastro.thebeast.alchemy


import env.TheBeastEnv
import java.io.StringReader
import specs.Specification

/**
 * @author Sebastian Riedel
 */

class MLNSpecification extends Specification with TheBeastEnv with AlchemySmokingFixtures {


  "An MLN" should {
    "be extended by predicates and types when loading an alchemy file with the corresponding predicate definitions" in {
      val mln = new MLN
      mln.loadMLN(new StringReader(smokingSignature))
      mln.getPredicate("Friends").values.domain.arity must_== 2
      mln.getPredicate("Smokes").values.domain.arity must_== 1
      mln.getPredicate("Cancer").values.domain.arity must_== 1
      mln.getType("person").size must_== 0
    }
    "load the formulae of an alchemy file" in {
      val mln = new MLN
      mln.loadMLN(new StringReader(smokingSignature + smokingRules))
      println(mln.getFormula(0))
      println(mln.getFormula(1))

    }

  }

  

}