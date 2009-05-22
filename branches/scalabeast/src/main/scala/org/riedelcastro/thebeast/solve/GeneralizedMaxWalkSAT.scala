package org.riedelcastro.thebeast.solve


import env._

/**
 * @author Sebastian Riedel
 */

class GeneralizedMaxWalkSAT extends ArgmaxSolver {
  private val random = new Random

  def argmax(term: Term[Double]) = {
    //we can only do Fold(Add,...) terms
    term match {
      case Sum(args) => argmaxSum(args)
      case _ => ArgmaxResult(null, Status.CantDo)
    }
  }

  private def argmaxSum(args: Seq[DoubleTerm]): ArgmaxResult = {
    //get all variables
    val variables = args.flatMap(t => t.variables)
    val y = new MutableEnv
    //generate initial solution
    for (v <- variables) y += (v -> v.values.elements.next)

    for (flip <- 0 until 10) {
      //find all terms not at their optimum => need to know what the max of a term is
      val suboptimalTerms = args.filter(a => y(a) < a.upperBound)
      val violated = suboptimalTerms(random.nextInt(suboptimalTerms.size))
      val variable = if (Math.random < 0.5) violated.variables.elements.next else null
       
      ;

    }

    //pick a random term of these
    //randomly change a variable OR pick a variable that when changed results in largest increase
    null
  }


}

class UpperBoundCalculator {
  def upperBound[T](term: Term[T]): T = {
    term match {
      case Constant(x) => x
      case AddApp(x, y) => (upperBound(x) + upperBound(y)).asInstanceOf[T]
    //case FunApp(f,x) => upperBound(f)(upperBound(x))
    //how can I do something like:
    //case FunApp(FunApp(Constant(Add),x),y) => ...
    // without the compiler complaining about requiring Any=>(Any=>Double)
    //problem: only return type is inferable
    }
  }
}


