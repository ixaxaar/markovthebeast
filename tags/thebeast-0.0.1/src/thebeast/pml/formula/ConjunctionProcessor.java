package thebeast.pml.formula;

import thebeast.nod.expression.*;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.pml.*;
import thebeast.pml.predicate.*;
import thebeast.pml.predicate.IntGEQ;
import thebeast.pml.predicate.IntLEQ;
import thebeast.pml.predicate.DoubleLEQ;
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
  private GroundAtoms groundAtoms, closure;
  private SignedAtom hidden;
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
    public LinkedList<SignedAtom> remainingAtoms = new LinkedList<SignedAtom>();

    public Context() {
    }

    public Context(Map<Variable, Term> var2term) {
      this.var2term.putAll(var2term);
    }


  }

  public ConjunctionProcessor(Weights weights, GroundAtoms groundAtoms) {
    this.weights = weights;
    this.groundAtoms = groundAtoms;
    this.builder = new FormulaBuilder(groundAtoms.getSignature());
    factory = TheBeast.getInstance().getNodServer().expressionFactory();

  }

  public ConjunctionProcessor(Weights weights, GroundAtoms groundAtoms, GroundAtoms closure, SignedAtom hidden) {
    this.weights = weights;
    this.groundAtoms = groundAtoms;
    this.builder = new FormulaBuilder(weights.getSignature());
    factory = TheBeast.getInstance().getNodServer().expressionFactory();
    this.hidden = hidden;
    this.closure = closure;
  }


  public void resolveBruteForce(final Context context, Atom atom) {
    ArrayList<SignedAtom> atoms = new ArrayList<SignedAtom>(1);
    atoms.add(new SignedAtom(true, atom));
    resolveBruteForce(context, atoms);
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
          throw new RuntimeException("Unresolved numeric variable " + var + "! Will blow up search space " +
                  "for conjunction " + conjunction);
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

  public void processWithClosure(final Context context, GroundAtoms closure, SignedAtom atom) {
    if (!atom.isTrue()) throw new RuntimeException("Can't process negated atoms here");
    PredicateAtom predicateAtom = (PredicateAtom) atom.getAtom();
    UserPredicate userPredicate = (UserPredicate) predicateAtom.getPredicate();
    int argIndex = 0;
    String prefix = predicateAtom.getPredicate().getName() + "_closure";
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
        context.conditions.add((BoolExpression) exprGenerator.convertFormula(
                builder.getFormula(), groundAtoms, weights, context.var2expr, context.var2term));
      } else if (termResolver.getUnresolved().size() == 1) {
        Variable toResolve = termResolver.getUnresolved().get(0);
        Term inverted = inverter.invert(resolved, artificial, toResolve);
        context.var2term.put(toResolve, inverted);
      } else {
        throw new RuntimeException("Seems like we really can't resolve " + atom);
      }
      ++argIndex;
    }
    context.prefixes.add(prefix);
    context.relations.add(closure.getGroundAtomsOf(userPredicate).getRelationVariable());

  }

  public void processConjunction(final Context context, List<SignedAtom> conjunction) {
    processConjunction(context, conjunction, true);
  }

  private enum ResolveState {
    RESOLVED, UNRESOLVED, TRIED_ALREADY
  }

  ;

  public void processConjunction(final Context context, List<SignedAtom> conjunction, final boolean throwException) {
    final LinkedList<SignedAtom> atoms = new LinkedList<SignedAtom>(conjunction);
    int atomIndex = 0;
    final HashSet<SignedAtom> triedOnce = new HashSet<SignedAtom>();

    while (atoms.size() > 0) {
      final SignedAtom signedAtom = atoms.remove(0);
      final Atom atom = signedAtom.getAtom();
      final boolean sign = signedAtom.isTrue();
      final int index = atomIndex;
      atom.acceptAtomVisitor(new AbstractAtomVisitor() {

        public void visitUndefinedWeight(UndefinedWeight undefinedWeight) {
          if (!sign) throw new RuntimeException("Can't do negated undefined(...)");
          BooleanFormula resolved = formulaResolver.resolve(undefinedWeight, context.var2term);
          if (resolved == null) {
            if (triedOnce.contains(signedAtom)) {
              //context.remainingHiddenArgs
              if (throwException)
                throw new RuntimeException("Seems like we really can't resolve " + signedAtom);
              context.remainingAtoms.add(signedAtom);
              return;
            }
            atoms.add(signedAtom);
            triedOnce.add(signedAtom);

          } else {
            context.conditions.add((BoolExpression) exprGenerator.convertFormula(
                    resolved, groundAtoms, weights, context.var2expr, context.var2term));

          }
        }

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
                  ResolveState resolveState;
                  resolveState = tryResolve(userPredicate, signedAtom, argIndex, arg, localConditions,
                          prefix, context, triedOnce, throwException, atoms);
                  if (resolveState == ResolveState.TRIED_ALREADY) break main;
                  if (resolveState == ResolveState.UNRESOLVED) break;
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
                    if (triedOnce.contains(signedAtom)) {
                      //context.remainingHiddenArgs
                      if (throwException)
                        throw new RuntimeException("Seems like we really can't resolve " + signedAtom);
                      context.remainingAtoms.add(signedAtom);
                      break main;

                    }
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
              if (predicate.getName().equals("equals")) {

                Term lhs = predicateAtom.getArguments().get(0);
                Term rhs = predicateAtom.getArguments().get(1);
                Term lhsResolved = termResolver.resolve(lhs, context.var2term);
                int lhsUnsresolved = termResolver.getUnresolved().size();
                Term rhsResolved = termResolver.resolve(rhs, context.var2term);
                int rhsUnsresolved = termResolver.getUnresolved().size();
                if (lhs instanceof Variable && lhsUnsresolved == 1 && rhsUnsresolved == 0) {
                  context.var2term.put((Variable) lhs, rhsResolved);
                  context.var2expr.put((Variable) lhs,
                          exprGenerator.convertTerm(rhsResolved, groundAtoms, weights, context.var2expr, context.var2term));
                  return;
                } else if (rhs instanceof Variable && rhsUnsresolved == 1 && lhsUnsresolved == 0) {
                  context.var2term.put((Variable) rhs, lhsResolved);
                  context.var2expr.put((Variable) rhs,
                          exprGenerator.convertTerm(lhsResolved, groundAtoms, weights, context.var2expr, context.var2term));
                  return;
                }

              }
              BooleanFormula resolved = formulaResolver.resolve(atom, context.var2term);
              if (resolved == null) {
                if (triedOnce.contains(signedAtom)) {
                  //context.remainingHiddenArgs
                  if (throwException) throw new RuntimeException("Seems like we really can't resolve " + signedAtom);
                  context.remainingAtoms.add(signedAtom);
                  return;

                }
                atoms.add(signedAtom);
                triedOnce.add(signedAtom);
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

            public void visitIntGEQ(IntGEQ intGEQ) {
              visitPredicate(intGEQ);
            }

            public void visitDoubleLEQ(DoubleLEQ doubleLEQ) {
              visitPredicate(doubleLEQ);
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
            if (triedOnce.contains(signedAtom)) {
              //context.remainingHiddenArgs
              if (throwException) throw new RuntimeException("Seems like we really can't resolve " + signedAtom);
              context.remainingAtoms.add(signedAtom);
              return;

            }
            atoms.add(signedAtom);
            triedOnce.add(signedAtom);
          }
        }

        public void visitTrue(True aTrue) {

        }
      });
      ++atomIndex;
    }
  }

  private ResolveState tryResolve(UserPredicate userPredicate, SignedAtom signedAtom, int argIndex, Term arg,
                                  LinkedList<BoolExpression> localConditions, String prefix, Context context,
                                  HashSet<SignedAtom> triedOnce, boolean throwException, LinkedList<SignedAtom> atoms) {
    ResolveState resolveState;
    String varName = prefix + "_" + userPredicate.getColumnName(argIndex);
    Variable artificial = new Variable(arg.getType(), varName);
    context.var2expr.put(artificial, factory.createAttribute(prefix, userPredicate.getAttribute(argIndex)));
    Term resolved = termResolver.resolve(arg, context.var2term);
    if (termResolver.getUnresolved().size() == 0) {
      builder.var(artificial).term(resolved).equality();
      localConditions.add((BoolExpression) exprGenerator.convertFormula(
              builder.getFormula(), groundAtoms, weights, context.var2expr, context.var2term));
      resolveState = ResolveState.RESOLVED;
    } else if (termResolver.getUnresolved().size() == 1) {
      Variable toResolve = termResolver.getUnresolved().get(0);
      Term inverted = inverter.invert(resolved, artificial, toResolve);
      context.var2term.put(toResolve, inverted);
      resolveState = ResolveState.RESOLVED;
    } else {
      if (triedOnce.contains(signedAtom)) {
        //context.remainingHiddenArgs
        if (throwException)
          throw new RuntimeException("Seems like we really can't resolve " + signedAtom);
        context.remainingAtoms.add(signedAtom);
        resolveState = ResolveState.TRIED_ALREADY;
      } else {
        atoms.add(signedAtom);
        triedOnce.add(signedAtom);
        resolveState = ResolveState.UNRESOLVED;
      }
    }
    return resolveState;
  }


}
