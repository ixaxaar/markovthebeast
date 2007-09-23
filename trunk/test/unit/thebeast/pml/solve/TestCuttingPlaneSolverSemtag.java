package thebeast.pml.solve;

import junit.framework.TestCase;
import thebeast.pml.*;
import thebeast.pml.solve.ilp.IntegerLinearProgram;
import thebeast.pml.solve.ilp.ILPSolverLpSolve;
import thebeast.pml.solve.weightedsat.MaxWalkSat;
import thebeast.pml.solve.weightedsat.WeightedSatProblem;
import thebeast.pml.formula.FormulaBuilder;

/**
 * @author Sebastian Riedel
 */
public class TestCuttingPlaneSolverSemtag extends TestCase {
  private Signature semtagSig;
  private Model semtagModel;
  private GroundAtoms semtagAtoms;
  private Weights erWeights;

  protected void setUp() {
    setUpEntitySemtag();

  }

  private void setUpEntitySemtag() {
    semtagSig = TheBeast.getInstance().createSignature();
    semtagSig.createType("Slot", false, "TO_LOC", "FROM_LOC", "O");
    semtagSig.createType("Word", false, "\"I\"", "\"want\"", "\"a\"", "\"flight\"",
            "\"from\"", "\"Edinburgh\"", "\"to\"", "\"Tokyo\"");

    semtagSig.createPredicate("word", "Int", "Word");
    semtagSig.createPredicate("slot", "Int", "Slot");

    semtagSig.createWeightFunctionByName("w_word", "Word", "Slot");
    semtagSig.createWeightFunctionByName("w_slotpair", false, "Slot", "Slot");

    semtagModel = semtagSig.createModel();
    semtagModel.addHiddenPredicate(semtagSig.getUserPredicate("slot"));
    semtagModel.addObservedPredicate(semtagSig.getUserPredicate("word"));

    FormulaBuilder builder = new FormulaBuilder(semtagSig);

    //|Int s: slot(t,s)| >= 1
    builder.var("Int", "t").quantify();
    builder.var("t").dontCare().atom("word").condition();
    builder.var("Slot", "s").quantify().var("t").var("s").atom("slot").cardinality();
    builder.term(1).upperBound().cardinalityConstraint(false).formula();
    builder.term(Double.POSITIVE_INFINITY).weight();

    semtagModel.addFactorFormula(builder.produceFactorFormula("atMostOne"));

    //|Int s: slot(t,s)| <= 1
    builder.var("Int", "t").quantify();
    builder.var("t").dontCare().atom("word").condition();
    builder.var("Slot", "s").quantify().var("t").var("s").atom("slot").cardinality();
    builder.term(1).lowerBound().cardinalityConstraint(false).formula();
    builder.term(Double.POSITIVE_INFINITY).weight();

    semtagModel.addFactorFormula(builder.produceFactorFormula("atLeastOne"));

    //word feature
    builder.var("Int", "t").var("Word", "w").var("Slot", "s").quantify().
            var("t").var("w").atom("word").condition().
            var("t").var("s").atom("slot").formula().
            var("w").var("s").apply("w_word").weight();

    semtagModel.addFactorFormula(builder.produceFactorFormula());

    //slot pair feature
    builder.var("Int", "t1").var("Int", "t2").var("Slot", "s1").var("Slot", "s2").quantify().
            var("t1").dontCare().atom("word").var("t2").dontCare().atom("word").var("t1").var("t2").intLessThan().and(3).condition().
            var("t1").var("s1").atom("slot").var("t2").var("s2").atom("slot").and(2).formula().
            var("s1").var("s2").apply("w_slotpair").weight();

    semtagModel.addFactorFormula(builder.produceFactorFormula("slotPair"));


    semtagAtoms = semtagSig.createGroundAtoms();
    semtagAtoms.load(">word\n" +
            "0  \"I\"\n" +
            "1  \"want\"\n" +
            "2  \"a\"\n" +
            "3  \"flight\"\n" +
            "4  \"from\"\n" +
            "5  \"Edinburgh\"\n" +
            "6  \"to\"\n" +
            "7  \"Tokyo\"\n" +
            ">slot\n" +
            "0  O\n" +
            "1  O\n" +
            "2  O\n" +
            "3  O\n" +
            "4  O\n" +
            "5  FROM_LOC\n" +
            "6  O\n" +
            "7  TO_LOC\n");


    setUpWeights();

  }

