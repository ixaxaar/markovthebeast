package thebeast.pml;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.statement.Interpreter;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 08-Feb-2007 Time: 21:19:43
 */
public class Evaluation {

  private GroundAtoms gold, guess, falsePositives, falseNegatives;

  private Model model;

  private HashMap<UserPredicate, RelationExpression>
          fpQueries = new HashMap<UserPredicate, RelationExpression>(),
          fnQueries = new HashMap<UserPredicate, RelationExpression>();

  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();

  public Evaluation(Model model) {
    this.model = model;
    gold = model.getSignature().createGroundAtoms();
    guess = model.getSignature().createGroundAtoms();
    falsePositives = model.getSignature().createGroundAtoms();
    falseNegatives = model.getSignature().createGroundAtoms();
    ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    for (UserPredicate pred : model.getHiddenPredicates()){
      builder.expr(gold.getGroundAtomsOf(pred).getRelationVariable());
      builder.expr(guess.getGroundAtomsOf(pred).getRelationVariable());
      builder.relationMinus();
      fnQueries.put(pred, builder.getRelation());
      builder.expr(guess.getGroundAtomsOf(pred).getRelationVariable());
      builder.expr(gold.getGroundAtomsOf(pred).getRelationVariable());
      builder.relationMinus();
      fpQueries.put(pred, builder.getRelation());
    }

  }


  public GroundAtoms getGold() {
    return gold;
  }

  public GroundAtoms getGuess() {
    return guess;
  }

  public void evaluate(GroundAtoms gold, GroundAtoms guess) {
    this.gold.load(gold,model.getHiddenPredicates());
    this.guess.load(guess, model.getHiddenPredicates());
    for (UserPredicate pred : model.getHiddenPredicates()){
      interpreter.assign(falsePositives.getGroundAtomsOf(pred).getRelationVariable(), fpQueries.get(pred));
      interpreter.assign(falseNegatives.getGroundAtomsOf(pred).getRelationVariable(), fnQueries.get(pred));
    }
  }

  public double getRecall(UserPredicate pred){
    double all = gold.getGroundAtomsOf(pred).getRelationVariable().value().size();
    double fn = falseNegatives.getGroundAtomsOf(pred).getRelationVariable().value().size();
    return (all - fn) / all;
  }

  public double getPrecision(UserPredicate pred){
    double all = gold.getGroundAtomsOf(pred).getRelationVariable().value().size();
    double fp = falsePositives.getGroundAtomsOf(pred).getRelationVariable().value().size();
    return (all - fp) / all;
  }

  public double getF1(UserPredicate predicate){
    double recall = getRecall(predicate);
    double precision = getPrecision(predicate);
    return 2 * recall * precision / (recall + precision);
  }

  public GroundAtoms getFalsePositives(){
    return falsePositives;
  }

  public int getFalsePositivesCount(){
    return falsePositives.getGroundAtomCount();
  }


  public GroundAtoms getFalseNegatives(){
    return falseNegatives;
  }

  public int getFalseNegativesCount(){
    return falseNegatives.getGroundAtomCount();
  }

  public int getGoldCount(){
    return gold.getGroundAtomCount();
  }

  public int getGuessCount(){
    return guess.getGroundAtomCount();
  }

}
