package org.riedelcastro.thebeast.env.vectors

import org.riedelcastro.thebeast.env.TheBeastEnv
import org.specs.runner.{JUnit, JUnit4}
import org.specs.Specification


/**
 * @author Sebastian Riedel
 */


class VectorDotAppTest extends JUnit4(VectorDotAppSpecification)
object VectorDotAppSpecification extends Specification with TheBeastEnv with JUnit {
  "A Vector dot product application" should {
    "flatten its arguments when flattened" in {
      var dotproduct = (one_("A") + (one_("B") + one_("C") + one_("L"))) dot (one_("D") + one_("E"))
      var expected = (one_("A") + one_("B") + one_("C") + one_("L")) dot (one_("D") + one_("E"))
      dotproduct.flatten must_== expected
    }

  }
}