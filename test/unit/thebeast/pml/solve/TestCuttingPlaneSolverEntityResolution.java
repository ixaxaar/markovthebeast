package thebeast.pml.solve;

import junit.framework.TestCase;
import thebeast.pml.*;
import thebeast.pml.solve.ilp.IntegerLinearProgram;
import thebeast.pml.solve.ilp.ILPSolverLpSolve;
import thebeast.pml.solve.weightedsat.WeightedSatProblem;
import thebeast.pml.solve.weightedsat.MaxWalkSat;
import thebeast.pml.formula.FormulaBuilder;

/**
 * @author Sebastian Riedel
 */
public class TestCuttingPlaneSolverEntityResolution extends TestCase {
  private Signature erSig;
  private Model erModel;
  private GroundAtoms erAtoms;

  protected void setUp() {
    setUpEntityResolution();

  }

  private void setUpEntityResolution() {
    erSig = TheBeast.getInstance().createSignature();
    erSig.createType("Bib", false, "Bib1", "Bib2", "Bib3", "Bib4");
    erSig.createType("Title", false, "Cut and Price", "Cut", "Price", "Max Walk Sat");

    erSig.createPredicate("title", "Bib", "Title");
    erSig.createPredicate("similarTitle", "Title", "Title");
    erSig.createPredicate("sameTitle", "Title", "Title");
    erSig.createPredicate("sameBib", "Bib", "Bib");

    erSig.createWeightFunction("w_titlebib", true);
    erSig.createWeightFunction("w_similarTitle");
    erSig.createWeightFunction("w_bibPrior");
    erSig.createWeightFunction("w_titlePrior");

    erModel = erSig.createModel();
    erModel.addHiddenPredicate(erSig.getUserPredicate("sameBib"));
    erModel.addHiddenPredicate(erSig.getUserPredicate("sameTitle"));
    erModel.addObservedPredicate(erSig.getUserPredicate("title"));
    erModel.addObservedPredicate(erSig.getUserPredicate("similarTitle"));

    FormulaBuilder builder = new FormulaBuilder(erSig);

    //Transitivity
    builder.var("Bib", "b1").var("Bib", "b2").var("Bib", "b3").quantify();
    builder.var("b1").var("b2").atom("sameBib").
            var("b2").var("b3").atom("sameBib").and(2).
            var("b1").var("b3").atom("sameBib").implies().formula();
    builder.term(Double.POSITIVE_INFINITY).weight();

    erModel.addFactorFormula(builder.produceFactorFormula("transitivity"));

    //Reflexity
    builder.var("Bib", "b1").var("Bib", "b2").quantify();
    builder.var("b1").var("b2").atom("sameBib").
            var("b2").var("b1").atom("sameBib").implies().formula();
    builder.term(Double.POSITIVE_INFINITY).weight();

    erModel.addFactorFormula(builder.produceFactorFormula("reflexity"));

    //sameTitle(t1,t2) => sameBib(b1,b2)
    builder.var("Title", "t1").var("Title", "t2").var("Bib", "b1").var("Bib", "b2").quantify().
            var("b1").var("t1").atom("title").var("b2").var("t2").atom("title").and(2).condition().
            var("t1").var("t2").atom("sameTitle").var("b1").var("b2").atom("sameBib").implies().formula().
            apply("w_titlebib").weight();

    erModel.addFactorFormula(builder.produceFactorFormula("sameTitle"));

    //similarTitle(t1,t2) => sameTitle(t1,t2)
    builder.var("Title", "t1").var("Title", "t2").quantify().
            var("t1").var("t2").atom("similarTitle").condition().
            var("t1").var("t2").atom("sameTitle").formula().
            apply("w_similarTitle").weight();

    erModel.addFactorFormula(builder.produceFactorFormula());

    builder.var("Bib", "b1").var("Bib", "b2").quantify().
            var("b1").var("b2").atom("sameBib").formula().
            apply("w_bibPrior").weight();

    erModel.addFactorFormula(builder.produceFactorFormula());

    builder.var("Title", "t1").var("Title", "t2").quantify().
            var("t1").var("t2").atom("sameTitle").formula().
            apply("w_titlePrior").weight();

    erModel.addFactorFormula(builder.produceFactorFormula());

    erAtoms = erSig.createGroundAtoms();
    erAtoms.getGroundAtomsOf("title").addGroundAtom("Bib1", "Cut and Price");
    erAtoms.getGroundAtomsOf("title").addGroundAtom("Bib2", "Cut");
    erAtoms.getGroundAtomsOf("title").addGroundAtom("Bib3", "Price");
    erAtoms.getGroundAtomsOf("title").addGroundAtom("Bib4", "Max Walk Sat");

    erAtoms.getGroundAtomsOf("similarTitle").addGroundAtom("Cut and Price", "Cut");
    erAtoms.getGroundAtomsOf("similarTitle").addGroundAtom("Cut and Price", "Price");
  }

