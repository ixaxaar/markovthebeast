package thebeast.pml;

import thebeast.nod.expression.AttributeExpression;
import thebeast.nod.expression.DepthFirstExpressionVisitor;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.Index;
import thebeast.nod.variable.RelationVariable;
import thebeast.pml.formula.AcyclicityConstraint;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.QueryGenerator;
import thebeast.pml.function.WeightFunction;
import thebeast.pml.term.Term;
import thebeast.util.NullProfiler;
import thebeast.util.Profiler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A FormulaStates object is container with
 * <p/>
 * <ul>
 * <p/>
 * <li>a collection of positive ground formulas which don't hold int the solution given by some ground atoms</li>
 * <p/>
 * <li>a collection of negative ground formulas which hold int the solution given by some ground atoms</li>
 * <p/>
 * </ul>
 */
public class GroundFormulas {

  private HashMap<FactorFormula, RelationVariable>
          falseGroundFormulas = new HashMap<FactorFormula, RelationVariable>(),
          allExplicitGroundFormulas = new HashMap<FactorFormula, RelationVariable>(),
          groundFormulas = new HashMap<FactorFormula, RelationVariable>(),
          explicitGroundFormulas = new HashMap<FactorFormula, RelationVariable>(),
          trueGroundFormulas = new HashMap<FactorFormula, RelationVariable>();
  private Model model;
  private Weights weights;

  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();

  private HashMap<FactorFormula, RelationExpression>
          trueQueries = new HashMap<FactorFormula, RelationExpression>(),
          falseQueries = new HashMap<FactorFormula, RelationExpression>(),
          allQueries = new HashMap<FactorFormula, RelationExpression>(),
          minusOld = new HashMap<FactorFormula, RelationExpression>();
  private GroundAtoms groundAtoms;

  private HashMap<UserPredicate, RelationExpression>
          cycleQueries = new HashMap<UserPredicate, RelationExpression>();


  private HashMap<UserPredicate, RelationVariable>
          cycles = new HashMap<UserPredicate, RelationVariable>();

  private boolean isDeterministic;

  private Profiler profiler = new NullProfiler();

  private boolean firstUpdate = true;

  private HashSet<FactorFormula> groundAll = new HashSet<FactorFormula>();


  /**
   * Creates a (read-only) copy of the given ground formulas.
   *
   * @param formulas the formulas to copy.
   */
  public GroundFormulas(GroundFormulas formulas) {
    this.model = formulas.model;
    this.weights = formulas.weights;
    for (FactorFormula formula : model.getFactorFormulas()) {
      if (!formula.isLocal()) {
        //Heading heading = formula.isParametrized() ? formula.getSolutionHeading() : formula.get
        if (formula.isAcyclicityConstraint()) {
          UserPredicate predicate = formula.getAcyclicityConstraint().getPredicate();
          cycles.put(predicate, interpreter.createRelationVariable(formulas.getCycles(predicate)));
        } else {
          explicitGroundFormulas.put(formula,
                  interpreter.createRelationVariable(formulas.getNewGroundFormulas(formula)));
          allExplicitGroundFormulas.put(formula,
                  interpreter.createRelationVariable(formulas.allExplicitGroundFormulas.get(formula)));
          groundFormulas.put(formula, interpreter.createRelationVariable(formula.getSolutionHeading()));
          if (formula.getWeight().isNonPositive() || !formula.getWeight().isNonNegative())
            trueGroundFormulas.put(formula,
                    interpreter.createRelationVariable(formulas.getTrueGroundFormulas(formula)));
          else if (formula.getWeight().isNonNegative())
            falseGroundFormulas.put(formula,
                    interpreter.createRelationVariable(formulas.getFalseGroundFormulas(formula)));
        }
      }
    }
  }

