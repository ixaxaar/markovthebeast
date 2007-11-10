package thebeast.pml;

import junit.framework.TestCase;
import thebeast.pml.fixtures.AlignmentFixtures;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Nov-2007 Time: 22:37:02
 */
public class TestLocalFeatureExtractor extends TestCase {

  public void testExtractWithUndefined() {
    Model model = AlignmentFixtures.createAlignmentModel();
    AlignmentFixtures.addUndefinedWordPairFormula(model);
    GroundAtoms atoms = model.getSignature().createGroundAtoms();
    Weights weights = model.getSignature().createWeights();
    AlignmentFixtures.setUndefinedWordPairWeight(weights, -1.0);
    AlignmentFixtures.setModel1Weight(weights, 0.2);
    AlignmentFixtures.setWordPairWeight(weights, "NULL", "NULL", 0.9);
    AlignmentFixtures.setWordPairWeight(weights, "Ich", "I", 0.9);
    AlignmentFixtures.setWordPairWeight(weights, "bin", "am", 0.9);
    AlignmentFixtures.setWordPairWeight(weights, "Sebastian", "Sebastian", 0.9);
    AlignmentFixtures.setSentences(atoms, 4, 4,
            "NULL", "Ich", "bin", "Sebastian",
            "NULL", "I", "am", "Sebastian");
    AlignmentFixtures.setModel1(atoms, 4,4,
            0.5,0,0,0,
            0,0.5,0,0,
            0,0,0.5,0,
            0,0,0,0.5);
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights);
    LocalFeatures features = new LocalFeatures(model, weights);
    extractor.extract(atoms, features);
    for (int s = 0; s < 4; ++s)
      for (int t = 0; t < 4; ++t)
        assertEquals(s!=t, features.containsFeature(model.getSignature().getUserPredicate("align"),
                weights.getIndex(model.getSignature().getWeightFunction("w_undef")),s,t));
    System.out.println(features);
  }



}
