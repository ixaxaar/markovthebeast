package thebeast.pml;

import junit.framework.TestCase;
import thebeast.pml.fixtures.AlignmentFixtures;

/**
 * @author Sebastian Riedel
 */
public class TestSolution extends TestCase {

  public void testFeatureExtractionWithBinaryFeatures(){
    Model model = AlignmentFixtures.createAlignmentModel();
    GroundAtoms atoms = model.getSignature().createGroundAtoms();
    Weights weights = model.getSignature().createWeights();
    AlignmentFixtures.setModel1Weight(weights, 0.2);
    AlignmentFixtures.setWordPairWeight(weights, "Ich", "I", 1.0);
    AlignmentFixtures.setWordPairWeight(weights, "bin", "am", 2.0);
    AlignmentFixtures.setWordPairWeight(weights, "Sebastian", "Sebastian", 3.0);
    AlignmentFixtures.setSentences(atoms, 4, 4,
            "NULL", "Ich", "bin", "Sebastian",
            "NULL", "I", "am", "Sebastian");
    AlignmentFixtures.setAlignment(atoms,1,1,2,2,3,3);

    Solution solution = new Solution(model, weights);
    solution.load(atoms);
    FeatureVector vector = solution.extract();
    assertEquals(3, vector.getAll().size());
    assertTrue(vector.getAll().contains(1,1.0));
    assertTrue(vector.getAll().contains(2,1.0));
    assertTrue(vector.getAll().contains(3,1.0));

  }


  public void testFeatureExtractionWithRealValuedFeatures(){
    Model model = AlignmentFixtures.createAlignmentModel();
    GroundAtoms atoms = model.getSignature().createGroundAtoms();
    Weights weights = model.getSignature().createWeights();
    AlignmentFixtures.setModel1Weight(weights, 0.2);
    AlignmentFixtures.setSentences(atoms, 4, 4,
            "NULL", "Ich", "bin", "Sebastian",
            "NULL", "I", "am", "Sebastian");
    AlignmentFixtures.setModel1(atoms, 4,4,
            0.5,0,0,0,
            0,0.5,0,0,
            0,0,0.5,0,
            0,0,0,0.5);
    AlignmentFixtures.setAlignment(atoms,1,1,2,2,3,3);

    Solution solution = new Solution(model, weights);
    solution.load(atoms);
    FeatureVector vector = solution.extract();
    assertTrue(vector.getAll().contains(0,1.5));
    System.out.println(vector.getAll());

  }

}
