package thebeast.pml.solve;

import junit.framework.TestCase;
import thebeast.pml.*;
import thebeast.pml.solve.ilp.ILPSolverLpSolve;
import thebeast.pml.solve.ilp.IntegerLinearProgram;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.FormulaBuilder;

/**
 * @author Sebastian Riedel
 */
public class TestCuttingPlaneSolverGEQInSoftDisjunction extends TestCase {
  private Signature ballsSig;
  private Model ballsModel;
  private GroundAtoms ballsAtoms;
  private FactorFormula f_color;
  private FactorFormula f_size;
  private FactorFormula f_count;
  private Weights weights1,weights2;

  protected void setUp() {
    setUpBalls();

  }

  private void setUpBalls() {
    ballsSig = TheBeast.getInstance().createSignature();
    ballsSig.createType("Color", false, "Black", "White", "Yellow", "Red", "Blue", "Green", "Orange");
    ballsSig.createType("Size", false, "Big", "Small");

    ballsSig.createPredicate("inBag", "Int", "Color");
    ballsSig.createPredicate("isBag", "Int");
    ballsSig.createPredicate("size", "Int", "Size");

    ballsSig.createWeightFunctionByName("w_color", "Color");
    ballsSig.createWeightFunctionByName("w_count", true, "Int", "Size");
    ballsSig.createWeightFunctionByName("w_size", "Int", "Size");

    ballsModel = ballsSig.createModel();
    ballsModel.addHiddenPredicate(ballsSig.getUserPredicate("inBag"));
    ballsModel.addHiddenPredicate(ballsSig.getUserPredicate("size"));
    ballsModel.addObservedPredicate(ballsSig.getUserPredicate("isBag"));

    FormulaBuilder builder = new FormulaBuilder(ballsSig);

    //per color score
    builder.var("Color", "c").var("Int", "b").quantify().
            var("b").atom("isBag").condition().
            var("b").var("c").atom("inBag").formula().
            var("c").apply("w_color").weight();

    f_color = builder.produceFactorFormula();

    //size prior
    builder.var("Size", "s").var("Int", "b").quantify().
            var("b").atom("isBag").condition().
            var("b").var("s").atom("size").formula().
            var("b").var("s").apply("w_size").weight();

    f_size = builder.produceFactorFormula("size");

    //counts
    builder.var("Size", "s").var("Int", "b").var("Int", "m").quantify().
            var("b").atom("isBag").condition().
            var("b").var("s").atom("size").
            var("Color", "c").quantify().var("b").var("c").atom("inBag").cardinality().
            var("m").lowerBound().cardinalityConstraint(false).implies().formula().
            var("m").var("s").apply("w_count").weight();
    f_count = builder.produceFactorFormula("count");

    ballsModel.addFactorFormula(f_color);
    ballsModel.addFactorFormula(f_count);
    ballsModel.addFactorFormula(f_size);

    System.out.println(f_color);
    System.out.println(f_size);
    System.out.println(f_count);

    ballsAtoms = ballsSig.createGroundAtoms();
    ballsAtoms.getGroundAtomsOf("isBag").addGroundAtom(0);
    ballsAtoms.getGroundAtomsOf("isBag").addGroundAtom(1);

    weights1 = ballsSig.createWeights();
    weights1.addWeight("w_color", -5.0, "Green");
    weights1.addWeight("w_color", -4.0, "Orange");
    weights1.addWeight("w_color", -3.0, "Black");
    weights1.addWeight("w_color", -2.0, "White");
    weights1.addWeight("w_color", -1.0, "Yellow");
    weights1.addWeight("w_color", 0.0, "Red");
    weights1.addWeight("w_color", 1.0, "Blue");

    weights1.addWeight("w_size", 10.0, 0, "Big");
    weights1.addWeight("w_size", -4.0, 0, "Small");
    weights1.addWeight("w_size", -4.0, 1, "Big");
    weights1.addWeight("w_size", -1.0, 1, "Small");

    weights1.addWeight("w_count", 6.9, 2, "Small");
    weights1.addWeight("w_count", 10.0, 4, "Big");

    weights2 = ballsSig.createWeights();
    weights2.addWeight("w_color", 5.0, "Green");
    weights2.addWeight("w_color", 4.0, "Orange");
    weights2.addWeight("w_color", 3.0, "Black");
    weights2.addWeight("w_color", 2.0, "White");
    weights2.addWeight("w_color", 1.0, "Yellow");
    weights2.addWeight("w_color", 0.0, "Red");
    weights2.addWeight("w_color", -1.0, "Blue");

    weights2.addWeight("w_size", 4.0, 0, "Big");
    weights2.addWeight("w_size", -4.0, 0, "Small");
    weights2.addWeight("w_size", -4.0, 1, "Big");
    weights2.addWeight("w_size", 6.1, 1, "Small");

    weights2.addWeight("w_count", 6.0, 2, "Small");
    weights2.addWeight("w_count", 1.0, 4, "Big");


  }

