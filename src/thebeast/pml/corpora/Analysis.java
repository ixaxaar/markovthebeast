package thebeast.pml.corpora;

import thebeast.pml.*;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.FormulaBuilder;
import thebeast.pml.function.WeightFunction;
import thebeast.pml.solve.CuttingPlaneSolver;

import java.io.PrintStream;
import java.util.Random;

/**
 * @author Sebastian Riedel
 */
@SuppressWarnings({"ConstantConditions"})
public class Analysis {
  private static final Random RANDOM = new Random(0);

  public static void main(String[] args) {
    //analyzeWithBlocks();
    int tries = args.length > 0 ? Integer.parseInt(args[0]) : 10;
    analyzeWithStripes(System.out, tries);

  }

  private static void analyzeWithBlocks() {
    int maxN = 20;
    int triangleSize = 10;
    double trueGoldMean = 1.0;
    double falseGoldMean = -3.0;
    double weight = 0.1;

    //should use problem with 3 rules
    //- block(x,y) => block(x,y+1)
    //- block(x,y) => not(block(x+1,y)
    //- not block(x,y) => block(x+1,y)


    Signature signature = TheBeast.getInstance().createSignature();
    UserPredicate block = signature.createPredicate("block", "Int", "Int");
    UserPredicate score = signature.createPredicate("score", "Int", "Int", "Double");
    WeightFunction w1 = signature.createWeightFunction("w1", true);
    FormulaBuilder builder = new FormulaBuilder(signature);

    Model model = signature.createModel();
    FactorFormula localScores = builder.parse("factor: for Int i, Int j, Double s if score(i,j,s) add [block(i,j)] * s");
    FactorFormula blockRule = builder.parse("factor: for Int i, Int j if score(i+1,j+1,_) " +
            "add [block(i,j) & block(i+1,j) => block(i+1,j+1)] * w1");
    model.addFactorFormula(localScores);
    model.addFactorFormula(blockRule);
    model.addHiddenPredicate(block);
    model.addObservedPredicate(score);

    Weights weights = signature.createWeights();
    weights.addWeight(w1, weight);

    GroundAtoms gold = signature.createGroundAtoms();
    Random random = new Random(0);
    createTriangle(maxN, triangleSize, gold, block);

    GroundAtoms observation = signature.createGroundAtoms();
    for (int x = 0; x < maxN; ++x)
      for (int y = 0; y < maxN; ++y) {
        double s = gold.getGroundAtomsOf(block).containsAtom(x, y) ?
                random.nextGaussian() + trueGoldMean :
                random.nextGaussian() + falseGoldMean;
        observation.getGroundAtomsOf(score).addGroundAtom(x, y, s);
      }

    printBlocks(System.out, maxN, gold.getGroundAtomsOf(block));

    CuttingPlaneSolver solver = new CuttingPlaneSolver();
    solver.configure(model, weights);

    solver.setObservation(observation);
    solver.solve();
    GroundAtoms guess = solver.getBestAtoms();
    printBlocks(System.out, maxN, guess.getGroundAtomsOf(block));
    System.out.println("solver.getIterationCount() = " + solver.getIterationCount());
    System.out.println("solver.getPropositionalModel().getGroundAtomCount() = " + solver.getPropositionalModel().getGroundAtomCount());
  }

  private static int[] intSteps(int from, int to, int stepSize) {
    int[] result = new int[(to - from) / stepSize];
    for (int i = 0; i < result.length; ++i)
      result[i] = from + i * stepSize;
    return result;
  }

  private static double[] doubleSteps(double from, double to, double stepSize) {
    double[] result = new double[(int) ((to - from) / stepSize)];
    for (int i = 0; i < result.length; ++i)
      result[i] = from + i * stepSize;
    return result;
  }

  private static double[] invert(double[] src) {
    double[] result = new double[src.length];
    for (int i = 0; i < result.length; ++i) result[i] = -src[i];
    return result;
  }

  private static void analyzeWithStripes(PrintStream out, int tries) {

    for (int n : intSteps(10, 110, 10))
      for (int solutionSize : intSteps(n / 10, n + n / 10, n / 10))
        for (double trueMean : doubleSteps(0.0, 5.0, 0.5))
          for (double falseMean : invert(doubleSteps(0.0, 5.0, 0.5)))
            for (double wOr : doubleSteps(0.0, 5.0, 0.5))
              for (double wForbid : doubleSteps(0.0, 5.0, 0.5))
                for (double wImply : doubleSteps(0.0, 5.0, 0.5))
                  analyzeWithStripes(n, solutionSize, tries, trueMean, falseMean, wImply, wForbid, wOr, out);

//
//    analyzeWithStripes(maxN, stripeSize, tries, trueGoldMean,
//            falseGoldMean, wImplyDown, wForbidRight, wRightOrLeft, out);


  }

