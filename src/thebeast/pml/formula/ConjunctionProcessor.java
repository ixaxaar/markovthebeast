package thebeast.pml.formula;

import thebeast.nod.expression.*;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.pml.*;
import thebeast.pml.predicate.*;
import thebeast.pml.predicate.IntLEQ;
import thebeast.pml.term.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 12-Feb-2007 Time: 21:39:37
 */
public class ConjunctionProcessor {
  private DNFGenerator dnfGenerator = new DNFGenerator();
  private CNFGenerator cnfGenerator = new CNFGenerator();
  private TermResolver termResolver = new TermResolver();
  private FormulaResolver formulaResolver = new FormulaResolver();
  private TermInverter inverter = new TermInverter();
  private NoDExpressionGenerator exprGenerator = new NoDExpressionGenerator();
  private GroundAtoms groundAtoms;
  private Weights weights;
  private ExpressionFactory factory;
  private FormulaBuilder builder;

  public static class Context {
    public LinkedList<BoolExpression> conditions = new LinkedList<BoolExpression>();
    public HashMap<Variable, Expression> var2expr = new HashMap<Variable, Expression>();
    public HashMap<Variable, Term> var2term = new HashMap<Variable, Term>();
    public LinkedList<RelationExpression> relations = new LinkedList<RelationExpression>();
    public LinkedList<String> prefixes = new LinkedList<String>();
    public ExpressionBuilder selectBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    public HashMap<String, Term> remainingHiddenArgs = new HashMap<String, Term>();

    public Context() {
    }

    public Context(Map<Variable, Term> var2term) {
      this.var2term.putAll(var2term);
    }


  }

  public ConjunctionProcessor(Weights weights, GroundAtoms groundAtoms) {
    this.weights = weights;
    this.groundAtoms = groundAtoms;
    this.builder = new FormulaBuilder(weights.getSignature());
    factory = TheBeast.getInstance().getNodServer().expressionFactory();

  }

  public void resolveBruteForce(final Context context, List<SignedAtom> conjunction) {
    UnresolvedVariableCollector finalCollector = new UnresolvedVariableCollector();
    finalCollector.bind(context.var2expr.keySet());
    finalCollector.bind(context.var2term.keySet());
    for (SignedAtom atom : conjunction) {
      atom.getAtom().acceptBooleanFormulaVisitor(finalCollector);
      for (Variable var : finalCollector.getUnresolved()) {
        Type type = var.getType();
        if (type.isNumeric())
          throw new RuntimeException("Unresolved numeric variable " + var + "! Will blow up search space");
        context.prefixes.add(var.getName());
        RelationExpression all = factory.createAllConstants((CategoricalType) type.getNodType());
        AttributeExpression att = factory.createAttribute(var.getName(), all.type().heading().attribute("value"));
        context.var2expr.put(var, att);
        context.var2term.put(var, var);
        context.relations.add(all);
      }
      finalCollector.bind(finalCollector.getUnresolved());
      finalCollector.getUnresolved().clear();
    }

  }