  public void testSolveInitInteger() {

    ILPSolverLpSolve solver = new ILPSolverLpSolve();
    IntegerLinearProgram ilp = new IntegerLinearProgram(ballsModel, weights1, solver);
    ilp.setInitIntegers(true);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(ilp);
    cuttingPlaneSolver.configure(ballsModel, weights1);
    cuttingPlaneSolver.setObservation(ballsAtoms);
    cuttingPlaneSolver.solve();

    System.out.println(cuttingPlaneSolver.getBestAtoms());
    System.out.println(ilp.toLpSolveFormat());
    System.out.println(ilp.getFractionals().value());
    System.out.println(ilp.getResultString());    

    //validateSolution1(cuttingPlaneSolver.getBestAtoms());

    cuttingPlaneSolver.configure(ballsModel, weights2);
    cuttingPlaneSolver.setObservation(ballsAtoms);
    cuttingPlaneSolver.solve();

    System.out.println(cuttingPlaneSolver.getBestAtoms());

    //validateSolution2(cuttingPlaneSolver.getBestAtoms());

    //System.out.println(cuttingPlaneSolver.getIterationCount());
    //System.out.println(ilp.toLpSolveFormat());


  }


  public void testSolveFullyGroundSome() {

    ILPSolverLpSolve solver = new ILPSolverLpSolve();
    solver.setBbDepthLimit(5);
    IntegerLinearProgram ilp = new IntegerLinearProgram(ballsModel, weights1, solver);
    ilp.setInitIntegers(true);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(ilp);
    cuttingPlaneSolver.configure(ballsModel, weights1);
    cuttingPlaneSolver.setFullyGround(ballsModel.getFactorFormula("count"), true);
    cuttingPlaneSolver.setObservation(ballsAtoms);
    //cuttingPlaneSolver.setEnforceIntegers(true);
    cuttingPlaneSolver.solve();

    System.out.println(cuttingPlaneSolver.getBestAtoms());    

    System.out.println(cuttingPlaneSolver.getIterationCount());
    //assertEquals(2, cuttingPlaneSolver.getIterationCount());

    //System.out.println(cuttingPlaneSolver.getBestAtoms());
    //validateSolution1(cuttingPlaneSolver.getBestAtoms());

    ilp = new IntegerLinearProgram(ballsModel, weights2, solver);
    ilp.setInitIntegers(true);

    cuttingPlaneSolver = new CuttingPlaneSolver(ilp);
    cuttingPlaneSolver.configure(ballsModel, weights2);
    cuttingPlaneSolver.setFullyGround(ballsModel.getFactorFormula("count"), true);
    cuttingPlaneSolver.setObservation(ballsAtoms);
    //cuttingPlaneSolver.setEnforceIntegers(true);
    cuttingPlaneSolver.solve();

    System.out.println(ilp.toLpSolveFormat());

    System.out.println(ilp.getResultString());

    System.out.println(cuttingPlaneSolver.getBestAtoms());

    //validateSolution2(cuttingPlaneSolver.getBestAtoms());

  }

  /*
  public void testSolveFullyGroundAll() {
    Weights erWeights = ballsSig.createWeights();
    erWeights.addWeight("w_titlebib", 2.0);
    erWeights.addWeight("w_similarTitle", 2.0);
    erWeights.addWeight("w_bibPrior", -0.01);
    erWeights.addWeight("w_titlePrior", -0.01);


    IntegerLinearProgram ilp = new IntegerLinearProgram(ballsModel, erWeights, new ILPSolverLpSolve());
    ilp.setInitIntegers(true);

    CuttingPlaneSolver cuttingPlaneSolver = new CuttingPlaneSolver(ilp);
    cuttingPlaneSolver.configure(ballsModel, erWeights);
    cuttingPlaneSolver.setFullyGround(ballsModel.getFactorFormula("transitivity"), true);
    cuttingPlaneSolver.setFullyGround(ballsModel.getFactorFormula("reflexity"), true);
    cuttingPlaneSolver.setFullyGround(ballsModel.getFactorFormula("sameTitle"), true);
    cuttingPlaneSolver.setObservation(ballsAtoms);
    cuttingPlaneSolver.solve();

    System.out.println(cuttingPlaneSolver.getIterationCount());
    assertEquals(1, cuttingPlaneSolver.getIterationCount());


    validateSolution(cuttingPlaneSolver.getBestAtoms());

  }

  */
  private void validateSolution1(GroundAtoms atoms) {
    GroundAtomCollection size = atoms.getGroundAtomsOf("size");

    assertTrue(size.containsAtom(0, "Big"));

    assertEquals(size.size(), 1);

    GroundAtomCollection inBag = atoms.getGroundAtomsOf("inBag");

    assertTrue(inBag.containsAtom(0, "Green"));
    assertTrue(inBag.containsAtom(0, "Orange"));
    assertTrue(inBag.containsAtom(0, "Black"));
    assertTrue(inBag.containsAtom(0, "White"));
    assertTrue(inBag.containsAtom(1, "Green"));
    assertTrue(inBag.containsAtom(1, "Orange"));
    assertTrue(inBag.containsAtom(1, "Black"));
    assertTrue(inBag.containsAtom(1, "White"));
    assertTrue(inBag.containsAtom(1, "Yellow"));

    assertEquals(inBag.size(), 9);

  }

  private void validateSolution2(GroundAtoms atoms) {
    GroundAtomCollection size = atoms.getGroundAtomsOf("size");

    assertTrue(size.containsAtom(0, "Big"));
    assertTrue(size.containsAtom(1, "Small"));

    assertEquals(size.size(), 2);

    GroundAtomCollection inBag = atoms.getGroundAtomsOf("inBag");

    assertTrue(inBag.containsAtom(0, "Green"));
    assertTrue(inBag.containsAtom(0, "Orange"));
    assertTrue(inBag.containsAtom(0, "Black"));
    assertTrue(inBag.containsAtom(0, "White"));
    assertTrue(inBag.containsAtom(1, "Green"));
    assertTrue(inBag.containsAtom(1, "Orange"));

    assertEquals(inBag.size(), 6);

  }


}
