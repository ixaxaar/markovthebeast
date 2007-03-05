package thebeast.pml;

import thebeast.util.Counter;

import java.util.Formatter;

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

  public String toString(){
     StringBuffer result = new StringBuffer();
     for (UserPredicate pred : model.getHiddenPredicates()){
       result.append(pred.getName()).append("\n");
       for (int i = 0; i < 25; ++i) result.append("-");
       result.append("\n");
       Formatter formatter = new Formatter();
       formatter.format("%-20s%4.3f\n","Recall", getRecall(pred));
       formatter.format("%-20s%4.3f\n","Precision", getPrecision(pred));
       formatter.format("%-20s%4.3f\n","F1", getF1(pred));
       result.append(formatter.toString());
     }
     return result.toString();
   }


}
