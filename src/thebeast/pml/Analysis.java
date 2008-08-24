package thebeast.pml;

import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.FormulaBuilder;
import thebeast.pml.function.WeightFunction;
import thebeast.pml.solve.CuttingPlaneSolver;
import thebeast.pml.solve.ilp.IntegerLinearProgram;
import thebeast.util.Pair;
import thebeast.util.Triple;

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
    //int tries = args.length > 0 ? Integer.parseInt(args[0]) : 10;
    //analyzeWithStripes(System.out, tries);
    //analyzeOuterBound2();
    //analyzeLowerBound1();
    //analyzeUpperBound1();
    //analyzeUpperBound1(6,10,1000000,new Random(0), 100000);
    //analyzeLowerBound1(10,6,1000000, new Random(0),10000);
    //analyzeInnerBound2();
    analyzeInnerBound2(10, 1, -1000000, 1, 100, 10);
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
    ((IntegerLinearProgram) solver.getPropositionalModel()).setInitIntegers(true);

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


  private static void analyzeInnerBound1() {
    int maxTries = 10;
    Random random = new Random(0);
    for (double w_rule : doubleSteps(-2, 2, 0.1)) {
      double size = 0;
      for (int tries = 0; tries < maxTries; ++tries) {
        size += analyzeInnerBound1(10, w_rule, random);
      }
      size /= maxTries;
      System.out.printf("%-4f %-4f %-4f\n", w_rule, size, w_rule >= 0 ? 9.0 : 72.0);
    }
  }

  private static void analyzeOuterBound1() {
    int maxTries = 10;
    int maxN = 20;
    Random random = new Random(0);
    for (int width : intSteps(0, maxN, 1)) {
      double size = 0;
      for (int tries = 0; tries < maxTries; ++tries) {
        size += analyzeOuterBound1(maxN, width, 10000, random, 10);

      }
      size /= maxTries;
      System.out.printf("%-4d %-4d %-4f %-4f\n", width, (maxN - 1) * (maxN - 1), (width - 1) * width / 2.0, size);
    }
  }

  private static void analyzeLowerBound1() {
    int maxTries = 10;
    int maxN = 10;
    Random random = new Random(0);

    for (int width : intSteps(2, maxN, 1)) {
      double iterations = 0;
      for (int tries = 0; tries < maxTries; ++tries) {
        iterations += analyzeLowerBound1(maxN, width, 10000, random, 10000,false);
      }
      iterations /= maxTries;
      System.out.printf("%-4d %-4d %-4f %-4f\n", width, width, iterations, (width - 1) * width * 0.5 + 1);
    }
    System.out.println("Full");
    for (int width : intSteps(2, maxN, 1)) {
      double iterations = 0;
      for (int tries = 0; tries < maxTries; ++tries) {
        iterations += analyzeLowerBound1(maxN, width, 10000, random, 10000,true);
      }
      iterations /= maxTries;
      System.out.printf("%-4d %-4d %-4f %-4f\n", width, width, iterations, (width - 1) * width * 0.5 + 1);
    }

  }

  private static void analyzeUpperBound1() {
    int maxTries = 10;
    int maxN = 5;
    Random random = new Random(0);
    for (int pyramids : intSteps(1, 10, 1)) {
      double iterations = 0;
      double groundFormulae = 0;
      for (int tries = 0; tries < maxTries; ++tries) {
        Pair<Integer,Integer> result =  analyzeUpperBound1(maxN, pyramids, 10000, random, 10);
        iterations += result.arg1;
        groundFormulae += result.arg2;
      }
      iterations /= maxTries;
      groundFormulae /= maxTries;
      System.out.printf("%-4d %-4f %-4f %-4f\n", pyramids, iterations, groundFormulae / pyramids, groundFormulae);
    }
  }


  private static void analyzeOuterBound2() {
    int maxTries = 100;
    int maxN = 10;
    Random random = new Random(0);
    for (double trueFalseFactor : doubleSteps(1, 10, 0.2)) {
      double size = 0;
      double upperBoundTriangle = 0;
      double upperBoundColumn = 0;
      for (int tries = 0; tries < maxTries; ++tries) {
        Triple<Integer, Integer, Integer> result = analyzeOuterBound2(maxN, maxN, 10000, random, trueFalseFactor);
        size += result.arg1;
        upperBoundTriangle += result.arg2;
        upperBoundColumn += result.arg3;

      }
      size /= maxTries;
      upperBoundTriangle /= maxTries;
      upperBoundColumn /= maxTries;
      System.out.printf("%-3f %-4f %-4f %-4f\n", trueFalseFactor, size, upperBoundTriangle, upperBoundColumn);
    }
  }


  private static void analyzeInnerBound2() {
    int n = 20;
    for (int h = 1; h <= n - 1; ++h) {
      int actual = analyzeInnerBound2(n, h, -1000000, 1, 100, 0.1);
      int bound1 = 1;
      int bound2 = n - h;
      System.out.printf("%-4d %-4d %-4d %-4d\n", h, bound1, bound2, actual);
    }
  }

  private static Pair<Integer,Integer> analyzeUpperBound1(int maxN, int numberOfPyramids, double ruleWeight,
                                        Random random, double factorTrueFalse) {
    Signature signature = TheBeast.getInstance().createSignature();
    UserPredicate block = signature.createPredicate("block", "Int", "Int");
    UserPredicate score = signature.createPredicate("score", "Int", "Int", "Double");
    WeightFunction w1 = signature.createWeightFunction("w1", ruleWeight >= 0);
    FormulaBuilder builder = new FormulaBuilder(signature);

    Model model = signature.createModel();
    FactorFormula localScores = builder.parse("factor: for Int i, Int j, Double s if score(i,j,s) add [block(i,j)] * s");
    FactorFormula blockRule = builder.parse("factor: for Int i, Int j if score(i+1,j+1,_) & score(i,j,_)" +
            "add [block(i,j) & block(i+1,j) => block(i+1,j+1)] * w1");
    model.addFactorFormula(localScores);
    model.addFactorFormula(blockRule);
    model.addHiddenPredicate(block);
    model.addObservedPredicate(score);

    GroundAtoms gold = signature.createGroundAtoms();

    GroundAtoms observation = signature.createGroundAtoms();
    //create local scores

    for (int pyramid = 0; pyramid < numberOfPyramids; ++pyramid){
      int from = pyramid * maxN + 1;
      int to = from + maxN-1;
      for (int y = 0; y < maxN; ++y){
        observation.getGroundAtomsOf(score).addGroundAtom(from-1, y, -1.0 + random.nextDouble());
      }
      for (int x = from; x < to; ++x) {
        observation.getGroundAtomsOf(score).addGroundAtom(x, 0, factorTrueFalse + (1.0 - random.nextDouble()));
        for (int y = 1; y < maxN; ++y) {
          observation.getGroundAtomsOf(score).addGroundAtom(x, y, -1.0 + random.nextDouble());
        }
      }
    }


    Weights weights = signature.createWeights();
    weights.addWeight(w1, ruleWeight);

    CuttingPlaneSolver solver = new CuttingPlaneSolver();
    solver.configure(model, weights);
    ((IntegerLinearProgram) solver.getPropositionalModel()).setInitIntegers(true);

    solver.setObservation(observation);
    solver.solve();
    GroundAtoms guess = solver.getBestAtoms();
//    printBlocks(System.out, maxN, (maxN + 1) * numberOfPyramids, guess.getGroundAtomsOf(block));
//
//    System.out.println("solver.getIterationCount() = " + solver.getIterationCount());
//    System.out.println("solver.getPropositionalModel().getGroundAtomCount() = " + solver.getPropositionalModel().getGroundAtomCount());
//    System.out.println("solver.getPropositionalModel().getGroundFormulaCount() = " + solver.getPropositionalModel().getGroundFormulaCount());
    //System.out.println("solver.getPropositionalModel() = " + solver.getPropositionalModel());
    return new Pair<Integer, Integer>(solver.getIterationCount(), solver.getPropositionalModel().getGroundFormulaCount() / 4);
  }


  private static int analyzeLowerBound1(int maxN, int width, double weight,
                                        Random random, double factorTrueFalse,
                                        boolean full) {
    Signature signature = TheBeast.getInstance().createSignature();
    UserPredicate block = signature.createPredicate("block", "Int", "Int");
    UserPredicate score = signature.createPredicate("score", "Int", "Int", "Double");
    WeightFunction w1 = signature.createWeightFunction("w1", weight >= 0);
    FormulaBuilder builder = new FormulaBuilder(signature);

    Model model = signature.createModel();
    FactorFormula localScores = builder.parse("factor: for Int i, Int j, Double s if score(i,j,s) add [block(i,j)] * s");
    FactorFormula blockRule = builder.parse("factor: for Int i, Int j if score(i+1,j+1,_) & score(i,j,_)" +
            "add [block(i,j) & block(i+1,j) => block(i+1,j+1)] * w1");
    model.addFactorFormula(localScores);
    model.addFactorFormula(blockRule);
    model.addHiddenPredicate(block);
    model.addObservedPredicate(score);

    GroundAtoms gold = signature.createGroundAtoms();

    GroundAtoms observation = signature.createGroundAtoms();
    //create local scores
    for (int x = 0; x < maxN; ++x) {
      for (int y = 1; y < maxN; ++y) {
        //top of pyramid
        if (x == maxN-1 && y == width-1)
          observation.getGroundAtomsOf(score).addGroundAtom(x, y, -10000000.0);
        else if (maxN - x < width - y)
          observation.getGroundAtomsOf(score).addGroundAtom(x, y, full ?
            1.0 - random.nextDouble() : 
            -1.0 + random.nextDouble());
        else
          observation.getGroundAtomsOf(score).addGroundAtom(x, y, -1.0 + random.nextDouble());
      }
      //bottom row
      if (x < maxN - width) {
        observation.getGroundAtomsOf(score).addGroundAtom(x, 0, -1.0 + random.nextDouble());
      } else {
        observation.getGroundAtomsOf(score).addGroundAtom(x, 0, factorTrueFalse + (1.0 - random.nextDouble()));
      }
    }

    Weights weights = signature.createWeights();
    weights.addWeight(w1, weight);

    CuttingPlaneSolver solver = new CuttingPlaneSolver();
    solver.configure(model, weights);
    ((IntegerLinearProgram) solver.getPropositionalModel()).setInitIntegers(true);

    solver.setObservation(observation);
    solver.solve();
    GroundAtoms guess = solver.getBestAtoms();
    //printBlocks(System.out, maxN, guess.getGroundAtomsOf(block));
//
    //System.out.println("solver.getIterationCount() = " + solver.getIterationCount());
//    System.out.println("solver.getPropositionalModel().getGroundAtomCount() = " + solver.getPropositionalModel().getGroundAtomCount());
   //System.out.println("solver.getPropositionalModel().getGroundFormulaCount() = " + solver.getPropositionalModel().getGroundFormulaCount() / 4);
    //System.out.println("solver.getPropositionalModel() = " + solver.getPropositionalModel());
    return solver.getIterationCount();
  }


  private static int analyzeOuterBound1(int maxN, int width, double weight, Random random, double factorTrueFalse) {
    Signature signature = TheBeast.getInstance().createSignature();
    UserPredicate block = signature.createPredicate("block", "Int", "Int");
    UserPredicate score = signature.createPredicate("score", "Int", "Int", "Double");
    WeightFunction w1 = signature.createWeightFunction("w1", weight >= 0);
    FormulaBuilder builder = new FormulaBuilder(signature);

    Model model = signature.createModel();
    FactorFormula localScores = builder.parse("factor: for Int i, Int j, Double s if score(i,j,s) add [block(i,j)] * s");
    FactorFormula blockRule = builder.parse("factor: for Int i, Int j if score(i+1,j+1,_) & score(i,j,_)" +
            "add [block(i,j) & block(i+1,j) => block(i+1,j+1)] * w1");
    model.addFactorFormula(localScores);
    model.addFactorFormula(blockRule);
    model.addHiddenPredicate(block);
    model.addObservedPredicate(score);

    GroundAtoms gold = signature.createGroundAtoms();

    GroundAtoms observation = signature.createGroundAtoms();
    //create local scores
    for (int x = 0; x < maxN; ++x) {
      for (int y = 1; y < maxN; ++y) {
        observation.getGroundAtomsOf(score).addGroundAtom(x, y, -1.0 + random.nextDouble());
      }
      observation.getGroundAtomsOf(score).addGroundAtom(x, 0,
              x < maxN - width ? -1.0 + random.nextDouble() : factorTrueFalse * (1.0 - random.nextDouble()));
    }

    Weights weights = signature.createWeights();
    weights.addWeight(w1, weight);

    CuttingPlaneSolver solver = new CuttingPlaneSolver();
    solver.configure(model, weights);
    ((IntegerLinearProgram) solver.getPropositionalModel()).setInitIntegers(true);

    solver.setObservation(observation);
    solver.solve();
    GroundAtoms guess = solver.getBestAtoms();
//    printBlocks(System.out, maxN, guess.getGroundAtomsOf(block));
//
//    System.out.println("solver.getIterationCount() = " + solver.getIterationCount());
//    System.out.println("solver.getPropositionalModel().getGroundAtomCount() = " + solver.getPropositionalModel().getGroundAtomCount());
//    System.out.println("solver.getPropositionalModel().getGroundFormulaCount() = " + solver.getPropositionalModel().getGroundFormulaCount());
    //System.out.println("solver.getPropositionalModel() = " + solver.getPropositionalModel());
    return solver.getPropositionalModel().getGroundFormulaCount() / 4;
  }


  private static Triple<Integer, Integer, Integer> analyzeOuterBound2(int maxN, int width, double weight,
                                                                      Random random, double factorTrueFalse) {
    Signature signature = TheBeast.getInstance().createSignature();
    UserPredicate block = signature.createPredicate("block", "Int", "Int");
    UserPredicate score = signature.createPredicate("score", "Int", "Int", "Double");
    WeightFunction w1 = signature.createWeightFunction("w1", weight >= 0);
    FormulaBuilder builder = new FormulaBuilder(signature);

    Model model = signature.createModel();
    FactorFormula localScores = builder.parse("factor: for Int i, Int j, Double s if score(i,j,s) add [block(i,j)] * s");
    FactorFormula blockRule = builder.parse("factor: for Int i, Int j if score(i+1,j+1,_) & score(i,j,_)" +
            "add [block(i,j) & block(i+1,j) => block(i+1,j+1)] * w1");
    model.addFactorFormula(localScores);
    model.addFactorFormula(blockRule);
    model.addHiddenPredicate(block);
    model.addObservedPredicate(score);

    GroundAtoms gold = signature.createGroundAtoms();

    GroundAtoms observation = signature.createGroundAtoms();
    //create local scores
    double[][] local = new double[maxN][maxN];
    for (int x = 0; x < maxN; ++x) {
      for (int y = 1; y < maxN; ++y) {
        double s = -1.0 + random.nextDouble();
        observation.getGroundAtomsOf(score).addGroundAtom(x, y, s);
        local[x][y] = s;
      }
      double s = x < maxN - width ? -1.0 + random.nextDouble() : factorTrueFalse + (1.0 - random.nextDouble());
      local[x][0] = s;
      observation.getGroundAtomsOf(score).addGroundAtom(x, 0, s);
    }
    double[][] triangle = new double[maxN][maxN];
    double[][] column = new double[maxN][maxN];
    int upperBoundTriangle = 0;
    int upperBoundColumn = 0;
    for (int y = 0; y < maxN; ++y) {
      for (int x = y; x < maxN; ++x) {
        triangle[x][y] = local[x][y] + (y == 0 ? 0 : triangle[x - 1][y - 1] + column[x][y - 1]);
        column[x][y] = local[x][y] + (y == 0 ? 0 : column[x][y - 1]);
        if (y > 0) {
          double strengthTriangle = triangle[x - 1][y - 1] + column[x][y - 1];
          double strengthColumn = column[x][y - 1];
          //System.out.printf("%-5.2f ",strength);
          if (strengthTriangle > 0) upperBoundTriangle++;
          if (strengthColumn > 0) upperBoundColumn++;
        }
      }
      //System.out.println();
    }


    Weights weights = signature.createWeights();
    weights.addWeight(w1, weight);

    CuttingPlaneSolver solver = new CuttingPlaneSolver();
    solver.configure(model, weights);
    ((IntegerLinearProgram) solver.getPropositionalModel()).setInitIntegers(true);

    solver.setObservation(observation);
    solver.solve();
    GroundAtoms guess = solver.getBestAtoms();
    //printBlocks(System.out, maxN, guess.getGroundAtomsOf(block));
//
//    System.out.println("solver.getIterationCount() = " + solver.getIterationCount());
//    System.out.println("solver.getPropositionalModel().getGroundAtomCount() = " + solver.getPropositionalModel().getGroundAtomCount());
//    System.out.println("solver.getPropositionalModel().getGroundFormulaCount() = " + solver.getPropositionalModel().getGroundFormulaCount());
    //System.out.println("solver.getPropositionalModel() = " + solver.getPropositionalModel());
    return new Triple<Integer, Integer, Integer>(solver.getPropositionalModel().getGroundFormulaCount() / 4,
            upperBoundTriangle, upperBoundColumn);
  }


  private static int analyzeInnerBound1(int maxN, double weight, Random random) {
    Signature signature = TheBeast.getInstance().createSignature();
    UserPredicate block = signature.createPredicate("block", "Int", "Int");
    UserPredicate score = signature.createPredicate("score", "Int", "Int", "Double");
    WeightFunction w1 = signature.createWeightFunction("w1", weight >= 0);
    FormulaBuilder builder = new FormulaBuilder(signature);

    Model model = signature.createModel();
    FactorFormula localScores = builder.parse("factor: for Int i, Int j, Double s if score(i,j,s) add [block(i,j)] * s");
    FactorFormula blockRule = builder.parse("factor: for Int i, Int j if score(i+1,j+1,_) & score(i,j,_)" +
            "add [block(i,j) & block(i+1,j) => block(i+1,j+1)] * w1");
    model.addFactorFormula(localScores);
    model.addFactorFormula(blockRule);
    model.addHiddenPredicate(block);
    model.addObservedPredicate(score);

    GroundAtoms gold = signature.createGroundAtoms();

    GroundAtoms observation = signature.createGroundAtoms();
    //create local scores
    for (int x = 0; x < maxN; ++x) {
      for (int y = 1; y < maxN; ++y) {
        observation.getGroundAtomsOf(score).addGroundAtom(x, y, -random.nextDouble());
      }
      observation.getGroundAtomsOf(score).addGroundAtom(x, 0, 2 * random.nextDouble());
    }

    Weights weights = signature.createWeights();
    weights.addWeight(w1, weight);

    CuttingPlaneSolver solver = new CuttingPlaneSolver();
    solver.configure(model, weights);
    ((IntegerLinearProgram) solver.getPropositionalModel()).setInitIntegers(true);

    solver.setObservation(observation);
    solver.solve();
    GroundAtoms guess = solver.getBestAtoms();
    //printBlocks(System.out, maxN, guess.getGroundAtomsOf(block));

    //System.out.println("solver.getIterationCount() = " + solver.getIterationCount());
    //System.out.println("solver.getPropositionalModel().getGroundAtomCount() = " + solver.getPropositionalModel().getGroundAtomCount());
    //System.out.println("solver.getPropositionalModel().getGroundFormulaCount() = " + solver.getPropositionalModel().getGroundFormulaCount());
    //System.out.println("solver.getPropositionalModel() = " + solver.getPropositionalModel());
    return solver.getPropositionalModel().getGroundFormulaCount() / 4;
  }

  private static int analyzeInnerBound2(int maxN, int h,
                                        double topWeight,
                                        double middleWeight,
                                        double bottomWeight, double ruleWeight) {
    Signature signature = TheBeast.getInstance().createSignature();
    UserPredicate block = signature.createPredicate("block", "Int", "Int");
    UserPredicate score = signature.createPredicate("score", "Int", "Int", "Double");
    WeightFunction w1 = signature.createWeightFunction("w1", true);
    FormulaBuilder builder = new FormulaBuilder(signature);

    Model model = signature.createModel();
    FactorFormula localScores = builder.parse("factor: for Int i, Int j, Double s if score(i,j,s) add [block(i,j)] * s");
    FactorFormula blockRule = builder.parse("factor: for Int i, Int j if score(i+1,j+1,_) & score(i,j,_)" +
            "add [block(i,j) & block(i+1,j) => block(i+1,j+1)] * w1");
    FactorFormula hardRuleUp = builder.parse("factor: for Int i, Int j " +
            "if score(i,j+1,_) & score(i,j,_) & j >= " + h + ": " +
            "block(i,j)=> block(i,j+1)");
//    FactorFormula hardRuleRight = builder.parse("factor: for Int i, Int j " +
//            "if score(i,j,_) & score(i+1,j,_) & j >= " + h + ": " +
//            "block(i,j)=> block(i+1, j)");
    model.addFactorFormula(localScores);
    model.addFactorFormula(blockRule);
    model.addFactorFormula(hardRuleUp);
//    model.addFactorFormula(hardRuleRight);
    model.addHiddenPredicate(block);
    model.addObservedPredicate(score);


    GroundAtoms observation = signature.createGroundAtoms();
    //create local scores
    for (int y = 0; y < h; ++y) {
      for (int x = 0; x < maxN; ++x) {
        observation.getGroundAtomsOf(score).addGroundAtom(x, y, x >= y ? bottomWeight : topWeight);
      }
    }
    for (int y = h; y < maxN - 1; ++y) {
      for (int x = 0; x < maxN; ++x) {
        observation.getGroundAtomsOf(score).addGroundAtom(x, y, x >= y ? middleWeight : topWeight);

      }
    }
    for (int y = maxN - 1; y < maxN; ++y) {
      for (int x = 0; x < maxN; ++x) {
        observation.getGroundAtomsOf(score).addGroundAtom(x, y, topWeight);
      }
    }

    Weights weights = signature.createWeights();
    weights.addWeight(w1, ruleWeight);

    CuttingPlaneSolver solver = new CuttingPlaneSolver();
    solver.setShowSize(true);
    solver.setShowIterations(true);
    solver.configure(model, weights);
    ((IntegerLinearProgram) solver.getPropositionalModel()).setInitIntegers(true);

    solver.setObservation(observation);
    solver.solve();
    GroundAtoms guess = solver.getBestAtoms();
    //printBlocks(System.out, maxN, guess.getGroundAtomsOf(block));

    //System.out.println("solver.getIterationCount() = " + solver.getIterationCount());
    //System.out.println("solver.getPropositionalModel().getGroundAtomCount() = " + solver.getPropositionalModel().getGroundAtomCount());
    //System.out.println("solver.getPropositionalModel().getGroundFormulaCount() = " + solver.getPropositionalModel().getGroundFormulaCount());
    //System.out.println("solver.getPropositionalModel() = " + solver.getPropositionalModel());

    int count = countWeightedGroundFormulae(solver.getPropositionalModel().toString());

    System.out.println("count = " + count);
    System.out.println("solver.getFormulas().getAllGroundFormulas(blockRule).size = "
      + solver.getFormulas().getAllGroundFormulas(blockRule).value().size());
    

    return count;
  }

  private static int countWeightedGroundFormulae(String ilp) {
    int count = 0;
    for (String line : ilp.split("[\n]"))
      if (line.startsWith("-1.0 formula_")) ++count;
    return count;
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

  public static void printBlocks(PrintStream out, int n, int m, GroundAtomCollection atoms) {
    for (int y = 0; y < n; ++y) {
      for (int x = 0; x < m; ++x) {
        out.print(atoms.containsAtom(x, y) ? "X" : " ");
      }
      out.println();
    }


  }




}