  /**
   * Creates an empty solution
   *
   * @param model   the model this solution is solving
   * @param weights the weights this set of ground formulas
   */
  public GroundFormulas(Model model, Weights weights) {
    this.model = model;
    this.weights = weights;
    for (FactorFormula formula : model.getFactorFormulas()) {
      if (!formula.isLocal()) {
        //Heading heading = formula.isParametrized() ? formula.getSolutionHeading() : formula.get
        if (formula.isAcyclicityConstraint()) {
          UserPredicate predicate = formula.getAcyclicityConstraint().getPredicate();
          cycles.put(predicate, interpreter.createRelationVariable(predicate.getHeadingCycle()));
        } else {
          allExplicitGroundFormulas.put(formula, interpreter.createRelationVariable(formula.getSolutionHeading()));
          explicitGroundFormulas.put(formula, interpreter.createRelationVariable(formula.getSolutionHeading()));
          groundFormulas.put(formula, interpreter.createRelationVariable(formula.getSolutionHeading()));
          if (formula.getWeight().isNonPositive() || !formula.getWeight().isNonNegative())
            trueGroundFormulas.put(formula, interpreter.createRelationVariable(formula.getSolutionHeading()));
          else if (formula.getWeight().isNonNegative())
            falseGroundFormulas.put(formula, interpreter.createRelationVariable(formula.getSolutionHeading()));
        }
      }
    }
    buildQueries();

  }

  /**
   * Builds the queries to extract ground formulas from ground atoms.
   */
  private void buildQueries() {
    QueryGenerator generator = new QueryGenerator();
    this.groundAtoms = this.weights.getSignature().createGroundAtoms();
    for (FactorFormula formula : this.model.getFactorFormulas()) {
      if (formula.isAcyclicityConstraint()) {
        cycleQueries.put(formula.getAcyclicityConstraint().getPredicate(),
                generator.generateCycleQuery(groundAtoms, formula.getAcyclicityConstraint()));
      } else if (!formula.isLocal()) {
        builder.expr(groundFormulas.get(formula)).expr(allExplicitGroundFormulas.get(formula)).relationMinus();
        minusOld.put(formula, builder.getRelation());
        if (formula.getWeight().isNonPositive() || !formula.getWeight().isNonNegative()) {
          RelationExpression query = generator.generateGlobalTrueQuery(formula, groundAtoms, this.weights);
          trueQueries.put(formula, query);
          if (formula.usesWeights()) {
            addIndices(this.weights, formula.getWeightFunction(), query);
          }
        } else if (formula.getWeight().isNonNegative()) {
          RelationExpression query = generator.generateGlobalFalseQuery(formula, groundAtoms, this.weights);
          falseQueries.put(formula, query);
          if (formula.usesWeights()) {
            addIndices(this.weights, formula.getWeightFunction(), query);
          }
        }
        try {
          RelationExpression query = generator.generateGlobalAllQuery(formula, groundAtoms, this.weights);
          allQueries.put(formula, query);
          if (formula.usesWeights()) {
            addIndices(this.weights, formula.getWeightFunction(), query);
          }
        } catch (RuntimeException e) {
          if (formula.getWeight().isFree()) throw e;
          //might happen for signed formulas and as long we don't need to fully ground them things are fine.
        }

      }

    }
  }


  /**
   * Formulas that are set to "ground all" will be fully grounded with the first update call, independently
   * of the formulas weight sign (if any).
   *
   * @param formula     the formula to fully ground (or not)
   * @param fullyGround true iff the formula should be fully grounded
   */
  public void setFullyGround(FactorFormula formula, boolean fullyGround) {
    if (fullyGround) groundAll.add(formula);
    else groundAll.remove(formula);
  }

  public Profiler getProfiler() {
    return profiler;
  }

  public void setProfiler(Profiler profiler) {
    this.profiler = profiler;
  }

  private void addIndices(Weights weights, WeightFunction weightFunction, RelationExpression query) {
    RelationVariable relvar = weights.getRelation(weightFunction);
    if (relvar.getIndex(weightFunction.getName()) == null) {
      final HashSet<String> bound = new HashSet<String>();
      query.acceptExpressionVisitor(new DepthFirstExpressionVisitor() {

        public void visitAttribute(AttributeExpression attribute) {
          if (attribute.prefix().equals("weights"))
            bound.add(attribute.attribute().name());
        }
      });
      addAllPossibleIndices(relvar, weightFunction, bound);
    }
  }

  private void addAllPossibleIndices(RelationVariable relvar, WeightFunction weightFunction, HashSet<String> bound) {
    if (bound.size() == 0) return;
    String name = bound.toString();
    if (!relvar.hasIndex(name)) interpreter.addIndex(relvar, name, Index.Type.HASH, bound);
    for (String var : bound) {
      HashSet<String> subset = new HashSet<String>(bound);
      subset.remove(var);
      addAllPossibleIndices(relvar, weightFunction, subset);
    }
  }

