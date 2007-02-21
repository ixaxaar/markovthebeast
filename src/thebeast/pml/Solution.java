package thebeast.pml;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.Summarize;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.type.Attribute;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.QueryGenerator;

import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class Solution {

  private GroundFormulas groundFormulas;
  private GroundAtoms groundAtoms;
  private Model model;
  private Weights weights;
  private HashMap<FactorFormula, RelationExpression>
          localExtractors = new HashMap<FactorFormula, RelationExpression>();
  private HashMap<FactorFormula, RelationExpression>
          localSummarizer = new HashMap<FactorFormula, RelationExpression>();
  private HashMap<UserPredicate, RelationExpression>
          localSummarizerForFeatures = new HashMap<UserPredicate, RelationExpression>();
  private HashMap<UserPredicate, RelationExpression>
          localJoin = new HashMap<UserPredicate, RelationExpression>();
  private HashMap<FactorFormula, RelationExpression>
          globalFalseSummarizer = new HashMap<FactorFormula, RelationExpression>(),
          globalTrueSummarizer = new HashMap<FactorFormula, RelationExpression>();
  private HashMap<FactorFormula, RelationVariable>
          tmpFeatures = new HashMap<FactorFormula, RelationVariable>();
  private HashMap<UserPredicate, RelationVariable>
          tmpFeaturesPerPred = new HashMap<UserPredicate, RelationVariable>();
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();

  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());

  private LocalFeatures localFeatures;

  private boolean groundFormulasNeedUpdate;

  public Solution(Model model, Weights weights) {
    groundAtoms = model.getSignature().createGroundAtoms();
    groundFormulas = new GroundFormulas(model, weights);
    localFeatures = new LocalFeatures(model, weights);
    this.model = model;
    this.weights = weights;
    QueryGenerator queryGenerator = new QueryGenerator(this.weights, groundAtoms);
    for (FactorFormula factorFormula : model.getLocalFactorFormulas()) {
      localExtractors.put(factorFormula,
              queryGenerator.generateLocalFeatureExtractor(factorFormula, groundAtoms, weights));
      RelationVariable var = interpreter.createRelationVariable(factorFormula.getHeadingIndex());
      tmpFeatures.put(factorFormula, var);
      builder.expr(var).by("index").num(1.0).summarizeAs("value", Summarize.Spec.DOUBLE_SUM).summarize();
      localSummarizer.put(factorFormula, builder.getRelation());
    }
    for (FactorFormula factorFormula : model.getGlobalFactorFormulas()) {
      if (factorFormula.isParametrized()) {
        if (!factorFormula.getWeight().isNonPositive()) {
          builder.expr(groundFormulas.getFalseGroundFormulas(factorFormula));
          builder.by("index").num(-1.0).summarizeAs("value", Summarize.Spec.DOUBLE_SUM).summarize();
          globalFalseSummarizer.put(factorFormula, builder.getRelation());
        }
        if (!factorFormula.getWeight().isNonNegative()) {
          builder.expr(groundFormulas.getTrueGroundFormulas(factorFormula));
          builder.by("index").num(1.0).summarizeAs("value", Summarize.Spec.DOUBLE_SUM).summarize();
          globalTrueSummarizer.put(factorFormula, builder.getRelation());
        }
      }
    }
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      RelationVariable var = interpreter.createRelationVariable(predicate.getHeadingIndex());
      tmpFeaturesPerPred.put(predicate, var);
      builder.expr(groundAtoms.getGroundAtomsOf(predicate).getRelationVariable()).from("atoms");
      builder.expr(localFeatures.getRelation(predicate)).from("features");
      for (Attribute att : predicate.getHeading().attributes()) {
        builder.attribute("atoms", att).attribute("features", att).equality();
      }
      builder.and(predicate.getArity()).where();
      for (Attribute att : predicate.getHeading().attributes()) {
        builder.id(att.name()).attribute("atoms", att);
      }
      builder.id("index").intAttribute("features", "index");
      builder.tuple(predicate.getArity() + 1).select().query();
      localJoin.put(predicate, builder.getRelation());

      builder.expr(var).by("index").num(1.0).summarizeAs("value", Summarize.Spec.DOUBLE_SUM).summarize();
      localSummarizerForFeatures.put(predicate, builder.getRelation());
      builder.expr(var).by("index");
      builder.num(1.0).summarizeAs("value", Summarize.Spec.DOUBLE_SUM).summarize();
      localSummarizerForFeatures.put(predicate, builder.getRelation());
    }

  }

  public GroundFormulas getGroundFormulas() {
    if (groundFormulasNeedUpdate) updateGroundFormulas();
    return groundFormulas;
  }

  public GroundAtoms getGroundAtoms() {
    return groundAtoms;
  }

  public void updateGroundFormulas() {
    groundFormulas.extract(groundAtoms);
    groundFormulasNeedUpdate = false;
  }

  public SparseVector extract() {
    //extract args + index into tmp vars
    SparseVector result = new SparseVector();
    for (FactorFormula formula : model.getLocalFactorFormulas()) {
      interpreter.assign(tmpFeatures.get(formula), localExtractors.get(formula));
      SparseVector tmp = new SparseVector();
      interpreter.assign(tmp.getValues(), localSummarizer.get(formula));
      result.addInPlace(1.0, tmp);
    }
    for (FactorFormula formula : model.getGlobalFactorFormulas()) {
      SparseVector tmp = new SparseVector();
      if (formula.isParametrized()) {
        if (!formula.getWeight().isNonNegative())
          interpreter.insert(tmp.getValues(), globalTrueSummarizer.get(formula));
        if (!formula.getWeight().isNonPositive())
          interpreter.insert(tmp.getValues(), globalFalseSummarizer.get(formula));
        result.addInPlace(1.0, tmp);
      }
    }
    return result;
  }

  public SparseVector extract(LocalFeatures features) {
    SparseVector result = new SparseVector();
    localFeatures.load(features);
    for (UserPredicate pred : model.getHiddenPredicates()) {
      RelationVariable var = tmpFeaturesPerPred.get(pred);
      interpreter.assign(var, localJoin.get(pred));
      SparseVector tmp = new SparseVector();
      interpreter.assign(tmp.getValues(), localSummarizerForFeatures.get(pred));
      result.addInPlace(1.0, tmp);
    }
    for (FactorFormula formula : model.getGlobalFactorFormulas()) {
      SparseVector tmp = new SparseVector();
      if (formula.isParametrized()) {
        if (!formula.getWeight().isNonNegative())
          interpreter.insert(tmp.getValues(), globalTrueSummarizer.get(formula));
        if (!formula.getWeight().isNonPositive())
          interpreter.insert(tmp.getValues(), globalFalseSummarizer.get(formula));
        result.addInPlace(1.0, tmp);
      }
    }
    return result;
  }

  public void load(GroundAtoms groundAtoms, GroundFormulas groundFormulas) {
    this.groundAtoms.load(groundAtoms);
    this.groundFormulas.load(groundFormulas);
  }

  public void load(GroundAtoms groundAtoms) {
    this.groundAtoms.load(groundAtoms);
    //this.groundFormulas.load(groundFormulas);
  }


  public void load(Solution solution) {
    this.groundAtoms.load(solution.groundAtoms);
    groundFormulasNeedUpdate = true;
  }

  public Solution copy() {
    Solution result = new Solution(model, weights);
    result.load(this);
    return result;
  }

}
