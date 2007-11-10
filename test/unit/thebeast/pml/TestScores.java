package thebeast.pml;

import junit.framework.TestCase;
import thebeast.pml.fixtures.AlignmentFixtures;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Nov-2007 Time: 22:37:02
 */
public class TestScores extends TestCase {

  public void testScoring() {
    Model model = AlignmentFixtures.createAlignmentModel();
    GroundAtoms atoms = model.getSignature().createGroundAtoms();
    Weights weights = model.getSignature().createWeights();
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
    Scores scores = new Scores(model, weights);
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights);
    LocalFeatures features = new LocalFeatures(model, weights);
    System.out.println(atoms);
    extractor.extract(atoms, features);
    System.out.println(features);
    scores.score(features, atoms);
    System.out.println(scores);
    for (int s = 0; s < atoms.getGroundAtomsOf("source").size(); ++s)
      for (int t = 0; t < atoms.getGroundAtomsOf("target").size(); ++t)
        assertTrue(scores.contains(model.getSignature().getUserPredicate("align"), s == t ? 1.0 : 0.0, s, t));
  }

  public void testScoringWithUndefined() {
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
    Scores scores = new Scores(model, weights);
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights);
    LocalFeatures features = new LocalFeatures(model, weights);
    System.out.println(atoms);
    extractor.extract(atoms, features);
    System.out.println(features);
    scores.score(features, atoms);
    System.out.println(scores);
    for (int s = 0; s < atoms.getGroundAtomsOf("source").size(); ++s)
      for (int t = 0; t < atoms.getGroundAtomsOf("target").size(); ++t)
        assertTrue(scores.contains(model.getSignature().getUserPredicate("align"), s == t ? 1.0 : -1.0, s, t));
  }



}