  public void testSolveInitInteger() {

    IntegerLinearProgram ilp = new IntegerLinearProgram(semtagModel, erWeights, new ILPSolverLpSolve());
    ilp.setInitIntegers(true);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(ilp);
    cuttingPlaneSolver.configure(semtagModel, erWeights);
    cuttingPlaneSolver.setObservation(semtagAtoms);
    cuttingPlaneSolver.solve();

//    System.out.println(cuttingPlaneSolver.getPropositionalModel());
//
//    System.out.println(cuttingPlaneSolver.getHistoryString());
//
    System.out.println(cuttingPlaneSolver.getBestAtoms());

    System.out.println(cuttingPlaneSolver.getIterationCount());

    validateSolution(cuttingPlaneSolver.getBestAtoms());

  }

  public void testSolveInitIntegerGroundSome() {

    IntegerLinearProgram ilp = new IntegerLinearProgram(semtagModel, erWeights, new ILPSolverLpSolve());
    ilp.setInitIntegers(true);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(ilp);
    cuttingPlaneSolver.configure(semtagModel, erWeights);
    cuttingPlaneSolver.setObservation(semtagAtoms);
    cuttingPlaneSolver.setFullyGround(semtagModel.getFactorFormula("atMostOne"), true);
    cuttingPlaneSolver.solve();

    System.out.println(cuttingPlaneSolver.getIterationCount());

    validateSolution(cuttingPlaneSolver.getBestAtoms());

  }

  public void testSolveOrdered() {

    IntegerLinearProgram ilp = new IntegerLinearProgram(semtagModel, erWeights, new ILPSolverLpSolve());
    ilp.setInitIntegers(true);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(ilp);
    cuttingPlaneSolver.configure(semtagModel, erWeights);
    //cuttingPlaneSolver.setEnforceIntegers(true);
    cuttingPlaneSolver.setObservation(semtagAtoms);
    cuttingPlaneSolver.setOrder(semtagModel.getFactorFormula("atMostOne"), 0);
    cuttingPlaneSolver.setOrder(semtagModel.getFactorFormula("slotPair"), 1);
    cuttingPlaneSolver.solve();

    System.out.println(cuttingPlaneSolver.getHistoryString());
    System.out.println(cuttingPlaneSolver.getIterationCount());
    assertEquals(6, cuttingPlaneSolver.getCandidateCount());
    assertEquals(0,cuttingPlaneSolver.getCandidateOrder(5));
    assertEquals(1,cuttingPlaneSolver.getCandidateOrder(4));
    assertEquals(1,cuttingPlaneSolver.getCandidateOrder(3));
    assertEquals(1,cuttingPlaneSolver.getCandidateOrder(2));
    assertEquals(1,cuttingPlaneSolver.getCandidateOrder(1));
    assertEquals(Integer.MAX_VALUE,cuttingPlaneSolver.getCandidateOrder(0));

    //System.out.println(cuttingPlaneSolver.getCandidateAtoms(5));
    //System.out.println(cuttingPlaneSolver.getCandidateFormulas(5));

    assertEquals(6,cuttingPlaneSolver.getIterationCount());

    validateSolution(cuttingPlaneSolver.getBestAtoms());

  }

  public void testSolveInitIntegerGroundAll() {

    IntegerLinearProgram ilp = new IntegerLinearProgram(semtagModel, erWeights, new ILPSolverLpSolve());
    ilp.setInitIntegers(true);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(ilp);
    cuttingPlaneSolver.configure(semtagModel, erWeights);
    cuttingPlaneSolver.setObservation(semtagAtoms);
    cuttingPlaneSolver.setFullyGroundAll(true);
    cuttingPlaneSolver.solve();

    System.out.println(cuttingPlaneSolver.getIterationCount());

    validateSolution(cuttingPlaneSolver.getBestAtoms());

  }

