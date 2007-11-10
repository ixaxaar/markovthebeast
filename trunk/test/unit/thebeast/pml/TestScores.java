package thebeast.pml;

import junit.framework.TestCase;
import thebeast.pml.formula.FormulaBuilder;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Nov-2007 Time: 22:37:02
 */
public class TestScores extends TestCase {
  protected Model model;
  protected Weights weights1;
  protected GroundAtoms problem1;
  protected UserPredicate align;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Signature signature = TheBeast.getInstance().createSignature();
    model = signature.createModel();
    signature.createType("SourceWord", true);
    signature.createType("TargetWord", true);
    align = signature.createPredicate("align", "Int", "Int");
    signature.createPredicate("source", "Int", "SourceWord");
    signature.createPredicate("target", "Int", "TargetWord");
    signature.createPredicate("m1", "Int", "Int", "Double");
    signature.createWeightFunction("w_m1");
    FormulaBuilder builder = new FormulaBuilder(signature);
    model.addFactorFormula(builder.parse("" + "factor: for Int s, Int t, Double p " +
            "if source(s,_) & target(t,_) & m1(s,t,p) add [align(s,t)] * p * w_m1"));

    model.addHiddenPredicate(signature.getUserPredicate("align"));
    model.addObservedPredicate(signature.getUserPredicate("source"));
    model.addObservedPredicate(signature.getUserPredicate("target"));
    model.addObservedPredicate(signature.getUserPredicate("m1"));

    problem1 = signature.createGroundAtoms();
    problem1.getGroundAtomsOf("source").addGroundAtom(0, "NULL");
    problem1.getGroundAtomsOf("source").addGroundAtom(1, "Ich");
    problem1.getGroundAtomsOf("source").addGroundAtom(2, "bin");
    problem1.getGroundAtomsOf("source").addGroundAtom(3, "Sebastian");

    problem1.getGroundAtomsOf("target").addGroundAtom(0, "NULL");
    problem1.getGroundAtomsOf("target").addGroundAtom(1, "I");
    problem1.getGroundAtomsOf("target").addGroundAtom(2, "am");
    problem1.getGroundAtomsOf("target").addGroundAtom(3, "Sebastian");

    for (int s = 0; s < 4; ++s) {
      for (int t = 0; t < 4; ++t) {
        problem1.getGroundAtomsOf("m1").addGroundAtom(s, t, s == t ? 0.5 : 0.0);
      }
    }
    weights1 = signature.createWeights();
    weights1.addWeight("w_m1", 0.2);
  }

  public void testScoring() {
    Scores scores = new Scores(model, weights1);
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights1);
    LocalFeatures features = new LocalFeatures(model, weights1);
    System.out.println(problem1);
    extractor.extract(problem1, features);
    System.out.println(features);
    scores.score(features, problem1);
    System.out.println(scores);
    for (int s = 0; s < problem1.getGroundAtomsOf("source").size(); ++s)
      for (int t = 0; t < problem1.getGroundAtomsOf("target").size(); ++t)
        assertTrue(scores.contains(align, s == t ? 0.1 : 0.0,s,t));
  }

}