  public void testSolveInitInteger() {
    Weights erWeights = erSig.createWeights();
    erWeights.addWeight("w_titlebib", 2.0);
    erWeights.addWeight("w_similarTitle", 2.0);

    IntegerLinearProgram ilp = new IntegerLinearProgram(erModel, erWeights, new ILPSolverLpSolve());
    ilp.setInitIntegers(true);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(ilp);
    cuttingPlaneSolver.configure(erModel, erWeights);
    cuttingPlaneSolver.setObservation(erAtoms);
    cuttingPlaneSolver.solve();

    validateSolution(cuttingPlaneSolver.getBestAtoms());


  }

  public void testSolveIncrementalIntegers() {
    Weights erWeights = erSig.createWeights();
    erWeights.addWeight("w_titlebib", 2.0);
    erWeights.addWeight("w_similarTitle", 2.0);
    erWeights.addWeight("w_bibPrior", -0.01);
    erWeights.addWeight("w_titlePrior", -0.01);

    IntegerLinearProgram ilp = new IntegerLinearProgram(erModel, erWeights, new ILPSolverLpSolve());

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(ilp);
    cuttingPlaneSolver.configure(erModel, erWeights);
    cuttingPlaneSolver.setObservation(erAtoms);
    cuttingPlaneSolver.setEnforceIntegers(true);
    cuttingPlaneSolver.solve();

    System.out.println(cuttingPlaneSolver.getIterationCount());
    assertEquals(3, cuttingPlaneSolver.getIterationCount());


    validateSolution(cuttingPlaneSolver.getBestAtoms());

    Solution solution = new Solution(erModel, erWeights);
    solution.load(cuttingPlaneSolver.getBestAtoms(), cuttingPlaneSolver.getBestFormulas());
    System.out.println(cuttingPlaneSolver.getBestFormulas());
    FeatureVector vector = solution.extract();
    System.out.println(erWeights.toString(vector.getLocal()));
    System.out.println(vector.getLocal().toString());
    double score = erWeights.score(vector);
    double expectedScore = 9 * -0.01 + 2 * -0.01 + 2 * 2.0;
    assertEquals(expectedScore,score);
    System.out.println(expectedScore);
    System.out.println(score);

  }

  public void testSolveWithMaxWalkSat() {
    Weights erWeights = erSig.createWeights();
    erWeights.addWeight("w_titlebib", 2.0);
    erWeights.addWeight("w_similarTitle", 2.0);
    //erWeights.addWeight("w_bibPrior",-0.01);
    //erWeights.addWeight("w_titlePrior",-0.01);

    MaxWalkSat maxWalkSat = new MaxWalkSat();
    maxWalkSat.setSeed(1);
    maxWalkSat.setMaxRestarts(1);
    maxWalkSat.setMaxFlips(1000);
    maxWalkSat.setGreedyProbability(0.5);
    WeightedSatProblem wsp = new WeightedSatProblem(maxWalkSat);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(wsp);
    cuttingPlaneSolver.configure(erModel, erWeights);
    cuttingPlaneSolver.setObservation(erAtoms);
    cuttingPlaneSolver.setEnforceIntegers(true);
    cuttingPlaneSolver.solve();

    System.out.println(wsp.getMapping(erSig.getUserPredicate("sameBib")).value());
    System.out.println(wsp.getMapping(erSig.getUserPredicate("sameTitle")).value());


    System.out.println(cuttingPlaneSolver.getIterationCount());
    System.out.println(cuttingPlaneSolver.getBestAtoms());
    //assertEquals(3, cuttingPlaneSolver.getIterationCount());


    validateSolution(cuttingPlaneSolver.getBestAtoms());

  }

  public void testSolveWithMaxWalkSatFullyGroundAll() {
    Weights erWeights = erSig.createWeights();
    erWeights.addWeight("w_titlebib", 2.0);
    erWeights.addWeight("w_similarTitle", 2.0);
    //erWeights.addWeight("w_bibPrior",-0.0001);
    //erWeights.addWeight("w_titlePrior",-0.001);

    MaxWalkSat maxWalkSat = new MaxWalkSat();
    //maxWalkSat.setSeed(1);
    maxWalkSat.setInitRandom(true);
    maxWalkSat.setMaxRestarts(4);
    maxWalkSat.setMaxFlips(100000);
    WeightedSatProblem wsp = new WeightedSatProblem(maxWalkSat);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(wsp);
    cuttingPlaneSolver.configure(erModel, erWeights);
    cuttingPlaneSolver.setObservation(erAtoms);
    cuttingPlaneSolver.setFullyGroundAll(true);
    cuttingPlaneSolver.setEnforceIntegers(true);
    cuttingPlaneSolver.solve();

    System.out.println(cuttingPlaneSolver.getIterationCount());
    System.out.println(cuttingPlaneSolver.getBestAtoms());
    //assertEquals(1, cuttingPlaneSolver.getIterationCount());

    //validateSolution(cuttingPlaneSolver.getBestAtoms());

  }