  public void testSolveMaxWalkSat() {

    MaxWalkSat maxWalkSat = new MaxWalkSat();
    maxWalkSat.setSeed(0);
    maxWalkSat.setMaxRestarts(1);
    maxWalkSat.setMaxFlips(100);
    maxWalkSat.setGreedyProbability(0.9);
    WeightedSatProblem wsp = new WeightedSatProblem(maxWalkSat);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(wsp);
    cuttingPlaneSolver.configure(semtagModel, erWeights);
    cuttingPlaneSolver.setObservation(semtagAtoms);
    //cuttingPlaneSolver.setFullyGroundAll(true);
    cuttingPlaneSolver.solve();

    //System.out.println(cuttingPlaneSolver.getHistoryString());
    System.out.println(cuttingPlaneSolver.getIterationCount());
    System.out.println(cuttingPlaneSolver.getBestAtoms());
    validateSolution(cuttingPlaneSolver.getBestAtoms());

  }

  public void testSolveMaxWalkSatGroundAll() {

    MaxWalkSat maxWalkSat = new MaxWalkSat();
    maxWalkSat.setSeed(0);
    maxWalkSat.setMaxRestarts(1);
    maxWalkSat.setMaxFlips(100000);
    maxWalkSat.setGreedyProbability(0.9);
    WeightedSatProblem wsp = new WeightedSatProblem(maxWalkSat);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(wsp);
    cuttingPlaneSolver.configure(semtagModel, erWeights);
    cuttingPlaneSolver.setObservation(semtagAtoms);
    cuttingPlaneSolver.setFullyGroundAll(true);
    cuttingPlaneSolver.solve();

    //System.out.println(cuttingPlaneSolver.getHistoryString());
    System.out.println(cuttingPlaneSolver.getIterationCount());
    System.out.println(cuttingPlaneSolver.getBestAtoms());
    validateSolution(cuttingPlaneSolver.getBestAtoms());

  }



  private void validateSolution(GroundAtoms bestAtoms) {
    Evaluation evaluation = new Evaluation(semtagModel);
    evaluation.evaluate(semtagAtoms, bestAtoms);
    System.out.println(evaluation);
    assertEquals(1.0, evaluation.getF1());
  }

  private void setUpWeights() {
    erWeights = semtagSig.createWeights();
    erWeights.addWeight("w_word", 1.0, "\"I\"", "O");
    erWeights.addWeight("w_word", 1.0, "\"want\"", "O");
    erWeights.addWeight("w_word", 1.0, "\"a\"", "O");
    erWeights.addWeight("w_word", 1.0, "\"flight\"", "O");
    erWeights.addWeight("w_word", 1.0, "\"from\"", "O");
    erWeights.addWeight("w_word", 2.0, "\"Edinburgh\"", "O");
    erWeights.addWeight("w_word", 1.0, "\"to\"", "O");
    erWeights.addWeight("w_word", 1.0, "\"Tokyo\"", "O");
    erWeights.addWeight("w_word", 4.0, "\"Edinburgh\"", "FROM_LOC");
    erWeights.addWeight("w_word", 4.0, "\"Edinburgh\"", "TO_LOC");
    erWeights.addWeight("w_word", 4.0, "\"Tokyo\"", "FROM_LOC");
    erWeights.addWeight("w_word", 4.0, "\"Tokyo\"", "TO_LOC");
    erWeights.addWeight("w_slotpair", -8.0, "FROM_LOC", "FROM_LOC");
    erWeights.addWeight("w_slotpair", -1.5, "TO_LOC", "FROM_LOC");
    erWeights.addWeight("w_slotpair", -1.0, "FROM_LOC", "TO_LOC");
    erWeights.addWeight("w_slotpair", -4.0, "TO_LOC", "TO_LOC");
  }


}
