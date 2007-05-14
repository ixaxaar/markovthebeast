package thebeast.pml.solve.weightedsat;

import thebeast.util.HashMultiMap;

import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class MaxWalkSat implements WeightedSatSolver {

  private static Random random = new Random(1);
  private Clause[] clauses = new Clause[0];
  private Atom[] atoms = new Atom[0];
  private int atomCount;
  private int clauseCount;
  private boolean[] best;
  private double bestScore;
  private double currentScore;
  private double greedy = 0.5;


  class Atom {
    final ArrayList<NodeClauseRelation> clauses = new ArrayList<NodeClauseRelation>();
    boolean state;
    double cost;

    public Atom(boolean state, double cost) {
      this.state = state;
      this.cost = cost;
    }
  }

  static class DeltaScoredAtom {
    Atom atom;
    double delta;

    public DeltaScoredAtom(Atom atom, double delta) {
      this.atom = atom;
      this.delta = delta;
    }
  }

  class NodeClauseRelation {
    Clause clause;
    Atom atom;
    int[] containingDisjunctions;
    int[] positions;
    private boolean changedClauseState;


    public NodeClauseRelation(Clause clause, Atom atom, int[] containingDisjunctions, int[] positions) {
      this.clause = clause;
      this.atom = atom;
      this.containingDisjunctions = containingDisjunctions;
      this.positions = positions;
    }
  }

  class Clause {
    NodeClauseRelation[] nodes;
    boolean[][] signs;
    int[] trueLiteralCounts;
    int trueDisjunctionCount;
    double cost;
    boolean state;

    public String toString(){
      Formatter formatter = new Formatter();
      formatter.format("%6s %2d %6.2f %s",state,trueDisjunctionCount, cost, Arrays.toString(trueLiteralCounts));
      return formatter.toString();
    }

    Clause(WeightedSatClause clause) {
      signs = clause.signs;
      cost = clause.score;
      trueLiteralCounts = new int[signs.length];
      HashMultiMap<Atom, Integer> atom2positions = new HashMultiMap<Atom, Integer>();
      HashMultiMap<Atom, Integer> atom2disjunctions = new HashMultiMap<Atom, Integer>();
      for (int disjunction = 0; disjunction < clause.atoms.length; ++disjunction) {
        for (int position = 0; position < clause.atoms[disjunction].length; ++position) {
          MaxWalkSat.Atom atom = atoms[clause.atoms[disjunction][position]];
          atom2positions.add(atom, position);
          atom2disjunctions.add(atom, disjunction);
        }
      }
      nodes = new NodeClauseRelation[atom2positions.size()];
      int node = 0;
      for (Atom atom : atom2positions.keySet()) {
        List<Integer> disjunctions = atom2disjunctions.get(atom);
        List<Integer> positionList = atom2positions.get(atom);
        int[] containingDisjunctions = new int[disjunctions.size()];
        int[] positions = new int[disjunctions.size()];
        int i = 0;
        for (Integer disjunction : disjunctions) containingDisjunctions[i++] = disjunction;
        i = 0;
        for (Integer position : positionList) positions[i++] = position;
        NodeClauseRelation rel = new NodeClauseRelation(this, atom, containingDisjunctions, positions);
        nodes[node++] = rel;
        atom.clauses.add(rel);
      }
    }

  }

  private static double calculateDeltaCost(Atom atom) {
    double delta = atom.state ? -atom.cost : atom.cost;
    main:
    for (NodeClauseRelation rel : atom.clauses) {
      rel.changedClauseState = false;
      if (rel.clause.state) {
        //a true clause means all disjunctions are true
        //the clause will change its state if we can find at least one disjunction
        //which will be false if we flip the atom
        for (int i = 0; i < rel.containingDisjunctions.length; ++i) {
          int disjunction = rel.containingDisjunctions[i];
          if ((atom.state == rel.clause.signs[disjunction][rel.positions[i]])
                  && rel.clause.trueLiteralCounts[disjunction] == 1) {
            delta -= rel.clause.cost;
            rel.changedClauseState = true;
            continue main;
          }
        }
      } else {
        //a false clause means there is at least one disjunction which is currently false
        int madeTrue = 0;
        for (int i = 0; i < rel.containingDisjunctions.length; ++i) {
          int disjunction = rel.containingDisjunctions[i];
          //check if we have made the disjunction false -> we won't make the clause true then either
          if ((atom.state == rel.clause.signs[disjunction][rel.positions[i]])
                  && rel.clause.trueLiteralCounts[disjunction] == 1)
            continue main;
          //check if we have made the disjunction true
          if ((atom.state != rel.clause.signs[disjunction][rel.positions[i]])
                  && rel.clause.trueLiteralCounts[disjunction] == 0)
            ++madeTrue;

        }
        if (madeTrue + rel.clause.trueDisjunctionCount == rel.clause.signs.length) {
          delta += rel.clause.cost;
          rel.changedClauseState = true;
        }
      }
    }

    return delta;
  }

  private static DeltaScoredAtom findHighestDeltaNode(Clause clause) {
    double maxDelta = Double.NEGATIVE_INFINITY;
    Atom result = null;
    for (NodeClauseRelation rel : clause.nodes) {
      double delta = calculateDeltaCost(rel.atom);
      if (delta > maxDelta) {
        maxDelta = delta;
        result = rel.atom;
      }
    }
    return new DeltaScoredAtom(result, maxDelta);
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
    for (NodeClauseRelation rel : atom.clauses) {
      if (rel.changedClauseState) rel.clause.state = !rel.clause.state;
      for (int i = 0; i < rel.containingDisjunctions.length; ++i) {
        int disjunction = rel.containingDisjunctions[i];
        int oldCount = rel.clause.trueLiteralCounts[disjunction];
        int newCount = oldCount + (atom.state == rel.clause.signs[disjunction][rel.positions[i]] ? 1 : -1);
        rel.clause.trueLiteralCounts[disjunction] = newCount;
        if (oldCount == 1 && newCount ==0) --rel.clause.trueDisjunctionCount;
        else if (oldCount == 0 && newCount == 1) ++rel.clause.trueDisjunctionCount;
      }
    }
  }

  public void init() {
    atomCount = 0;
    clauseCount = 0;
  }

  public void addAtoms(boolean states[], double[] scores) {
    if (atoms.length < atomCount + states.length) {
      Atom[] newAtoms = new Atom[atomCount + states.length];
      System.arraycopy(atoms, 0, newAtoms, 0, atoms.length);
      atoms = newAtoms;
      best = new boolean[atomCount + states.length];
    }
    for (int i = 0; i < states.length; ++i) {
      Atom atom = atoms[atomCount];
      if (atom == null) {
        atom = new Atom(states[i], scores[i]);
        atoms[atomCount] = atom;
      } else {
        atom.clauses.clear();
        atom.cost = scores[i];
        atom.state = states[i];
      }
      ++atomCount;
    }
  }

  public void addClauses(WeightedSatClause ... clausesToAdd) {
    if (clauses.length < clauseCount + clausesToAdd.length) {
      Clause[] newClauses = new Clause[clauseCount + clausesToAdd.length];
      System.arraycopy(clauses, 0, newClauses, 0, clauseCount);
      clauses = newClauses;
    }
    for (int i = 0; i < clausesToAdd.length; ++i) {
      clauses[clauseCount] = new Clause(clausesToAdd[i]);
      ++clauseCount;
    }

  }

  public boolean[] solve() {
    syncClauses();
    int maxFlips = 100;
    double target = 100;
    double score = 0;
    bestScore = 0;
    fill(atoms, best);
    for (int flip = 0; flip < maxFlips && score < target; ++flip) {
      MaxWalkSat.Clause clause = pickRandomClause(clauses);
      double uniform = random.nextDouble();
      Atom atom;
      double delta;
      if (uniform > this.greedy) {
        atom = pickRandomNode(clause);
        delta = calculateDeltaCost(atom);
      } else {
        DeltaScoredAtom deltaScoredAtom = findHighestDeltaNode(clause);
        atom = deltaScoredAtom.atom;
        delta = deltaScoredAtom.delta;
      }
      flipNode(atom);
      score += delta;
      if (score > bestScore) {
        fill(atoms, best);
        bestScore = score;
      }
      //printState(uniform > this.greedy, score,atoms, atomCount);
      //for (int i = 0; i < clauseCount; ++i) System.out.println(clauses[i]);
    }
    return best;
  }

  private static void printState(boolean uniform, double score, Atom[] atoms, int atomCount){
    System.out.printf("%-8s%10f ",uniform ? "uniform" : "greedy" ,score);
    for (int i = 0; i < atomCount; ++i)
      System.out.printf("%-6s",atoms[i].state);
    System.out.println();
  }

  private static void fill(Atom[] atoms, boolean[] best) {
    for (int i = 0; i < best.length; ++i) {
      best[i] = atoms[i].state;
    }
  }

  private void randomizeNodeStates() {
    for (Atom atom : atoms) {
      double uniform = random.nextDouble();
      atom.state = uniform > 0.5;
    }
  }

  private void syncClauses() {
    for (Clause clause : clauses) {
      for (int i = 0; i < clause.signs.length; ++i)
        clause.trueLiteralCounts[i] = 0;
      for (NodeClauseRelation rel : clause.nodes) {
        for (int i = 0; i < rel.containingDisjunctions.length; ++i) {
          int disjunction = rel.containingDisjunctions[i];
          if (clause.signs[disjunction][rel.positions[i]] == rel.atom.state)
            clause.trueLiteralCounts[disjunction]++;
        }
      }
      clause.state = true;
      clause.trueDisjunctionCount = 0;
      for (int i = 0; i < clause.signs.length; ++i)
        if (clause.trueLiteralCounts[i] == 0) {
          clause.state = false;
        } else {
          ++clause.trueDisjunctionCount;
        }
    }
  }

  public boolean[] getSolution() {
    boolean[] result = new boolean[atoms.length];
    for (int i = 0; i < result.length; ++i)
      result[i] = atoms[i].state;
    return result;
  }


}