  public void processConjunction(final Context context, List<SignedAtom> conjunction) {
    final LinkedList<SignedAtom> atoms = new LinkedList<SignedAtom>(conjunction);
    int atomIndex = 0;
    final HashSet<SignedAtom> triedOnce = new HashSet<SignedAtom>();

    while (atoms.size() > 0) {
      final SignedAtom signedAtom = atoms.remove(0);
      final Atom atom = signedAtom.getAtom();
      final boolean sign = signedAtom.isTrue();
      final int index = atomIndex;
      atom.acceptAtomVisitor(new AtomVisitor() {
        public void visitPredicateAtom(final PredicateAtom predicateAtom) {
          final String prefix = predicateAtom.getPredicate().getName() + "_" + String.valueOf(index);
          predicateAtom.getPredicate().acceptPredicateVisitor(new PredicateVisitor() {

            public void visitUserPredicate(UserPredicate userPredicate) {
              int argIndex = 0;
              LinkedList<BoolExpression> localConditions = new LinkedList<BoolExpression>();
              main:
              if (sign) {
                for (Term arg : predicateAtom.getArguments()) {
                  if (arg instanceof DontCare) {
                    ++argIndex;
                    continue;
                  }
                  String varName = prefix + "_" + userPredicate.getColumnName(argIndex);
                  Variable artificial = new Variable(arg.getType(), varName);
                  context.var2expr.put(artificial, factory.createAttribute(prefix, userPredicate.getAttribute(argIndex)));
                  Term resolved = termResolver.resolve(arg, context.var2term);
                  if (termResolver.getUnresolved().size() == 0) {
                    builder.var(artificial).term(resolved).equality();
                    localConditions.add((BoolExpression) exprGenerator.convertFormula(
                            builder.getFormula(), groundAtoms, weights, context.var2expr, context.var2term));
                  } else if (termResolver.getUnresolved().size() == 1) {
                    Variable toResolve = termResolver.getUnresolved().get(0);
                    Term inverted = inverter.invert(resolved, artificial, toResolve);
                    context.var2term.put(toResolve, inverted);
                  } else {
                    if (triedOnce.contains(signedAtom)) {
                      throw new RuntimeException("Seems like we really can't resolve " + signedAtom);
                    }
                    atoms.add(signedAtom);
                    triedOnce.add(signedAtom);
                    break;
                  }
                  ++argIndex;
                }
                context.prefixes.add(prefix);
                context.relations.add(groundAtoms.getGroundAtomsOf(userPredicate).getRelationVariable());
              } else {
                builder.clear();
                for (Term arg : predicateAtom.getArguments()) {
                  if (arg instanceof DontCare) {
                    builder.term(arg);
                    ++argIndex;
                    continue;
                  }
                  Term resolved = termResolver.resolve(arg, context.var2term);
                  if (termResolver.getUnresolved().size() == 0) {
                    builder.term(resolved);
                  } else {
                    if (triedOnce.contains(signedAtom))
                      throw new RuntimeException("Seems like we really can't resolve " + signedAtom);
                    atoms.add(signedAtom);
                    triedOnce.add(signedAtom);
                    break main;
                  }
                  ++argIndex;
                }
                builder.atom(predicateAtom.getPredicate());
                builder.not();
                localConditions.add((BoolExpression) exprGenerator.convertFormula(
                        builder.getFormula(), groundAtoms, weights, context.var2expr, context.var2term));
              }
              context.conditions.addAll(localConditions);
            }

            public void visitPredicate(Predicate predicate) {
              BooleanFormula resolved = formulaResolver.resolve(atom, context.var2term);
              if (resolved == null) {
                atoms.add(signedAtom);
                return;
              }
              context.conditions.add((BoolExpression) exprGenerator.convertFormula(
                      resolved, groundAtoms, weights, context.var2expr, context.var2term));
            }

            public void visitEquals(Equals equals) {
              visitPredicate(equals);
            }

            public void visitIntLEQ(IntLEQ intLEQ) {
              visitPredicate(intLEQ);
            }

            public void visitNotEquals(NotEquals notEquals) {
              visitPredicate(notEquals);
            }

            public void visitIntLT(IntLT intLT) {
              visitPredicate(intLT);
            }

            public void visitIntGT(IntGT intGT) {
              visitPredicate(intGT);
            }
          });

        }

        public void visitCardinalityConstraint(CardinalityConstraint cardinalityConstraint) {

          //we have to check whether we can resolve all unbound variables in the main clause of the constraint
          try {
            if (!sign) {
              cardinalityConstraint = new CardinalityConstraint(false, cardinalityConstraint);
            }
            context.conditions.add((BoolExpression)
                    exprGenerator.convertFormula(cardinalityConstraint, groundAtoms, weights,
                            context.var2expr, context.var2term));
          } catch (NoDExpressionGenerator.UnresolvableVariableException e) {
            if (triedOnce.contains(signedAtom))
              throw new RuntimeException("Seems like we really can't resolve " + signedAtom);
            atoms.add(signedAtom);
            triedOnce.add(signedAtom);
          }
        }
      });
      ++atomIndex;
    }
  }


}
