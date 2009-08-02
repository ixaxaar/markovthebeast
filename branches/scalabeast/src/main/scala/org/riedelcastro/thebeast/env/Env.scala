package org.riedelcastro.thebeast.env


import collection.mutable.{MapProxy, HashSet}

/**
 * @author Sebastian Riedel
 */
trait Env {
  def apply[T](term: Term[T]): T = term.eval(this).get

  //todo: should this be removed?
  def eval[T](term: Term[T]): Option[T] = term.eval(this)

  def resolveVar[T](variable: EnvVar[T]): Option[T]

  def mask(hiddenVariables: Set[EnvVar[_]]) = new MaskedEnv(this, hiddenVariables);

  def overlay(over:Env) = new OverlayedEnv(this,over)

  def variables:Set[EnvVar[_]]

}

case class EnvComparison(env1:Env, env2:Env) {

  //we compare composed functions by counting how often, for a given value v in the range,
  //both env1 and env2 map the same sequence of arguments (composed) to v
  //this amounts to true positives (v=true) and true negatives (v=false) for functions
  //mapping to the Bools (i.e. V1->V2->...->Bools)
  //if values are of atomic type T, we consider them of type T->{Nothing}

  def overlap[T,V](funVar:Var[T=>V], value:V) : Int = {
    //this is probably the slowest implementation possible
    var functionValues = funVar.values.asInstanceOf[FunctionValues[T,V]]
    var count = 0
    for (arg <- functionValues.domain){
      val v1 = env1.resolveVar(FunAppVar(funVar,arg))
      val v2 = env2.resolveVar(FunAppVar(funVar,arg))
      if (v1 == value && v1 == v2) count += 1
    }
    count
  }

}



class OverlayedEnv(val under:Env, val over:Env) extends Env {
  def resolveVar[T](variable: EnvVar[T]) = {
    over.resolveVar(variable) match {
      case Some(x) => Some(x)
      case None => under.resolveVar(variable)
    }
  }


  def variables = over.variables ++ under.variables
}

class MaskedEnv(var unmasked: Env, var hiddenVariables: Set[EnvVar[_]]) extends Env {
  def resolveVar[T](variable: EnvVar[T]) = {
    if (hiddenVariables.contains(variable)) None else unmasked.resolveVar(variable)
  }

  //todo: this doesn't work if hiddenVariables contain funapp vars
  def variables = unmasked.variables -- hiddenVariables
}

private class MutableMap extends scala.collection.mutable.HashMap[Any, Any] {
  private class ClosedMutableMap(var self: MutableMap, signature: FunctionValues[_, _])
          extends MutableMap with MapProxy[Any, Any] {
    override def default(a: Any) = signature.range.defaultValue

    override def apply(a: Any) = self.get(a) match {
      case Some(x: MutableMap) => x.close(signature.range.asInstanceOf[FunctionValues[_, _]])
      case Some(_) => super.apply(a)
      case None => default(a)
    }

  }

  def close(signature: FunctionValues[_, _]): MutableMap = {
    new ClosedMutableMap(this, signature)
  }

}

class MutableEnv extends Env {
  private type MutableMapCheck = scala.collection.mutable.HashMap[_, _]

  private var values = new MutableMap
  private var closed = new HashSet[EnvVar[_]]

  def resolveVar[T](variable: EnvVar[T]) = {
    var result = variable match {
      case v: Var[_] => values.get(variable).asInstanceOf[Option[T]]
      case FunAppVar(funVar, arg) => getMap(funVar).get(arg).asInstanceOf[Option[T]]
    }
    if (closed.contains(variable)) convertToClosed(variable, result) else result
  }

  def convertToClosed[T](variable: EnvVar[T], value: Option[T]): Option[T] = {
    value match {
      case Some(x: MutableMap) => Some(x.close(variable.values.asInstanceOf[FunctionValues[_, _]]).asInstanceOf[T])
      case Some(_) => value
      case None => Some(variable.values.defaultValue)
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

  def close(variable: EnvVar[_], closed: Boolean) {
    if (closed) this.closed += variable else this.closed.removeEntry(variable)
  }


  private def cloneMutableMap(map: MutableMap): MutableMap = {
    val result = new MutableMap
    map foreach {
      case (key, value) =>
        if (value.isInstanceOf[MutableMapCheck])
          result += (key -> cloneMutableMap(value.asInstanceOf[MutableMap]))
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


  def mapTo[T](envVar: EnvVar[T]) = new VarWithEnv(envVar, this)

  def variables = values.keySet.asInstanceOf[Set[EnvVar[_]]]
}

case class VarWithEnv[T](envVar: EnvVar[T], env: MutableEnv) {
  def ->(t: T) = env.set(envVar, t)
}

case class MapToBuilder[T, R](val funVar: EnvVar[T => R], val env: MutableEnv) {
  def apply(t: T) = VarWithEnv(FunAppVar(funVar, t), env)
}