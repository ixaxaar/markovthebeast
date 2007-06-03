package thebeast.pml;

import thebeast.util.Counter;
import thebeast.pml.parser.Shell;
import thebeast.pml.corpora.TextFileCorpus;

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
    for (UserPredicate pred : model.getHiddenPredicates()) {
      result.append(pred.getName()).append("\n");
      for (int i = 0; i < 25; ++i) result.append("-");
      result.append("\n");
      Formatter formatter = new Formatter();
      formatter.format("%-20s%4.3f\n", "Recall", getRecall(pred));
      formatter.format("%-20s%4.3f\n", "Precision", getPrecision(pred));
      formatter.format("%-20s%4.3f\n", "F1", getF1(pred));
      result.append(formatter.toString());
    }
    return result.toString();
  }

  public static void main(String[] args) throws Exception {
    Model model = TheBeast.getInstance().loadModel(new FileInputStream(args[0]));
    Weights weights = model.getSignature().createWeights();
    weights.load(new FileInputStream(args[1]));
    TextFileCorpus gold = new TextFileCorpus(model.getSignature(), new File(args[2]));
    TextFileCorpus guess = new TextFileCorpus(model.getSignature(), new File(args[3]));
    CorpusEvaluation corpusEvaluation = new CorpusEvaluation(model);
    Iterator<GroundAtoms> i_guess = guess.iterator();
    Iterator<GroundAtoms> i_gold = gold.iterator();
    while (i_guess.hasNext()){
      Evaluation eval = new Evaluation(model);
      eval.evaluate(i_gold.next(), i_guess.next());
      corpusEvaluation.add(eval);
      GroundFormulas f_guess = new GroundFormulas(model,weights);
      f_guess.init();
      f_guess.update(eval.getGuess());
      GroundFormulas f_gold = new GroundFormulas(model, weights);
      f_gold.init();
      f_gold.update(eval.getGold());
      Solution s_guess = new Solution(model, weights);
      s_guess.load(eval.getGuess(),f_guess);
      Solution s_gold = new Solution(model, weights);
      s_gold.load(eval.getGold(),f_gold);
      System.out.println("Gold Score: " + weights.score(s_gold.extract()));
      System.out.println("Guess Score: " + weights.score(s_guess.extract()));

    }
    System.out.println(corpusEvaluation);
    //shell.load();
  }
}
