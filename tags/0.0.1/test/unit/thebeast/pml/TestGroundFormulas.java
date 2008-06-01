package thebeast.pml;

import junit.framework.TestCase;
import thebeast.pml.fixtures.AlignmentFixtures;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 13-Nov-2007 Time: 15:07:46
 */
public class TestGroundFormulas extends TestCase {

  public void testGlobalRealValuedFeature() {
    Model model = AlignmentFixtures.createAlignmentModel();
    AlignmentFixtures.addAlignedToSameTargetFormula(model, "w_sameTarget");
    Weights weights = model.getSignature().createWeights();
    weights.addWeight("w_sameTarget", -2.0);

    GroundAtoms atoms = model.getSignature().createGroundAtoms();
    AlignmentFixtures.setSentences(atoms, 4, 4,
            "NULL", "Ich", "bin", "Sebastian",
            "NULL", "I", "am", "Sebastian");
    AlignmentFixtures.setAlignment(atoms, 0, 0, 1, 0, 2, 0);

    GroundFormulas formulas = model.createGroundFormulas(weights);
    formulas.init();
    formulas.update(atoms);
    System.out.println(formulas);
    assertEquals(3, formulas.getTrueGroundFormulas(model.getFactorFormulas().get(0)).value().size());
    assertTrue(formulas.containsTrueGrounding(model.getFactorFormulas().get(0), 0, 1.0, 0, 1, 0));
    assertTrue(formulas.containsTrueGrounding(model.getFactorFormulas().get(0), 0, 2.0, 0, 2, 0));
    assertTrue(formulas.containsTrueGrounding(model.getFactorFormulas().get(0), 0, 1.0, 1, 2, 0));


  }

}
