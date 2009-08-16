package org.riedelcastro.thebeast.env.vectors


import collection.mutable.{HashMap}
/**
 * @author Sebastian Riedel
 */

class Vector {

  private val store = new HashMap[Any,Double]

  def set(value:Double, keys:Any*){
    store += (keys.toList -> value)
  }

  def setWithSingleKey(value:Double, key:Any){
    store += (key -> value)
  }


  private def unpackIfSingle(args:Any*) = if (args.length == 1) args(0) else args

  def get(keys:Any*) : Double = store.getOrElse(keys.toList,0.0)

  def getWithSingleKey(key:Any) : Double = store.getOrElse(key,0.0)


  def add(that:Vector, scale:Double) : Vector = {
    val result = new Vector
    result.addInPlace(this, 1.0)
    result.addInPlace(that, scale)
    result
  }

  def scalar(scale:Double) : Vector = {
    val result = new Vector
    result.addInPlace(this, scale)
    result
  }

  def addInPlace(that:Vector, scale:Double) : Unit = {
    for (entry <- store.elements)
      setWithSingleKey(entry._2 + scale * that.getWithSingleKey(entry._1), entry._1)
    for (entry <- that.store.elements)
      if (!store.keySet.contains(entry._1)) store += (entry._1 -> entry._2 * scale)
  }

  def dot(that:Vector) : Double = {
    store.foldLeft(0.0) {(score,keyValue)=>  score + keyValue._2 * that.getWithSingleKey(keyValue._1)} 
  }


  override def toString =
    store.elements.foldLeft("") {(s,e)=>
            s + e._1.asInstanceOf[Collection[_]].mkString(",")+ "\t" + e._2.toString + "\n" } 
}



case object VectorAdd extends (Vector=>(Vector=>Vector)){
  def apply(lhs:Vector) = (rhs:Vector) => lhs.add(rhs,1.0)


}

case object VectorDot extends (Vector=>(Vector=>Double)){
  def apply(lhs:Vector) = (rhs:Vector) => lhs.dot(rhs)
}

case object VectorScalar extends (Vector=>(Double=>Vector)){
  def apply(lhs:Vector) = (rhs:Double) => lhs.scalar(rhs)
}

object VectorSpace extends Values[Vector] {
  def elements = throw new Error("Can't iterate over all vectors")
  override def defaultValue = VectorZero
  override def randomValue = throw new Error("Space too large for randomly drawing an element")
}

object VectorZero extends Vector {
  override def addInPlace(that: Vector, scale: Double) = throw new Error("Cannot change the zero vector")
}

object VectorDemo extends Application with TheBeastEnv {

  val vector = new Vector
  vector.set(2.0, "blah", 1)
  vector.set(-1.0, 200, "pups", true)

  println(vector)

  val Bools = Values(true, false)  
  val Persons = Values("Anna", "Peter", "Nick", "Ivan")
  val smokes = "smokes" <~ Persons -> Bools;
  val cancer = "cancer" <~ Persons -> Bools;
  val friends = "friends" <~ Persons -> (Persons -> Bools);

  val weights = "w" <~ VectorSpace

  val f1 = sum(Persons) {x => $ {smokes(x) -> cancer(x)} * 0.1}
  val f2 = sum(Persons) {x => sum(Persons) {y => $ {friends(x)(y) && smokes(x) -> smokes(y)} * 0.1}}
  val f3 = vectorSum(Persons) {x => $ {smokes(x) -> cancer(x)} * VectorOne(x)}
  //val mln = f3 dot weights

  //it should be possible to move the dot product into the summation, replacing VectorOne(x) with weights(x)
  
}

