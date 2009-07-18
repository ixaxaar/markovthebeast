package org.riedelcastro.thebeast.env


import specs.runner.JUnit
import specs.Specification

/**
 * @author Sebastian Riedel
 */

class MutableEnvSpecification extends Specification with TheBeastEnv with JUnit {
  "A Mutable Environment" should {
    "set atomic variables to values" in {
      var x = "x" <~ Values(1,2,3)
      var env = new MutableEnv
      env += x -> 1
      env(x) must_== 1
    }
    "set partial function mappings" in {
      var f = "f" <~ Values(1,2,3) -> (Values("A","B") -> Values(true,false))
      var env = new MutableEnv
      env.mapTo(f)(1)("A") ->true
      env(f(1)("A")) must_== true
    }
    "mask an already set variable" in {
      var f = "f" <~ Values(1,2,3) -> (Values("A","B") -> Values(true,false))
      var env = new MutableEnv
      env.mapTo(f)(1)("A") ->true
      env.mask(Set(f)).eval(f(1)("A")) must_== None
    }

    "return None for unset variables" in {
      var f = "f" <~ Values(1,2,3) -> Values(true,false)
      var env = new MutableEnv
      env.eval(f(1)) must_== None      
    }
  }

}