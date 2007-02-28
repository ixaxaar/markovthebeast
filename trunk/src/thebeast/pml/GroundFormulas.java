package thebeast.pml;

import thebeast.nod.variable.RelationVariable;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.expression.RelationExpression;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.QueryGenerator;

import java.util.HashMap;

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
          explicitGroundFormulas = new HashMap<FactorFormula, RelationVariable>(),
          trueGroundFormulas = new HashMap<FactorFormula, RelationVariable>();
  private Model model;
  private Weights weights;

  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();

  private HashMap<FactorFormula, RelationExpression>
          trueQueries = new HashMap<FactorFormula, RelationExpression>(),
          falseQueries = new HashMap<FactorFormula, RelationExpression>();
  private GroundAtoms groundAtoms;

  private HashMap<UserPredicate, RelationExpression>
          cycleQueries = new HashMap<UserPredicate, RelationExpression>();


  private HashMap<UserPredicate, RelationVariable>
          cycles = new HashMap<UserPredicate, RelationVariable>();


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
          explicitGroundFormulas.put(formula, interpreter.createRelationVariable(formula.getSolutionHeading()));
          if (!formula.getWeight().isNonNegative())
            trueGroundFormulas.put(formula, interpreter.createRelationVariable(formula.getSolutionHeading()));
          if (!formula.getWeight().isNonPositive())
            falseGroundFormulas.put(formula, interpreter.createRelationVariable(formula.getSolutionHeading()));
        }
      }
    }
    QueryGenerator generator = new QueryGenerator();
    this.groundAtoms = weights.getSignature().createGroundAtoms();
    for (FactorFormula formula : model.getFactorFormulas()) {
      if (formula.isAcyclicityConstraint()) {
        cycleQueries.put(formula.getAcyclicityConstraint().getPredicate(),
                generator.generateCycleQuery(groundAtoms, formula.getAcyclicityConstraint()));
      } else if (!formula.isLocal()) {
        if (!formula.getWeight().isNonNegative())
          trueQueries.put(formula, generator.generateGlobalTrueQuery(formula, groundAtoms, weights));
        if (!formula.getWeight().isNonPositive()) {
          RelationExpression relationExpression = generator.generateGlobalFalseQuery(formula, groundAtoms, weights);
          falseQueries.put(formula, relationExpression);
        }
      }
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

  public void load(GroundFormulas formulas) {
    for (FactorFormula formula : trueGroundFormulas.keySet()) {
      interpreter.assign(getTrueGroundFormulas(formula), formulas.getTrueGroundFormulas(formula));
    }
    for (FactorFormula formula : falseGroundFormulas.keySet()) {
      interpreter.assign(getFalseGroundFormulas(formula), formulas.getFalseGroundFormulas(formula));
    }
    for (FactorFormula formula : explicitGroundFormulas.keySet()) {
      interpreter.assign(getExplicitGroundFormulas(formula), formulas.getExplicitGroundFormulas(formula));
    }
    for (UserPredicate predicate : cycles.keySet())
      interpreter.assign(getCycles(predicate), formulas.getCycles(predicate));
  }

  /**
   * Extract violated/true/both ground formulas within a given solution.
   *
   * @param solution the ground atoms we look for groundformulas in.
   */
  public void extract(GroundAtoms solution) {

    this.groundAtoms.load(solution);
    for (FactorFormula factorFormula : model.getFactorFormulas()) {
      if (factorFormula.isAcyclicityConstraint()) {
        UserPredicate predicate = factorFormula.getAcyclicityConstraint().getPredicate();
        interpreter.assign(getCycles(predicate), cycleQueries.get(predicate));
      } else if (!factorFormula.isLocal()) {
        RelationVariable both = getExplicitGroundFormulas(factorFormula);
        if (!factorFormula.getWeight().isNonNegative()) {
          RelationVariable relation = getTrueGroundFormulas(factorFormula);
          interpreter.assign(relation, trueQueries.get(factorFormula));
          interpreter.assign(both, relation);
        }
        if (!factorFormula.getWeight().isNonPositive()) {
          RelationVariable relation = getFalseGroundFormulas(factorFormula);
          interpreter.assign(relation, falseQueries.get(factorFormula));
          interpreter.insert(both, relation);
        }
      }
    }
  }

  public RelationVariable getExplicitGroundFormulas(FactorFormula formula) {
    return explicitGroundFormulas.get(formula);
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
      if (!formula.getWeight().isNonPositive()) {
        result.append("# False groundings of: ").append(formula.toString()).append("\n");
        result.append(getFalseGroundFormulas(formula).value().toString());
//        result.append("# All groundings:\n");
//        result.append(getExplicitGroundFormulas(formula).value().toString());
        result.append("\n");
      }
      if (!formula.getWeight().isNonNegative()) {
        result.append("# True groundings of: ").append(formula.toString()).append("\n");
        result.append(getTrueGroundFormulas(formula).value().toString());
        result.append("\n");
      }
    }
    return result.toString();
  }

  public void update(GroundAtoms solution) {
    this.groundAtoms.load(solution);
    //System.out.println(this.groundAtoms);
    for (FactorFormula factorFormula : model.getFactorFormulas()) {
      if (factorFormula.isAcyclicityConstraint()) {
        UserPredicate predicate = factorFormula.getAcyclicityConstraint().getPredicate();
        interpreter.assign(getCycles(predicate), cycleQueries.get(predicate));
      } else if (!factorFormula.isLocal()) {
        RelationVariable both = getExplicitGroundFormulas(factorFormula);
        interpreter.clear(both);
        if (!factorFormula.getWeight().isNonNegative()) {
          RelationVariable relation = getTrueGroundFormulas(factorFormula);
          interpreter.assign(relation, trueQueries.get(factorFormula));
          interpreter.assign(both, relation);
        }
        if (!factorFormula.getWeight().isNonPositive()) {
          RelationVariable relation = getFalseGroundFormulas(factorFormula);
          interpreter.assign(relation, falseQueries.get(factorFormula));
          interpreter.insert(both, relation);
        }
      }
    }
  }
}
