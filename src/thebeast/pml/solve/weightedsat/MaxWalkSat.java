package thebeast.pml.solve.weightedsat;

import thebeast.pml.PropertyName;
import thebeast.util.HashMultiMap;
import thebeast.util.TreeProfiler;

import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class MaxWalkSat implements WeightedSatSolver {

  private Random random = new Random();
  private Clause[] clauses = new Clause[0];
  private Clause[] unsatisfiedClauses = new Clause[0];
  private Clause[] newUnsatisfiedClauses = new Clause[0];
  private Clause[] buffer = new Clause[0];
  private Atom[] atoms = new Atom[0];
  private int[] freeSlots;
  private int freeSlotCount;
  private int atomCount;
  private int clauseCount;
  private int unsatisfiedClauseCount;
  private int newUnsatisfiedClauseCount;
  private boolean[] best;
  private double bestScore;
  private double greedy = 0.5;
  private int maxFlips = 1000;
  private int maxRestarts = 1;
  private boolean
          initRandom = true,
          updateRandom = false;
  private double target = Double.POSITIVE_INFINITY;
  private boolean pickFromUnsatisfied = true;
  private long timeOut = Long.MAX_VALUE;
  private int calls;
  private DeltaScoredAtom deltaScoredAtom = new DeltaScoredAtom(null, 0);

  public void setProperty(PropertyName name, Object value) {
    if ("maxFlips".equals(name.getHead()))
      setMaxFlips((Integer) value);
    else if ("seed".equals(name.getHead()))
      setSeed((Integer) value);
    else if ("initRandom".equals(name.getHead()))
      setInitRandom((Boolean) value);
    else if ("updateRandom".equals(name.getHead()))
      setUpdateRandom((Boolean) value);
    else if ("maxRestarts".equals(name.getHead()))
      setMaxRestarts((Integer) value);
    else if ("timeout".equals(name.getHead()))
      setTimeOut((Integer) value);
    else if ("greedyProb".equals(name.getHead()))
      setGreedyProbability((Double) value);
    else if ("pickFromUnsatisfied".equals(name.getHead()))
      setPickFromUnsatisfied((Boolean) value);
  }


  public boolean isUpdateRandom() {
    return updateRandom;
  }

  public void setUpdateRandom(boolean updateRandom) {
    this.updateRandom = updateRandom;
  }

  public Object getProperty(PropertyName name) {
    return null;
  }


  public long getTimeOut() {
    return timeOut;
  }

  public void setTimeOut(long timeOut) {
    this.timeOut = timeOut;
  }

  class Atom {
    final ArrayList<NodeClauseRelation> clauses = new ArrayList<NodeClauseRelation>();
    boolean state;
    int index;

    public Atom(boolean state, int index) {
      this.state = state;
      this.index = index;
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
    int index;
    int indexInUnsatisfied;

    public String toString() {
      Formatter formatter = new Formatter();
      formatter.format("%6s %2d %6.2f %s", state, trueDisjunctionCount, cost, Arrays.toString(trueLiteralCounts));
      for (int disjunction = 0; disjunction < signs.length; ++disjunction) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (NodeClauseRelation rel : nodes) {
          for (int i = 0; i < rel.containingDisjunctions.length; ++i) {
            if (rel.containingDisjunctions[i] == disjunction) {
              map.put(rel.positions[i], rel.atom.index);
              break;
            }
          }
        }
        for (int atom = 0; atom < signs[disjunction].length; ++atom) {
          formatter.format("%2s", signs[disjunction][atom] ? "" : "!");
          formatter.format("%-1d", map.get(atom));
        }

      }

      return formatter.toString();
    }

    Clause(int atomIndex, double cost) {
      signs = new boolean[][]{{!pickFromUnsatisfied || cost > 0}};
      if (pickFromUnsatisfied && cost < 0)
        cost = -cost;
      trueLiteralCounts = new int[signs.length];
      this.cost = cost;
      MaxWalkSat.Atom atom = atoms[atomIndex];
      NodeClauseRelation rel = new NodeClauseRelation(this, atom, new int[]{0}, new int[]{0});
      nodes = new NodeClauseRelation[]{rel};
      atom.clauses.add(rel);
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
      ArrayList<Atom> sorted = new ArrayList<Atom>(atom2positions.keySet());
      Collections.sort(sorted, new Comparator<Atom>() {
        public int compare(Atom o1, Atom o2) {
          return o1.index - o2.index;
        }
      });
      for (Atom atom : sorted) {
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


  public boolean isInitRandom() {
    return initRandom;
  }

  public void setInitRandom(boolean initRandom) {
    this.initRandom = initRandom;
  }


  public int getMaxRestarts() {
    return maxRestarts;
  }

  public void setMaxRestarts(int maxRestarts) {
    this.maxRestarts = maxRestarts;
  }


  private static double calculateDeltaCost(Atom atom) {
    double delta = 0;
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


  private static void findHighestDeltaNode(Clause clause, DeltaScoredAtom deltaScoredAtom) {

    double maxDelta = Double.NEGATIVE_INFINITY;
    Atom result = null;
    for (NodeClauseRelation rel : clause.nodes) {
      double delta = calculateDeltaCost(rel.atom);
//      if (clause.signs[0].length == 3){
      //System.out.printf("%-3d%-6s%-5f ", rel.atom.index, rel.atom.state, delta);
//      }
      if (delta > maxDelta) {
        maxDelta = delta;
        result = rel.atom;
      }
    }
    //System.out.println("");
    deltaScoredAtom.atom = result;
    deltaScoredAtom.delta = maxDelta;
  }

  private static Atom pickRandomNode(Random random, Clause clause) {
    int number = Math.abs(random.nextInt()) % clause.nodes.length;
    return clause.nodes[number].atom;
  }

  private static MaxWalkSat.Clause pickRandomClause(Random random, Clause[] clauses, int clauseCount) {
    int nextInt = random.nextInt();
    int number = Math.abs(nextInt) % clauseCount;
    //System.out.println("nextInt = " + nextInt);
    //System.out.println("clauseCount = " + clauseCount);
    //System.out.println("Clause " + number);
    return clauses[number];
  }


  private void updateUnsatisfiedClauses() {
    unsatisfiedClauseCount = 0;
    for (int i = 0; i < clauseCount; ++i) {
      if (!clauses[i].state) {
        clauses[i].indexInUnsatisfied = unsatisfiedClauseCount;
        unsatisfiedClauses[unsatisfiedClauseCount++] = clauses[i];
      }
    }
    newUnsatisfiedClauseCount = 0;
  }

  private void updateUnsatisfiedClausesIncrementally() {
    int index = 0;
    for (int i = 0; i < unsatisfiedClauseCount; ++i) {
      if (!unsatisfiedClauses[i].state)
        buffer[index++] = unsatisfiedClauses[i];
    }
    for (int i = 0; i < newUnsatisfiedClauseCount; ++i) {
      buffer[index++] = newUnsatisfiedClauses[i];
    }
    Clause[] tmp = unsatisfiedClauses;
    unsatisfiedClauses = buffer;
    buffer = tmp;

    unsatisfiedClauseCount = index;
    newUnsatisfiedClauseCount = 0;

  }


  private void flipNode(Atom atom) {
    newUnsatisfiedClauseCount = 0;
    atom.state = !atom.state;
    freeSlotCount = 0;
    for (NodeClauseRelation rel : atom.clauses) {
      if (rel.changedClauseState) {
        rel.clause.state = !rel.clause.state;
        if (!rel.clause.state)
          newUnsatisfiedClauses[newUnsatisfiedClauseCount++] = rel.clause;
        else
          freeSlots[freeSlotCount++] = rel.clause.indexInUnsatisfied;
      }
      for (int i = 0; i < rel.containingDisjunctions.length; ++i) {
        int disjunction = rel.containingDisjunctions[i];
        int oldCount = rel.clause.trueLiteralCounts[disjunction];
        int newCount = oldCount + (atom.state == rel.clause.signs[disjunction][rel.positions[i]] ? 1 : -1);
        rel.clause.trueLiteralCounts[disjunction] = newCount;
        if (oldCount == 1 && newCount == 0) --rel.clause.trueDisjunctionCount;
        else if (oldCount == 0 && newCount == 1) ++rel.clause.trueDisjunctionCount;
      }
    }
    //fill free slots with new unsatisfied clauses
    int freeSlot = 0;
    for (int i = 0; i < newUnsatisfiedClauseCount; ++i) {
      MaxWalkSat.Clause unsatisfiedClause = newUnsatisfiedClauses[i];
      if (freeSlot < freeSlotCount) {
        unsatisfiedClauses[freeSlots[freeSlot]] = unsatisfiedClause;
        unsatisfiedClause.indexInUnsatisfied = freeSlots[freeSlot];
        ++freeSlot;
      } else {
        unsatisfiedClauses[unsatisfiedClauseCount] = unsatisfiedClause;
        unsatisfiedClause.indexInUnsatisfied = unsatisfiedClauseCount;
        ++unsatisfiedClauseCount;
      }
    }
    //remove free slots at the end
    while (unsatisfiedClauseCount > 0 && unsatisfiedClauses[unsatisfiedClauseCount - 1].state)
      --unsatisfiedClauseCount;
    //swap
    if (unsatisfiedClauseCount == 0) return;
    for (; freeSlot < freeSlotCount; ++freeSlot) {
      int slot = freeSlots[freeSlot];
      if (slot >= unsatisfiedClauseCount) continue;
      if (slot != unsatisfiedClauseCount - 1) {
        //swap
        Clause last = unsatisfiedClauses[unsatisfiedClauseCount - 1];
        last.indexInUnsatisfied = slot;
        unsatisfiedClauses[slot] = last;
      }
      --unsatisfiedClauseCount;
      //remove free slots at the end
      while (unsatisfiedClauseCount > 0 && unsatisfiedClauses[unsatisfiedClauseCount - 1].state)
        --unsatisfiedClauseCount;
      if (unsatisfiedClauseCount == 0) return;
    }

  }

  public void init() {
    atomCount = 0;
    clauseCount = 0;
    calls = 0;
  }

  public void addAtoms(double[] scores) {
    increaseAtomCapacity(scores.length);
    increaseClauseCapacity(scores.length);
    for (int i = 0; i < scores.length; ++i) {
      Atom atom = atoms[atomCount];
      if (atom == null) {
        atom = new Atom(false, atomCount);
        atoms[atomCount] = atom;
      } else {
        atom.clauses.clear();
        atom.state = false;
      }
      best[atomCount] = false;
      if (scores[i] != 0.0)
        clauses[clauseCount++] = new Clause(atomCount, scores[i]);
      ++atomCount;
    }
  }

  private void ensureAtomCapacity(int capacity) {
    if (atoms.length < capacity) increaseAtomCapacity(capacity - atoms.length);
  }

  private void increaseAtomCapacity(int howmuch) {
    if (atoms.length < atomCount + howmuch) {
      Atom[] newAtoms = new Atom[atomCount + howmuch];
      System.arraycopy(atoms, 0, newAtoms, 0, atoms.length);
      atoms = newAtoms;
      boolean[] newBest = new boolean[atomCount + howmuch];
      if (best != null) System.arraycopy(best, 0, newBest, 0, best.length);
      best = newBest;
    }
  }

  public void addClauses(WeightedSatClause... clausesToAdd) {
    increaseClauseCapacity(clausesToAdd.length);
    for (WeightedSatClause aClausesToAdd : clausesToAdd) {
      Clause clause = normalize(aClausesToAdd);
      if (clause == null) continue;
      clauses[clauseCount] = clause;
      clause.index = clauseCount;
      ++clauseCount;
    }

  }

  private void ensureClauseCapacity(int capacity) {
    if (clauses.length < capacity) increaseClauseCapacity(capacity - clauses.length);
  }

  private void increaseClauseCapacity(int howmuch) {
    if (clauses.length < clauseCount + howmuch) {
      Clause[] newClauses = new Clause[clauseCount + howmuch];
      System.arraycopy(clauses, 0, newClauses, 0, clauseCount);
      clauses = newClauses;
      unsatisfiedClauses = new Clause[clauseCount + howmuch];
      newUnsatisfiedClauses = new Clause[clauseCount + howmuch];
      buffer = new Clause[clauseCount + howmuch];
      freeSlots = new int[clauseCount + howmuch];
    }
  }


  public Clause normalize(WeightedSatClause clause) {
    int newDisjunctionCount = 0;
    boolean[][] newDisjunctionSigns = new boolean[clause.signs.length][];
    int[][] newDisjunctionAtoms = new int[clause.signs.length][];

    for (int disjunction = 0; disjunction < clause.signs.length; ++disjunction) {
      int[] atoms = clause.atoms[disjunction];
      boolean[] signs = clause.signs[disjunction];
      int length = atoms.length;
      boolean[] toSkip = new boolean[length];
      int toSkipCount = 0;
      boolean alwaysTrue = false;
      outer:
      for (int i = 0; i < length; ++i)
        for (int j = i + 1; j < length; ++j) {
          if (atoms[i] == atoms[j]) {
            if (signs[i] == signs[j]) {
              ++toSkipCount;
              toSkip[i] = true;
              continue outer;
            } else {
              alwaysTrue = true;
              break outer;
            }
          }
        }
      if (alwaysTrue) continue;
      int newLength = length - toSkipCount;
      int[] newAtoms = new int[newLength];
      boolean[] newSigns = new boolean[newLength];
      int pointer = 0;
      for (int i = 0; i < length; ++i) {
        if (!toSkip[i]) {
          newAtoms[pointer] = atoms[i];
          newSigns[pointer] = signs[i];
          ++pointer;
        }
      }
      newDisjunctionAtoms[newDisjunctionCount] = newAtoms;
      newDisjunctionSigns[newDisjunctionCount] = newSigns;
      ++newDisjunctionCount;

    }
    if (newDisjunctionCount == 0) return null;
    int[][] newAtoms = new int[newDisjunctionCount][];
    boolean[][] newSigns = new boolean[newDisjunctionCount][];
    System.arraycopy(newDisjunctionAtoms, 0, newAtoms, 0, newDisjunctionCount);
    System.arraycopy(newDisjunctionSigns, 0, newSigns, 0, newDisjunctionCount);
    return new Clause(new WeightedSatClause(clause.score, newAtoms, newSigns));
  }

  public int getMaxFlips() {
    return maxFlips;
  }

  public void setMaxFlips(int maxFlips) {
    this.maxFlips = maxFlips;
  }

  public void setSeed(long seed) {
    random = new Random(seed);
    //System.out.println("Seed: " + seed);
  }


  public double getTarget() {
    return target;
  }

  public void setTarget(double target) {
    this.target = target;
  }

  public boolean[] solve() {
    //System.out.println("maxFlips = " + maxFlips);
    //System.out.println(random.);
    //random.setSeed(0);
    TreeProfiler profiler = new TreeProfiler();
    bestScore = Double.NEGATIVE_INFINITY;
    long time = System.currentTimeMillis();
    ++calls;
    //System.out.println("Solving...");
    profiler.start("solve");
    for (int run = 0; run < maxRestarts && bestScore < target && System.currentTimeMillis() - time < timeOut; ++run) {
      if (calls == 1 && initRandom || calls > 1 && updateRandom)
        randomizeNodeStates();
      else
        useBestStateAsInit();
      syncClauses();
      updateUnsatisfiedClauses();
//      System.out.println(unsatisfiedClauseCount);
//      for (int i = 0; i < unsatisfiedClauseCount; ++i)
//        System.out.println(unsatisfiedClauses[i]);
      //for (int i = 0; i < clauseCount; ++i)
      //  System.out.println(clauses[i]);

      double score = getScore();
      if (score > bestScore) {
        fill(atoms, best);
        bestScore = score;
        //System.out.println(bestScore);
      }
      //System.out.println("clauseCount = " + clauseCount);
      //System.out.println("score = " + score);
      //System.out.println("Flipping..");
      for (int flip = 0; flip < maxFlips && bestScore < target && System.currentTimeMillis() - time < timeOut; ++flip) {
        MaxWalkSat.Clause clause;
        //profiler.start("pick-clause");
        if (pickFromUnsatisfied) {
          clause = pickRandomClause(random, unsatisfiedClauses, unsatisfiedClauseCount);
        } else
          clause = pickRandomClause(random, clauses, clauseCount);
        //System.out.println(clause.index);
        double uniform = random.nextDouble();
        Atom atom;
        double delta;
        //profiler.end().start("pick-node");
        if (uniform > this.greedy) {
          atom = pickRandomNode(random, clause);
          delta = calculateDeltaCost(atom);
        } else {
          findHighestDeltaNode(clause, deltaScoredAtom);
          atom = deltaScoredAtom.atom;
          delta = deltaScoredAtom.delta;
        }
        ///System.out.println("Changed: " + atom.index);

        //profiler.end().start("flip-node");
        flipNode(atom);
        //System.out.println("unsatisfiedClauseCount = " + unsatisfiedClauseCount);

        score += delta;
        //profiler.end().start("update-best");
        if (score > bestScore) {
          fill(atoms, best);
          bestScore = score;
          //System.out.println(bestScore);
//          for (int i = 0; i < unsatisfiedClauseCount; ++i) {
//            System.out.print(i == clause.index ? ">" : " ");
//            System.out.println(unsatisfiedClauses[i]);
//          }
          //System.out.println("unsatisfiedClauseCount = " + unsatisfiedClauseCount);
          //System.out.println("calculateCostOfUnsatistfied() = " + calculateCostOfUnsatistfied());
          //System.out.println("sum = " + (score + calculateCostOfUnsatistfied()));
        }
        //profiler.end();
//        profiler.end().start("update-clauses");
        //updateUnsatisfiedClausesIncrementally();
//        profiler.end();
//        printState(uniform > this.greedy, score,atoms, atomCount);
//        for (int i = 0; i < clauseCount; ++i) {
//          System.out.print(i == clause.index ? ">": " ");
//          System.out.println(clauses[i]);
//        }
//        System.out.println("unsatisfiedClauseCount = " + unsatisfiedClauseCount);
        if (unsatisfiedClauseCount == 0) {
//          System.out.println("no unsatisfied!");
          profiler.end();
          return best;
        }

        //System.out.println(score);
      }

      //System.out.println("best =  " + bestScore);
    }
    profiler.end();
    //System.out.println(profiler);

//        for (int i = 0; i < clauseCount; ++i) {
//          System.out.print(i == clause.index ? ">": " ");
//          System.out.println(clauses[i]);
//        }

    //System.out.println(bestScore);
    //System.out.println(Arrays.toString(best));
    System.out.print(".");
    return best;
  }

  public void setStates(boolean[] states) {
    System.arraycopy(states,0,best,0,states.length);
  }

  public double getBestScore() {
    return bestScore;
  }

  private static void printState(boolean uniform, double score, Atom[] atoms, int atomCount) {
    System.out.printf("%-8s%10f ", uniform ? "uniform" : "greedy", score);
    for (int i = 0; i < atomCount; ++i)
      System.out.printf("%-3s", atoms[i].state ? "1" : "0");
    System.out.println();
  }

  private static void fill(Atom[] atoms, boolean[] best) {
    for (int i = 0; i < best.length; ++i) {
      best[i] = atoms[i].state;
    }
  }

  private double calculateCostOfUnsatistfied(){
    double result = 0;
    for (int i = 0; i < unsatisfiedClauseCount; ++i)
      result += unsatisfiedClauses[i].cost;
    return result;
  }

  private void randomizeNodeStates() {
    for (Atom atom : atoms) {
      double uniform = random.nextDouble();
      atom.state = uniform > 0.5;
    }
  }

  private void useBestStateAsInit() {
    for (int i = 0; i < atomCount; ++i)
      atoms[i].state = best[i];
  }

  private void syncClauses() {
    for (int clauseNr = 0; clauseNr < clauseCount; ++clauseNr) {
      Clause clause = clauses[clauseNr];
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


  public boolean isPickFromUnsatisfied() {
    return pickFromUnsatisfied;
  }

  public void setPickFromUnsatisfied(boolean pickFromUnsatisfied) {
    this.pickFromUnsatisfied = pickFromUnsatisfied;
  }

  public double getScore() {
    double sum = 0;
    for (int i = 0; i < clauseCount; ++i)
      if (clauses[i].state) sum += clauses[i].cost;
    return sum;
  }

  public double getNormalizedScore() {
    double sum = 0;
    for (int i = 0; i < clauseCount; ++i)
      if (clauses[i].signs.length == 1 && clauses[i].signs[0].length == 1)
        sum += clauses[i].signs[0][0] ? clauses[i].cost : -clauses[i].cost;
      else if (!clauses[i].state) sum -= clauses[i].cost;
    return sum;
  }


  public void setGreedyProbability(double greedy) {
    this.greedy = greedy;
  }
}
