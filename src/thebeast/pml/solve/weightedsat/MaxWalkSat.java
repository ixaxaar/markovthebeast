package thebeast.pml.solve.weightedsat;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Sebastian Riedel
 */
public class MaxWalkSat implements WeightedSatSolver {

  private static Random random;
  private Clause[] clauses;
  private Atom[] atoms;
  private double cost;


  class Atom {
    ArrayList<NodeClauseRelation> clauses;
    boolean state;
    double cost;
  }

  class NodeClauseRelation {
    Clause clause;
    Atom atom;
    int[] containingDisjunctions;
    int[] positions;
    private boolean changedClauseState;
  }

  class Clause {
    NodeClauseRelation[] nodes;
    boolean[][] signs;
    boolean[] disjunctionStates;
    int[] trueCount;
    double cost;
    boolean state;

  }

  private static double calculateDeltaCost(Atom atom) {
    double delta = atom.state ? - atom.cost : atom.cost;
    main:
    for (NodeClauseRelation rel : atom.clauses) {
      rel.changedClauseState = false;
      if (rel.clause.state) {
        for (int i = 0; i < rel.containingDisjunctions.length; ++i) {
          int disjunction = rel.containingDisjunctions[i];
          if ((atom.state == rel.clause.signs[disjunction][rel.positions[i]])
                  && rel.clause.trueCount[disjunction] == 1) {
            delta -= rel.clause.cost;
            rel.changedClauseState = true;
            continue main;
          }
        }
      } else {
        for (int i = 0; i < rel.containingDisjunctions.length; ++i) {
          int disjunction = rel.containingDisjunctions[i];
          if (rel.clause.trueCount[disjunction] < rel.clause.signs[disjunction].length - 2 ||
                  atom.state != rel.clause.signs[disjunction][rel.positions[i]])
            continue main;
        }
        delta += rel.clause.cost;
        rel.changedClauseState = true;
      }
    }

    return delta;
  }

  private static Atom findHighestDeltaNode(Clause clause) {
    double maxDelta = Double.NEGATIVE_INFINITY;
    Atom result = null;
    for (NodeClauseRelation rel : clause.nodes) {
      double delta = calculateDeltaCost(rel.atom);
      if (delta > maxDelta) {
        maxDelta = delta;
        result = rel.atom;
      }
    }
    return result;
  }

  private static Atom pickRandomNode(Clause clause) {
    int number = Math.abs(random.nextInt()) % clause.nodes.length;
    return clause.nodes[number].atom;
  }

  private static MaxWalkSat.Clause pickRandomClause(Clause[] clauses) {
    int number = Math.abs(random.nextInt()) % clauses.length;
    return clauses[number];
  }

  private static void flipNode(Atom atom) {
    atom.state = !atom.state;
    for (NodeClauseRelation rel : atom.clauses){
      if (rel.changedClauseState) rel.clause.state = !rel.clause.state;
      for (int i = 0; i < rel.containingDisjunctions.length; ++i) {
        int disjunction = rel.containingDisjunctions[i];
        rel.clause.trueCount[disjunction] +=
                (atom.state == rel.clause.signs[disjunction][rel.positions[i]]) ? 1 : -1;
      }
    }
  }

  public void solve() {
    int maxFlips = 100;
    double target = 100;
    double cost = 0;
    for (int flip = 0; flip < maxFlips && cost < target; ++flip) {
      MaxWalkSat.Clause clause = pickRandomClause(clauses);
      double uniform = random.nextDouble();
      Atom atom = uniform > 0.5 ? pickRandomNode(clause) : findHighestDeltaNode(clause);
      flipNode(atom);
    }
  }


  private void randomizeNodeStates(){
    for (Atom atom : atoms){
      double uniform = random.nextDouble();
      atom.state = uniform > 0.5;
    }
  }

  private void syncClauses(){
    for (Clause clause : clauses){
      for (int i = 0; i < clause.signs.length;++i)
        clause.trueCount[i] = 0;
      for (NodeClauseRelation rel : clause.nodes){
        for (int i = 0; i < rel.containingDisjunctions.length; ++i) {
          int disjunction = rel.containingDisjunctions[i];
          if (clause.signs[disjunction][rel.positions[i]] == rel.atom.state)
            clause.trueCount[disjunction]++;
        }
      }
      clause.state = true;
      for (int i = 0; i < clause.signs.length;++i)
        if (clause.trueCount[i] == 0) {
          clause.state = false;
          break;
        }
    }
  }

  public boolean[] getSolution(){
    boolean[] result = new boolean[atoms.length];
    for (int i = 0; i < result.length; ++i)
      result[i] = atoms[i].state;
    return result;
  }


}