  private static void analyzeWithStripes(int maxN, int stripeSize, int tries, double trueGoldMean, double falseGoldMean, double wImplyDown, double wForbidRight, double wRightOrLeft, PrintStream out) {
    boolean printSolutions = false;

    //should use problem with 3 rules
    //- block(x,y) => block(x,y+1)
    //- block(x,y) => not(block(x+1,y)
    //- not block(x,y) => block(x+1,y)


    Signature signature = TheBeast.getInstance().createSignature();
    UserPredicate block = signature.createPredicate("block", "Int", "Int");
    UserPredicate score = signature.createPredicate("score", "Int", "Int", "Double");
    WeightFunction w1 = signature.createWeightFunction("w1", true);
    WeightFunction w2 = signature.createWeightFunction("w2", true);
    WeightFunction w3 = signature.createWeightFunction("w3", true);
    FormulaBuilder builder = new FormulaBuilder(signature);

    Model model = signature.createModel();
    FactorFormula localScores = builder.parse("factor: for Int i, Int j, Double s if score(i,j,s) add [block(i,j)] * s");
    FactorFormula implyDown = builder.parse("factor: for Int i, Int j if score(i,j+1,_) " +
            "add [block(i,j) => block(i,j+1)] * w1");
    FactorFormula forbidRight = builder.parse("factor: for Int i, Int j if score(i+1,j,_) " +
            "add [block(i,j) => !block(i,j+1)] * w2");
    FactorFormula rightOrLeft = builder.parse("factor: for Int i, Int j if score(i+1,j,_) " +
            "add [!block(i,j) => block(i,j+1)] * w3");
    model.addFactorFormula(localScores);
    if (wImplyDown != 0) model.addFactorFormula(implyDown);
    if (wRightOrLeft != 0) model.addFactorFormula(rightOrLeft);
    if (wForbidRight != 0) model.addFactorFormula(forbidRight);
    model.addHiddenPredicate(block);
    model.addObservedPredicate(score);

    Weights weights = signature.createWeights();
    weights.addWeight(w1, wImplyDown);
    weights.addWeight(w2, wForbidRight);
    weights.addWeight(w3, wRightOrLeft);

    GroundAtoms gold = signature.createGroundAtoms();
    createStripes(maxN, stripeSize, gold, block);

    GroundAtoms observation = signature.createGroundAtoms();

    CuttingPlaneSolver solver = new CuttingPlaneSolver();
    solver.configure(model, weights);

    int totalGroundAtomCount = 0;
    int totalGroundFormulaCount = 0;
    int totalIterations = 0;
    long totalTime = 0;
    for (int i = 0; i < tries; ++i) {
      observation.clear();
      for (int x = 0; x < maxN; ++x)
        for (int y = 0; y < maxN; ++y) {
          double s = gold.getGroundAtomsOf(block).containsAtom(x, y) ?
                  RANDOM.nextGaussian() + trueGoldMean :
                  RANDOM.nextGaussian() + falseGoldMean;
          observation.getGroundAtomsOf(score).addGroundAtom(x, y, s);
        }

      if (printSolutions) printBlocks(System.out, maxN, gold.getGroundAtomsOf(block));


      solver.setObservation(observation);
      long start = System.currentTimeMillis();
      solver.solve();
      totalTime += System.currentTimeMillis() - start;
      totalIterations += solver.getIterationCount();
      totalGroundAtomCount += solver.getPropositionalModel().getGroundAtomCount();
      totalGroundFormulaCount += solver.getPropositionalModel().getGroundFormulaCount();

      GroundAtoms guess = solver.getBestAtoms();
      if (printSolutions) printBlocks(System.out, maxN, guess.getGroundAtomsOf(block));

    }
    double avgDuration = (double) totalTime / tries;
    double avgIterations = (double) totalIterations / tries;
    double avgGroundAtomCount = (double) totalGroundAtomCount / tries;
    double avgGroundFormulaCount = (double) totalGroundFormulaCount / tries;

    out.printf("%5d ", maxN);
    out.printf("%5d ", stripeSize);
    out.printf("%5d ", tries);
    out.printf("%5.2f ", trueGoldMean);
    out.printf("%5.2f ", falseGoldMean);
    out.printf("%5.2f ", wImplyDown);
    out.printf("%5.2f ", wForbidRight);
    out.printf("%5.2f ", wRightOrLeft);
    out.printf("%5.2f ", avgIterations);
    out.printf("%7.2f ", avgDuration);
    out.printf("%7.2f ", avgGroundAtomCount);
    out.printf("%7.2f ", avgGroundFormulaCount);
    out.println();

    if (printSolutions) {
      System.out.println("avgGroundFormulaCount = " + avgGroundFormulaCount);
      System.out.println("avgGroundAtomCount = " + avgGroundAtomCount);
      System.out.println("avgIterations = " + avgIterations);
      System.out.println("avgDuration = " + avgDuration);
    }
  }

  private static void createTriangle(int maxN, int triangleSize, GroundAtoms gold, UserPredicate block) {
    for (int x = maxN / 2 - triangleSize / 2; x < maxN / 2 + triangleSize / 2; ++x)
      for (int y = maxN / 2 - triangleSize / 2; y < x + 1; ++y) {
        gold.getGroundAtomsOf(block).addGroundAtom(x, y);
      }
  }

  private static void createStripes(int maxN, int stripeSize, GroundAtoms gold, UserPredicate block) {
    for (int x = maxN / 2 - stripeSize / 2; x < maxN / 2 + stripeSize / 2; x += 2)
      for (int y = maxN / 2 - stripeSize / 2; y < maxN / 2 + stripeSize / 2; ++y) {
        gold.getGroundAtomsOf(block).addGroundAtom(x, y);
      }
  }


  public static void printBlocks(PrintStream out, int n, GroundAtomCollection atoms) {
    for (int y = 0; y < n; ++y) {
      for (int x = 0; x < n; ++x) {
        out.print(atoms.containsAtom(x, y) ? "X" : " ");
      }
      out.println();
    }


  }


}