  public void testSolveWithMaxWalkSatFullyGroundSome() {
    Weights erWeights = erSig.createWeights();
    erWeights.addWeight("w_titlebib", 2.0);
    erWeights.addWeight("w_similarTitle", 2.0);
    //erWeights.addWeight("w_bibPrior",-0.0001);
    //erWeights.addWeight("w_titlePrior",-0.001);

    MaxWalkSat maxWalkSat = new MaxWalkSat();
    //maxWalkSat.setSeed(1);
    maxWalkSat.setInitRandom(true);
    maxWalkSat.setMaxRestarts(4);
    maxWalkSat.setMaxFlips(100000);
    WeightedSatProblem wsp = new WeightedSatProblem(maxWalkSat);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(wsp);
    cuttingPlaneSolver.configure(erModel, erWeights);
    cuttingPlaneSolver.setFullyGround(erModel.getFactorFormula("transitivity"), true);
    cuttingPlaneSolver.setObservation(erAtoms);
    cuttingPlaneSolver.setEnforceIntegers(true);
    cuttingPlaneSolver.solve();

    System.out.println(cuttingPlaneSolver.getIterationCount());
    System.out.println(cuttingPlaneSolver.getBestAtoms());
    //assertEquals(1, cuttingPlaneSolver.getIterationCount());

    //validateSolution(cuttingPlaneSolver.getBestAtoms());

  }


  public void testSolveFullyGroundSome() {
    Weights erWeights = erSig.createWeights();
    erWeights.addWeight("w_titlebib", 2.0);
    erWeights.addWeight("w_similarTitle", 2.0);
    erWeights.addWeight("w_bibPrior", -0.01);
    erWeights.addWeight("w_titlePrior", -0.01);

    IntegerLinearProgram ilp = new IntegerLinearProgram(erModel, erWeights, new ILPSolverLpSolve());

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(ilp);
    cuttingPlaneSolver.configure(erModel, erWeights);
    cuttingPlaneSolver.setFullyGround(erModel.getFactorFormula("transitivity"), true);
    cuttingPlaneSolver.setObservation(erAtoms);
    cuttingPlaneSolver.setEnforceIntegers(true);
    cuttingPlaneSolver.solve();

    System.out.println(cuttingPlaneSolver.getIterationCount());
    //assertEquals(2, cuttingPlaneSolver.getIterationCount());

    //System.out.println(cuttingPlaneSolver.getBestAtoms());
    validateSolution(cuttingPlaneSolver.getBestAtoms());

  }

  public void testSolveFullyGroundAll() {
    Weights erWeights = erSig.createWeights();
    erWeights.addWeight("w_titlebib", 2.0);
    erWeights.addWeight("w_similarTitle", 2.0);
    erWeights.addWeight("w_bibPrior", -0.01);
    erWeights.addWeight("w_titlePrior", -0.01);


    IntegerLinearProgram ilp = new IntegerLinearProgram(erModel, erWeights, new ILPSolverLpSolve());
    ilp.setInitIntegers(true);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(ilp);
    cuttingPlaneSolver.configure(erModel, erWeights);
    cuttingPlaneSolver.setFullyGround(erModel.getFactorFormula("transitivity"), true);
    cuttingPlaneSolver.setFullyGround(erModel.getFactorFormula("reflexity"), true);
    cuttingPlaneSolver.setFullyGround(erModel.getFactorFormula("sameTitle"), true);
    cuttingPlaneSolver.setObservation(erAtoms);
    cuttingPlaneSolver.solve();

    System.out.println(cuttingPlaneSolver.getIterationCount());
    assertEquals(1, cuttingPlaneSolver.getIterationCount());


    validateSolution(cuttingPlaneSolver.getBestAtoms());

  }


  private void validateSolution(GroundAtoms atoms) {
    GroundAtomCollection sameBib = atoms.getGroundAtomsOf("sameBib");

    assertTrue(sameBib.containsAtom("Bib1", "Bib2"));
    assertTrue(sameBib.containsAtom("Bib2", "Bib1"));
    assertTrue(sameBib.containsAtom("Bib1", "Bib3"));
    assertTrue(sameBib.containsAtom("Bib3", "Bib1"));
    assertTrue(sameBib.containsAtom("Bib2", "Bib3"));
    assertTrue(sameBib.containsAtom("Bib3", "Bib2"));
    assertTrue(sameBib.containsAtom("Bib1", "Bib1"));
    assertTrue(sameBib.containsAtom("Bib2", "Bib2"));
    assertTrue(sameBib.containsAtom("Bib3", "Bib3"));

    assertEquals(sameBib.size(), 9);

    GroundAtomCollection sameTitle = atoms.getGroundAtomsOf("sameTitle");
    assertTrue(sameTitle.containsAtom("Cut and Price", "Cut"));
    assertTrue(sameTitle.containsAtom("Cut and Price", "Price"));
    assertEquals(sameTitle.size(), 2);
  }

}
