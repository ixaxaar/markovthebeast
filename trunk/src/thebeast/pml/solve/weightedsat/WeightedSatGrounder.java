package thebeast.pml.solve.weightedsat;

import thebeast.nod.expression.*;
import thebeast.nod.type.Attribute;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.RelationVariable;
import thebeast.pml.*;
import thebeast.pml.formula.*;
import thebeast.pml.term.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A WeightedSatGrounder creates NoD queries for factor formulas that generate grounded
 * sat clauses. This class is tightly connected with the {@link thebeast.pml.solve.weightedsat.WeightedSatProblem}
 * class.
 *
 * @author Sebastian Riedel
 */
public class WeightedSatGrounder {

  /**
   * <p>Returns a query that generates sat clauses for the grounded formulas in <code>formulas</code>
   * and fills the <code>satAtoms</code> table with new ground atoms + indices if necessary.
   * <p>the query generates tables of the form |0.234|[[1,0,0],[0,1]]|[[2,3,4],[2,5]]| where the first
   * component refers to weight of the clause, the first
   * inner tuples refer to the signs of the cnf and the second one to corresponding atom indices.
   *
   * @param formula  the factor formula to create a grounding query for
   * @param formulas a collection of ground atoms
   * @param wsp      the weighted sat problem that the query should get the ground atom indices from (and to
   *                 which it will add new ground atoms if a formula requires this.
   * @return a query that generates tables with grounded weighted sat clauses and (as side effect)
   *         fills up the ground atom mappings in the specified wsp.
   */
  RelationExpression createGroundingQuery(FactorFormula formula, GroundFormulas formulas, WeightedSatProblem wsp) {
    if (formula.isLocal())
      throw new RuntimeException("It doesn't make sense to create a grounding query for a local formula: " + formula);

    ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    BooleanFormula booleanFormula = formula.getFormula();
    CNFGenerator cnfGenerator = new CNFGenerator();
    CNF cnf = cnfGenerator.convertToCNF(booleanFormula);
    RelationVariable groundFormulas = formulas.getNewGroundFormulas(formula);
    Quantification quantification = formula.getQuantification();

    //create the variable mapping
    Map<Variable, Expression> term2expr = createMapping(quantification);

    //build the sign tuples
    Expression disjunctionSigns = getDisjunctionSigns(cnf);

    //build the atom indices tuples
    Expression disjunctionAtoms = getDisjunctionAtoms(cnf, wsp, term2expr);

    //set from table and prefix
    builder.expr(groundFormulas).from("formulas");

    //get the weight
    DoubleExpression weight = formula.isDeterministic() ?
            builder.num(20.0).getDouble() : getFormulaWeight(builder, wsp.getWeights());

    builder.id("weight").expr(weight);
    builder.id("signs").expr(disjunctionSigns);
    builder.id("atoms").expr(disjunctionAtoms);
    builder.tuple(3).select().query();

    return builder.getRelation();
  }

  private Map<Variable, Expression> createMapping(Quantification quantification) {
    ExpressionBuilder builder = TheBeast.getInstance().getNodServer().expressionBuilder();
    Map<Variable, Expression> term2expr;
    term2expr = new HashMap<Variable, Expression>();
    int varIndex = 0;
    for (Variable var : quantification.getVariables()){
      Attribute attribute = quantification.getAttribute(varIndex++);
      Expression expression = builder.attribute("formulas", attribute).getExpression();
      term2expr.put(var, expression);
    }
    return term2expr;
  }

  private DoubleExpression getFormulaWeight(ExpressionBuilder builder, Weights weights){
    builder.expr(weights.getWeights());
    builder.intAttribute("formulas","index").doubleArrayElement();
    return builder.getDouble();
  }

  private IntExpression getAtomIndex(PredicateAtom atom, WeightedSatProblem wsp, Map<Variable, Expression> term2expr){
    Scores scores = wsp.getScores();
    if (!(atom.getPredicate() instanceof UserPredicate))
      throw new RuntimeException("Can only get atom indices for user predicates");
    UserPredicate pred = (UserPredicate) atom.getPredicate();
    ExpressionBuilder builder = TheBeast.getInstance().getNodServer().expressionBuilder();
    builder.expr(wsp.getMapping(pred));
    NoDExpressionGenerator gen = new NoDExpressionGenerator();
    for (int arg = 0; arg < pred.getArity(); ++arg){
      Expression expr = gen.convertTerm(atom.getArguments().get(arg),null,null,term2expr, null);
      builder.id(pred.getColumnName(arg)).expr(expr);
    }
    builder.tupleForIds();
    builder.id("index").expr(wsp.getAtomCounter()).intPostInc();
    builder.id("score").expr(scores.getScoreRelation(pred));
    for (int arg = 0; arg < pred.getArity(); ++arg){
      Expression expr = gen.convertTerm(atom.getArguments().get(arg),null,null,term2expr, null);
      builder.id(pred.getColumnName(arg)).expr(expr);
    }
    builder.tuple(pred.getArity()).id("score").num(0.0).tuple(1);
    builder.get().doubleExtractComponent("score").tuple(2);
    builder.getPut().intExtractComponent("index");
    return builder.getInt();
  }

  private ArrayExpression getDisjunctionSigns(CNF cnf){
    ExpressionBuilder builder = TheBeast.getInstance().getNodServer().expressionBuilder();
    for (int disjunction = 0; disjunction < cnf.getDisjunctionCount();++disjunction){
      boolean[] signs = cnf.getSigns(disjunction);
      for (boolean sign : signs){
        builder.bool(sign);
      }
      builder.array(signs.length);
    }
    builder.array(cnf.getDisjunctionCount());
    return builder.getArray();
  }

   private ArrayExpression getDisjunctionAtoms(CNF cnf, WeightedSatProblem wsp, Map<Variable,Expression> term2epxr){
    ExpressionBuilder builder = TheBeast.getInstance().getNodServer().expressionBuilder();
    for (int disjunction = 0; disjunction < cnf.getDisjunctionCount();++disjunction){
      List<SignedAtom> atoms = cnf.getDisjunction(disjunction);
      for (SignedAtom atom : atoms){
        builder.expr(getAtomIndex((PredicateAtom) atom.getAtom(),wsp,term2epxr));     
      }
      builder.array(atoms.size());
    }
    builder.array(cnf.getDisjunctionCount());
    return builder.getArray();
  }



}
