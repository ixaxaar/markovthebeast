package org.riedelcastro.thebeast.env


import org.specs._

/**
 * @author Sebastian Riedel
 */
class MutableFunctionValueSpecification extends Specification with TheBeastEnv {
  "A mutable function value" should {
    "store a mapping" in {
      val f = new MutableFunctionValue(Values(1,2,3)->Values("A","B"))
      f(1) = "A"
      f(1) must_== "A"
    }
    "return None for keys that have not been set" in {
      val f = new MutableFunctionValue(Values(1,2,3)->Values("A","B"))
      f(1) = "A"
      f.get(2) must_== None
    }
    "provide a closed function that returns default values for unset keys" in {
      val f = new MutableFunctionValue(Values(1,2,3)->Values("A","B"))
      f(1) = "B"
      f.close(2) must_== "A"
    }
    "return all sources that map to a specific return value in" in {
      val f = new MutableFunctionValue(Values(1,2,3)->Values("A","B"))
      f(1) = "B"
      f.getSources(Some("B")) must_== Set(1)
    }
    "return all sources that are not maped to any value in" in {
      val f = new MutableFunctionValue(Values(1,2,3)->Values("A","B"))
      f(1) = "B"
      f.getSources(None) must_== Set(2,3)
    }

  }

}