  /**
   * Returns a relvar/table that contains all groundings of the given formula which are true in the current solution
   *
   * @param factorFormula the factor formula we want groundings for.
   * @return a relvar with the true groundings. Has columns for each quantification variable.
   */
  public RelationVariable getTrueGroundFormulas(FactorFormula factorFormula) {
    return trueGroundFormulas.get(factorFormula);
  }

  /**
   * Returns a relvar/table that contains all groundings of the given formula which are false in the current solution
   *
   * @param factorFormula the factor formula we want groundings for.
   * @return a relvar with the false groundings. Has columns for each quantification variable.
   */
  public RelationVariable getFalseGroundFormulas(FactorFormula factorFormula) {
    return falseGroundFormulas.get(factorFormula);
  }

  /**
   * Returns a table that contains cycles for a given predicate.
   *
   * @param predicate the predicate we want to know the cycles of.
   * @return a relvar that contains rows which themselves contain a table with
   *         the members (i.e. pairs) of the cycles.
   */
  public RelationVariable getCycles(UserPredicate predicate) {
    return cycles.get(predicate);
  }


  public Model getModel() {
    return model;
  }

  public void load(GroundFormulas formulas, Collection<FactorFormula> factors) {
    for (FactorFormula formula : factors) {
      if (formula.getWeight().isNonPositive())
        interpreter.assign(getTrueGroundFormulas(formula), formulas.getTrueGroundFormulas(formula));
      else if (formula.getWeight().isNonNegative())
        interpreter.assign(getFalseGroundFormulas(formula), formulas.getFalseGroundFormulas(formula));
      else if (formula.isAcyclicityConstraint()) {
        UserPredicate predicate = formula.getAcyclicityConstraint().getPredicate();
        interpreter.assign(getCycles(predicate), formulas.getCycles(predicate));
      }
      interpreter.assign(getNewGroundFormulas(formula), formulas.getNewGroundFormulas(formula));

    }
  }

  public void load(GroundFormulas formulas) {
    isDeterministic = formulas.isDeterministic();
    for (FactorFormula formula : trueGroundFormulas.keySet()) {
      interpreter.assign(getTrueGroundFormulas(formula), formulas.getTrueGroundFormulas(formula));
    }
    for (FactorFormula formula : falseGroundFormulas.keySet()) {
      interpreter.assign(getFalseGroundFormulas(formula), formulas.getFalseGroundFormulas(formula));
    }
    for (FactorFormula formula : explicitGroundFormulas.keySet()) {
      interpreter.assign(getNewGroundFormulas(formula), formulas.getNewGroundFormulas(formula));
    }
    for (UserPredicate predicate : cycles.keySet())
      interpreter.assign(getCycles(predicate), formulas.getCycles(predicate));
  }

  public RelationVariable getNewGroundFormulas(FactorFormula formula) {
    return groundFormulas.get(formula);
    //return explicitGroundFormulas.get(formula);
  }

  public boolean hasChanged() {
    return false;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    for (FactorFormula formula : model.getGlobalFactorFormulas()) {
      if (formula.isAcyclicityConstraint()) {
        UserPredicate predicate = formula.getAcyclicityConstraint().getPredicate();
        result.append("# Cycles in: ").append(predicate.getName()).append("\n").append(cycles.get(predicate).value());
        continue;
      }
      //result.append(getExplicitGroundFormulas(formula).value().toString());
      if (formula.getWeight().isNonNegative()) {
        result.append("# False groundings of: ").append(formula.toString()).append("\n");
        result.append(getFalseGroundFormulas(formula).value().toString());
//        result.append("# All groundings:\n");
//        result.append(getExplicitGroundFormulas(formula).value().toString());
        result.append("\n");
      }
      if (formula.getWeight().isNonPositive()) {
        result.append("# True groundings of: ").append(formula.toString()).append("\n");
        result.append(getTrueGroundFormulas(formula).value().toString());
        result.append("\n");
      }
    }
    return result.toString();
  }

  /**
   * Find violated/true ground formulas for all formulas in the current model.
   *
   * @param solution the solution to extract groundings from.
   */
  public void update(GroundAtoms solution) {
    update(solution, model.getFactorFormulas());
    isDeterministic = false;
  }

