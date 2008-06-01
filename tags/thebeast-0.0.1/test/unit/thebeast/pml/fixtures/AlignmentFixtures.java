package thebeast.pml.fixtures;

import thebeast.pml.*;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.FormulaBuilder;

/**
 * @author Sebastian Riedel
 */
public class AlignmentFixtures {

  protected Model model;
  protected Weights weights1;
  protected GroundAtoms problem1;
  protected UserPredicate align;

  public static Model createAlignmentModel(){
    Signature signature = TheBeast.getInstance().createSignature();
    Model model = signature.createModel();
    signature.createType("SourceWord", true);
    signature.createType("TargetWord", true);
    signature.createPredicate("align", "Int", "Int");
    signature.createPredicate("source", "Int", "SourceWord");
    signature.createPredicate("target", "Int", "TargetWord");
    signature.createPredicate("m1", "Int", "Int", "Double");

    model.addHiddenPredicate(signature.getUserPredicate("align"));
    model.addObservedPredicate(signature.getUserPredicate("source"));
    model.addObservedPredicate(signature.getUserPredicate("target"));
    model.addObservedPredicate(signature.getUserPredicate("m1"));
    return model;
  }

  public static void addM1Formula(Model model) {
    model.getSignature().createWeightFunction("w_m1");
    FormulaBuilder builder = new FormulaBuilder(model.getSignature());
    model.addFactorFormula(builder.parse("" + "factor: for Int s, Int t, Double p " +
            "if source(s,_) & target(t,_) & m1(s,t,p) add [align(s,t)] * p * w_m1"));
  }

  public static void addWordPairFormula(Model model) {
    model.getSignature().createWeightFunctionByName("w_pair", "SourceWord", "TargetWord");
    FormulaBuilder builder = new FormulaBuilder(model.getSignature());
    model.addFactorFormula(builder.parse("" + "factor: for Int s, Int t, SourceWord ws, TargetWord wt " +
            "if source(s,ws) & target(t,wt) add [align(s,t)] * w_pair(ws,wt)"));
  }


  public static void addUndefinedWordPairFormula(Model model){
    FormulaBuilder builder = new FormulaBuilder(model.getSignature());
    model.getSignature().createWeightFunction("w_undef");
    FactorFormula result = builder.parse(("" +
            "factor: for Int s, Int t, SourceWord ws, TargetWord wt " +
            "if source(s,ws) & target(t,wt) & undefined(w_pair(ws,wt)) " +
            "add [align(s,t)] * w_undef"));
    model.addFactorFormula(result);
  }

  public static void addDistanceFormula(Model model){
    FormulaBuilder builder = new FormulaBuilder(model.getSignature());
    model.getSignature().createWeightFunction("w_dist");
    FactorFormula result = builder.parse(("" +
            "factor: for Int s, Int t " +
            "if source(s,_) & target(t,_)" +
            "add [align(s,t)] * double(s - t) * w_dist"));
    model.addFactorFormula(result);
  }

  public static void addAbsoluteDistanceFormula(Model model, String weightName){
    FormulaBuilder builder = new FormulaBuilder(model.getSignature());
    model.getSignature().createWeightFunction(weightName);
    FactorFormula result = builder.parse(("" +
            "factor: for Int s, Int t " +
            "if source(s,_) & target(t,_)" +
            "add [align(s,t)] * abs(double(s - t)) * " + weightName));
    model.addFactorFormula(result);
  }

  public static void addAlignedToSameTargetFormula(Model model, String weightName){
    FormulaBuilder builder = new FormulaBuilder(model.getSignature());
    model.getSignature().createWeightFunction(weightName, false);
    FactorFormula result = builder.parse(("" +
            "factor: for Int s1, Int s2, Int t " +
            "if source(s1,_) & source(s2,_) & target(t,_) & s2 > s1 " +
            "add [align(s1,t) & align(s2,t)] * double(s2 - s1) * w_sameTarget"));
    result.setAlwaysPenalizing(true);
    model.addFactorFormula(result);
  }

  public static void addM1DistanceFormula(Model model){
    FormulaBuilder builder = new FormulaBuilder(model.getSignature());
    model.getSignature().createWeightFunction("w_m1dist");
    FactorFormula result = builder.parse(("" +
            "factor: for Int s, Int t, Double m1 " +
            "if source(s,_) & target(t,_) & m1(s,t,m1)" +
            "add [align(s,t)] * (double(s - t) * m1) * w_m1dist"));
    model.addFactorFormula(result);
  }

  public static void setSentences(GroundAtoms atoms, int sourceCount, int targetCount, String ... words){
    for (int i = 0; i < sourceCount; ++i)
      atoms.getGroundAtomsOf("source").addGroundAtom(i, words[i]);
    for (int i = 0; i < targetCount; ++i)
      atoms.getGroundAtomsOf("target").addGroundAtom(i, words[i + sourceCount]);
  }

  public static void setModel1(GroundAtoms atoms, int sourceCount, int targetCount, double ... probs){
    for (int s = 0; s < sourceCount; ++s) {
      for (int t = 0; t < targetCount; ++t) {
        atoms.getGroundAtomsOf("m1").addGroundAtom(s, t, probs[sourceCount * t + s]);
      }
    }
  }

  public static void setM1DistanceWeight(Weights w, double weight){
    w.addWeight("w_m1dist",weight);
  }

  public static void setModel1Weight(Weights w, double weight){
    w.addWeight("w_m1",weight);
  }

  public static void setDistanceWeight(Weights w, double weight){
    w.addWeight("w_dist",weight);
  }

  public static void setUndefinedWordPairWeight(Weights w, double weight){
    w.addWeight("w_undef",weight);
  }

  public static void setWordPairWeight(Weights weights, String source, String target, double weight){
    weights.addWeight("w_pair", weight, source, target);
  }

  public static void setAlignment(GroundAtoms atoms, int ... alignments){
    for (int i = 0; i < alignments.length; i+=2)
      atoms.getGroundAtomsOf("align").addGroundAtom(alignments[i],alignments[i+1]);

  }

}
