package org.riedelcastro.thebeast.solve


import env._

/**
 * @author Sebastian Riedel
 */

class GeneralizedMaxWalkSAT extends ArgmaxSolver {
  def argmax(term: Term[Double]) = {
    //we can only do Fold(Add,...) terms
    term match {
      case Fold(Constant(Add), args, init) => argmax(args ++ Seq(init))
      case _ => ArgmaxResult(null, Status.CantDo)
    }
  }

  private def argmax(args: Seq[Term[Double]]): ArgmaxResult = {
    //get all variables
    //generate initial solution
    //find all terms not at their optimum => need to know what the max of a term is
    //pick a random term of these
    //randomly change a variable OR pick a variable that when changed results in largest increase
    null
  }


}

class UpperBoundCalculator {
  def upperBound[T](term: Term[T]):T = {
    term match {
      case Constant(x) => x
      case AddApp(x,y) => (upperBound(x) + upperBound(y)).asInstanceOf[T] 
      //case FunApp(f,x) => upperBound(f)(upperBound(x))
      //how can I do something like:
      //case FunApp(FunApp(Constant(Add),x),y) => ...
      // without the compiler complaining about requiring Any=>(Any=>Double)
      //problem: only return type is inferable 
    }
  }
}