  public void init() {
    firstUpdate = true;
    for (RelationVariable var : allExplicitGroundFormulas.values())
      interpreter.clear(var);
    for (RelationVariable var : explicitGroundFormulas.values())
      interpreter.clear(var);
    for (RelationVariable var : groundFormulas.values())
      interpreter.clear(var);
    for (RelationVariable var : trueGroundFormulas.values())
      interpreter.clear(var);
    for (RelationVariable var : falseGroundFormulas.values())
      interpreter.clear(var);
    groundAtoms.clear(model.getHiddenPredicates());
    groundAtoms.clear(model.getObservedPredicates());
    groundAtoms.clear(model.getGlobalPredicates());
    //buildQueries();

  }

  public int size() {
    int size = 0;
    for (RelationVariable var : allExplicitGroundFormulas.values())
      size += var.value().size();
    return size;
  }

  public void updateDeterministic(GroundAtoms solution) {
    clear();
    update(solution, model.getDeterministicFormulas());
    isDeterministic = true;
  }

  public boolean isDeterministic() {
    return isDeterministic;
  }

  public int getViolationCount() {
    int count = 0;
    for (FactorFormula formula : model.getDeterministicFormulas()) {
      if (formula.getFormula() instanceof AcyclicityConstraint) {
        AcyclicityConstraint acyclicityConstraint = (AcyclicityConstraint) formula.getFormula();
        count += cycles.get(acyclicityConstraint.getPredicate()).value().size();
      } else
        count += groundFormulas.get(formula).value().size();
    }
    return count;
  }


  /**
   * Find violated/true ground formulas for the specified quantified formulas within the
   * given solution
   *
   * @param solution the solution to extract the groundings from
   * @param formulas the formulas to find ground formulas for.
   */
  public void update(GroundAtoms solution, Collection<FactorFormula> formulas) {
    this.groundAtoms.load(model.getGlobalAtoms(), model.getGlobalPredicates());
    this.groundAtoms.load(solution, model.getInstancePredicates());
    //System.out.println(this.groundAtoms);
    for (FactorFormula factorFormula : formulas) {
      String name = factorFormula.toString();
      if (factorFormula.isAcyclicityConstraint()) {
        UserPredicate predicate = factorFormula.getAcyclicityConstraint().getPredicate();
        interpreter.assign(getCycles(predicate), cycleQueries.get(predicate));
      } else if (!factorFormula.isLocal()) {
        profiler.start("..." + name.substring(name.length() - 30));
        //RelationVariable both = getExplicitGroundFormulas(factorFormula);
        RelationVariable both = groundFormulas.get(factorFormula);
        interpreter.clear(both);
        boolean fullyGround = groundAll.contains(factorFormula);
        RelationVariable relation = null;
        Term weight = factorFormula.getWeight();
        if (weight.isNonPositive() || !weight.isNonNegative()) {
          relation = getTrueGroundFormulas(factorFormula);
          interpreter.assign(relation, trueQueries.get(factorFormula));
        } else {
          relation = getFalseGroundFormulas(factorFormula);
          interpreter.assign(relation, falseQueries.get(factorFormula));
        }
        if (firstUpdate && (weight.isFree() || fullyGround)) {
          interpreter.assign(both, allQueries.get(factorFormula));
        } else {
          interpreter.assign(both, relation);
        }
        //System.out.println(factorFormula);
        //System.out.println(both.value());
        //RelationVariable newFormulas = explicitGroundFormulas.get(factorFormula);
        //interpreter.assign(newFormulas, minusOld.get(factorFormula));
        //System.out.println(newFormulas.value());
        //interpreter.insert(allExplicitGroundFormulas.get(factorFormula), newFormulas);
        //System.out.println(allExplicitGroundFormulas.get(factorFormula).value());
        profiler.end();
      }
    }
    firstUpdate = false;
  }

  public RelationVariable getAllGroundFormulas(FactorFormula formula) {
    return allExplicitGroundFormulas.get(formula);
  }

  public void clear() {
    for (RelationVariable var : cycles.values())
      interpreter.clear(var);
    for (RelationVariable var : trueGroundFormulas.values())
      interpreter.clear(var);
    for (RelationVariable var : falseGroundFormulas.values())
      interpreter.clear(var);
    for (RelationVariable var : explicitGroundFormulas.values())
      interpreter.clear(var);
    for (RelationVariable var : groundFormulas.values())
      interpreter.clear(var);

  }
}
