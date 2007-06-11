package thebeast.pml;

import thebeast.util.Counter;
import thebeast.pml.parser.Shell;
import thebeast.pml.corpora.TextFileCorpus;
import thebeast.pml.formula.FactorFormula;
import thebeast.nod.FileSource;

import java.util.Formatter;
import java.util.Iterator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;

/**
 * @author Sebastian Riedel
 */
@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
public class CorpusEvaluation {

  private int guessCount, goldCount, fnCount, fpCount;

  private Model model;


  private Counter<UserPredicate>
          guessCountPerPredicate = new Counter<UserPredicate>(),
          goldCountPerPredicate = new Counter<UserPredicate>(),
          fnCountPerPredicate = new Counter<UserPredicate>(),
          fpCountPerPredicate = new Counter<UserPredicate>();


  public CorpusEvaluation(Model model) {
    this.model = model;
  }

  public void add(Evaluation evaluation) {
    guessCount += evaluation.getGuessCount();
    goldCount += evaluation.getGoldCount();
    fnCount += evaluation.getFalseNegativesCount();
    fpCount += evaluation.getFalsePositivesCount();
    for (UserPredicate pred : model.getHiddenPredicates()) {
      goldCountPerPredicate.increment(pred, evaluation.getGold().getGroundAtomsOf(pred).size());
      guessCountPerPredicate.increment(pred, evaluation.getGuess().getGroundAtomsOf(pred).size());
      fnCountPerPredicate.increment(pred, evaluation.getFalseNegatives().getGroundAtomsOf(pred).size());
      fpCountPerPredicate.increment(pred, evaluation.getFalsePositives().getGroundAtomsOf(pred).size());
    }
  }


  public int getGuessCount() {
    return guessCount;
  }

  public int getGoldCount() {
    return goldCount;
  }

  public int getFalseNegativesCount() {
    return fnCount;
  }

  public int getFalsePositivesCount() {
    return fpCount;
  }


  public double getRecall() {
    double all = goldCount;
    if (all == 0) return 1.0;
    return (all - fnCount) / all;
  }

  public double getRecall(UserPredicate predicate) {
    double all = goldCountPerPredicate.get(predicate);
    if (all == 0) return 1.0;
    return (all - fnCountPerPredicate.get(predicate)) / all;
  }

  public double getPrecision() {
    double all = guessCount;
    if (all == 0) return 1.0;
    return (all - fpCount) / all;
  }

  public double getPrecision(UserPredicate predicate) {
    double all = guessCountPerPredicate.get(predicate);
    if (all == 0) return 1.0;
    return (all - fpCountPerPredicate.get(predicate)) / all;
  }

  public double getF1() {
    double recall = getRecall();
    double precision = getPrecision();
    return 2 * recall * precision / (recall + precision);
  }

  public double getF1(UserPredicate predicate) {
    double recall = getRecall(predicate);
    double precision = getPrecision(predicate);
    return 2 * recall * precision / (recall + precision);
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    Formatter formatter = new Formatter(result);
    result.append("Global").append("\n");
    for (int i = 0; i < 25; ++i) result.append("-");
    result.append("\n");
    formatter.format("%-20s%4.3f\n", "Recall", getRecall());
    formatter.format("%-20s%4.3f\n", "Precision", getPrecision());
    formatter.format("%-20s%4.3f\n", "F1", getF1());
    //result.append(formatter.toString());
    for (UserPredicate pred : model.getHiddenPredicates()) {
      result.append(pred.getName()).append("\n");
      for (int i = 0; i < 25; ++i) result.append("-");
      result.append("\n");
      formatter.format("%-20s%4.3f\n", "Recall", getRecall(pred));
      formatter.format("%-20s%4.3f\n", "Precision", getPrecision(pred));
      formatter.format("%-20s%4.3f\n", "F1", getF1(pred));
      //result.append(formatter.toString());
    }
    return result.toString();
  }

  public static void main(String[] args) throws Exception {
    String weightFileType = args[0];
    Model model = TheBeast.getInstance().loadModel(new FileInputStream(args[1]));
    Weights weights = model.getSignature().createWeights();
    if (weightFileType.equals("dump")) {
      FileSource source = TheBeast.getInstance().getNodServer().createSource(new File(args[2]), 1024);
      weights.read(source);
    } else if (weightFileType.equals("text")) {
      weights.load(new FileInputStream(args[2]));
    }
    TextFileCorpus gold = new TextFileCorpus(model.getSignature(), new File(args[3]));
    TextFileCorpus guess = new TextFileCorpus(model.getSignature(), new File(args[4]));
    CorpusEvaluation corpusEvaluation = new CorpusEvaluation(model);
    Iterator<GroundAtoms> i_guess = guess.iterator();
    Iterator<GroundAtoms> i_gold = gold.iterator();
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights);
    while (i_guess.hasNext()){
      Evaluation eval = new Evaluation(model);
      GroundAtoms gold_atoms = i_gold.next();
      GroundAtoms guess_atoms = i_guess.next();
      guess_atoms.load(gold_atoms, model.getObservedPredicates());
      eval.evaluate(gold_atoms, guess_atoms);
      corpusEvaluation.add(eval);
      GroundFormulas f_guess = new GroundFormulas(model,weights);
      f_guess.init();
      f_guess.update(guess_atoms);

      GroundFormulas f_gold = new GroundFormulas(model, weights);
      f_gold.init();
      f_gold.update(gold_atoms);
      Solution s_guess = new Solution(model, weights);
      s_guess.load(guess_atoms,f_guess);
      Solution s_gold = new Solution(model, weights);
      s_gold.load(gold_atoms,f_gold);

//      for (FactorFormula formula : model.getGlobalFactorFormulas()){
//        if (formula.isDeterministic()) continue;
//        System.err.println(formula.toString());
//        System.err.println("Weight:"+ weights.getWeight(formula.getWeightFunction()));
//        System.err.println(f_gold.getFalseGroundFormulas(formula).value().size());
//        System.err.println(f_guess.getFalseGroundFormulas(formula).value().size());
////        if (formula.getWeightFunction().getName().equals("w_bibvenue")){
////          System.err.println(f_gold.getFalseGroundFormulas(formula).value());
////        }
//      }


      LocalFeatures gold_features = new LocalFeatures(model, weights);
      LocalFeatures guess_features = new LocalFeatures(model, weights);
      extractor.extract(gold_atoms, gold_features);
      extractor.extract(guess_atoms, guess_features);

      FeatureVector gold_vector = s_gold.extract(gold_features);
      System.out.println("Gold Score: " + weights.score(gold_vector));
      FeatureVector guess_vector = s_guess.extract(guess_features);
      System.out.println("Guess Score: " + weights.score(guess_vector));
      System.out.println("Violations: " + f_guess.getViolationCount());

    }
    System.out.println(corpusEvaluation);
    //shell.load();
  }
}
