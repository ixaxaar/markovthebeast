package org.riedelcastro.thebeast.env.tuples

/**
 * @author Sebastian Riedel
 */

trait TupleTerm extends scala.Product {
  def isGround : Boolean = {
    for (i <- 0 until productArity) if (!productElement(i).asInstanceOf[Term[Any]].isGround) return false;
    return true;
  }
}

case class TupleTerm2[T1,T2](_1:Term[T1],_2:Term[T2]) extends Term[Tuple2[T1,T2]] with TupleTerm {
  def eval(env: Env) = {
    val arg1: Option[T1] = _1.eval(env)
    val arg2: Option[T2] = _2.eval(env)
    if (arg1 != None && arg2 != None) Some(Tuple2(arg1.get, arg2.get)) else None
  }

  def values = TupleValues2(_1.values,_2.values)

  def ground(env: Env) = TupleTerm2(_1.ground(env),_2.ground(env))

  def simplify = if (_1.isInstanceOf[Constant[_]] && _2.isInstanceOf[Constant[_]])
    Constant(Tuple2(_1.asInstanceOf[Constant[T1]].value,_2.asInstanceOf[Constant[T2]].value ))
    else this

  def variables = _1.variables ++ _2.variables
}

case class TupleTerm3[T1,T2,T3](_1:Term[T1],_2:Term[T2],_3:Term[T3]) extends Term[Tuple3[T1,T2,T3]] with TupleTerm {
  def eval(env: Env) = {
    val arg1: Option[T1] = _1.eval(env)
    val arg2: Option[T2] = _2.eval(env)
    val arg3: Option[T3] = _3.eval(env)
    if (arg1 != None && arg2 != None && arg3 != None) Some(Tuple3(arg1.get, arg2.get,arg3.get)) else None
  }

  def values = TupleValues3(_1.values,_2.values, _3.values)

  def ground(env: Env) = TupleTerm3(_1.ground(env),_2.ground(env), _3.ground(env))

  def simplify = if (_1.isInstanceOf[Constant[_]] && _2.isInstanceOf[Constant[_]] && _3.isInstanceOf[Constant[_]])
    Constant(Tuple3(_1.asInstanceOf[Constant[T1]].value,_2.asInstanceOf[Constant[T2]].value, _3.asInstanceOf[Constant[T3]].value))
    else this

  def variables = _1.variables ++ _2.variables ++ _3.variables
}

 