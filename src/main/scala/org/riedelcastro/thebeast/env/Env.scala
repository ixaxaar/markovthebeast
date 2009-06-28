package org.riedelcastro.thebeast.env

/**
 * @author Sebastian Riedel
 */
trait Env {
  def apply[T](term: Term[T]): T = term.eval(this).get

  def eval[T](term: Term[T]): Option[T] = {
    term match {
      case Constant(x) => Some(x)
      case v: Var[_] => resolveVar[T](v)
      case FunApp(funTerm, argTerm) =>
        {
          val fun = eval(funTerm);
          val arg = eval(argTerm);
          if (fun.isDefined && arg.isDefined) Some(fun.get(arg.get)) else None
        }
      case Fold(funTerm, argTerms, initTerm) =>
        if (argTerms.isEmpty)
          eval(initTerm)
        else
          eval(FunApp(FunApp(funTerm, Fold(funTerm, argTerms.drop(1), initTerm)), argTerms(0)))
      case x => eval(x.ground(this))
    }
  }


  def resolveVar[T](variable: EnvVar[T]): Option[T]

  def mask(hiddenVariables:Set[EnvVar[_]]) = new MaskedEnv(this,hiddenVariables);

}

class MaskedEnv(masked:Env, hiddenVariables:Set[EnvVar[_]]) extends Env {
  def resolveVar[T](variable: EnvVar[T]) = {
    if (hiddenVariables.contains(variable)) None else masked.resolveVar(variable)
  }
}

class MutableEnv extends Env {
  private type MutableMapCheck = scala.collection.mutable.HashMap[_, _]
  private type MutableMap = scala.collection.mutable.HashMap[Any, Any]
  private var values = new MutableMap

  def resolveVar[T](variable: EnvVar[T]) = {
    variable match {
      case v: Var[_] => values.get(variable).asInstanceOf[Option[T]]
      case FunAppVar(funVar, arg) => getMap(funVar).get(arg).asInstanceOf[Option[T]]
    }
  }

  private def getMap(variable: EnvVar[Any]): MutableMap = {
    variable match {
      case v: Var[_] => values.getOrElseUpdate(v, new MutableMap()).asInstanceOf[MutableMap]
      case FunAppVar(funVar, arg) => getMap(funVar).getOrElseUpdate(arg, new MutableMap()).asInstanceOf[MutableMap]
    }
  }


  override def clone = {
    val result = new MutableEnv
    result.values = cloneMutableMap(values)
    result
  }

  private def cloneMutableMap(map: MutableMap): MutableMap = {
    val result = new MutableMap
    map foreach {
      case (key, value) =>
        if (value.isInstanceOf[MutableMapCheck])
          result += (key ->cloneMutableMap(value.asInstanceOf[MutableMap]))
        else
          result += (key -> value)
    }
    result
  }

  def set[T](variable: EnvVar[T], value: T) {
    variable match {
      case v: Var[_] => values += Tuple2[Any, Any](v, value)
      case FunAppVar(funVar, arg) => getMap(funVar) += Tuple2[Any, Any](arg, value)
    }
  }

  def +=[T](mapping: Tuple2[EnvVar[T], T]) = set(mapping._1, mapping._2)

}