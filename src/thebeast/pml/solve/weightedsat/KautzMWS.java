package thebeast.pml.solve.weightedsat;

import thebeast.pml.PropertyName;
import thebeast.util.NullProfiler;
import thebeast.util.Profiler;
import thebeast.util.Util;
import thebeast.util.Pair;

import java.io.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;

/**
 * @author Sebastian Riedel
 */
public class KautzMWS implements WeightedSatSolver {

  private int atomCount;
  private int clauseCount;
  private double greedy = 0.5;
  private int maxFlips = 1000;
  private int maxRestarts = 1;
  private double target = Double.POSITIVE_INFINITY;
  private long timeOut = Long.MAX_VALUE;
  private Profiler profiler = new NullProfiler();
  private boolean debug = false;
  private StringBuffer constraints = new StringBuffer();
  private File inputCNF;
  private File initVarsFile;
  private boolean[] initStates;
  private boolean useInitStates = false;
  private double detWeight = 100;

  private HashMap<Pair<Integer, Integer>, int[][]> nBalls = new HashMap<Pair<Integer, Integer>, int[][]>();

  public KautzMWS() {
    inputCNF = new File(System.getProperty("java.io.tmpdir") + "/kautzInput.cnf");
    initVarsFile = new File(System.getProperty("java.io.tmpdir") + "/kautzInitVars.vars");
  }

  private int[][] requestMBallsOutOfN(int m, int n) {
    Pair<Integer, Integer> pair = new Pair<Integer, Integer>(n, m);
    int[][] result = nBalls.get(pair);
    if (result == null) {
      int[] allBalls = new int[n];
      for (int i = 0; i < n; ++i) allBalls[i] = i;
      result = Util.nBalls(m, allBalls);
      nBalls.put(pair, result);
    }
    return result;
  }


  public KautzMWS copy() {
    KautzMWS result = new KautzMWS();
    result.timeOut = timeOut;
    result.maxFlips = maxFlips;
    result.greedy = greedy;
    result.debug = debug;
    return result;
  }

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public void setProperty(PropertyName name, Object value) {
    if ("maxFlips".equals(name.getHead()))
      setMaxFlips((Integer) value);
    else if ("seed".equals(name.getHead()))
      setSeed((Integer) value);
    else if ("debug".equals(name.getHead()))
      debug = (Boolean) value;
    else if ("maxRestarts".equals(name.getHead()))
      setMaxRestarts((Integer) value);
    else if ("timeout".equals(name.getHead()))
      setTimeOut((Integer) value);
    else if ("greedyProb".equals(name.getHead()))
      setGreedyProbability((Double) value);
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


  public int getMaxRestarts() {
    return maxRestarts;
  }

  public void setMaxRestarts(int maxRestarts) {
    this.maxRestarts = maxRestarts;
  }


  public void init() {
    atomCount = 0;
    clauseCount = 0;
    constraints.setLength(0);
  }

  public void addAtoms(double[] scores) {
    for (int i = 0; i < scores.length; ++i) {
      if (scores[i] != 0.0) {
        boolean invert = scores[i] < 0;
        int cost = (int) (invert ? (-scores[i] * 100) : (scores[i] * 100));
        constraints.append(cost).append(" ").append(invert ? -(atomCount + 1) : (atomCount + 1)).
                append(" 0\n");
        ++clauseCount;
      }
      ++atomCount;
    }
  }


  public void addClauses(WeightedSatClause... clausesToAdd) {
    profiler.start("addClauses");

    for (WeightedSatClause clause : clausesToAdd) {
      //processClauseWithOwnExpansion(clause);

      processClauseUsingClauseExpansion(clause);
    }
    profiler.end();

  }

  private void processClauseUsingClauseExpansion(WeightedSatClause clause) {
    if (clause.hasCardinalityConstraints()) {

      WeightedSatClause[] separated = clause.expandCardinalityConstraints().separate();
      //todo: more aggresive increase here?
//        increaseClauseCapacity(clauses.length + separated.length);
      for (WeightedSatClause wsc : separated) {
        for (int disjunction = 0; disjunction < wsc.atoms.length; ++disjunction) {
          boolean invert = wsc.score < 0;
          int cost = (int) (invert ? (-wsc.score * 100) : (wsc.score * 100));
          constraints.append(cost);
          for (int atom = 0; atom < wsc.signs[disjunction].length; ++atom) {
            constraints.append(" ").append(wsc.signs[disjunction][atom] != invert ? "" : "-").
                    append((wsc.atoms[disjunction][atom] + 1));
          }
          constraints.append(" 0\n");
          ++clauseCount;
        }
      }
    } else {
      //first process cardinality constraints

//        increaseClauseCapacity(clauses.length + clausesToAdd.length);
      WeightedSatClause wsc = clause;
      for (int disjunction = 0; disjunction < wsc.atoms.length; ++disjunction) {
        boolean invert = wsc.score < 0;
        int cost = (int) (invert ? (-wsc.score * 100) : (wsc.score * 100));
        constraints.append(cost);
        for (int atom = 0; atom < wsc.signs[disjunction].length; ++atom) {
          constraints.append(" " + (wsc.signs[disjunction][atom] != invert ? "" : "-")
                  + (wsc.atoms[disjunction][atom] + 1));
        }
        constraints.append(" 0\n");
        ++clauseCount;
      }
    }
  }

  private void processClauseWithOwnExpansion(WeightedSatClause clause) {
    boolean invert = clause.score < 0;
    int cost = (int) (invert ? (-clause.score * 100) : (clause.score * 100));
    for (int disjunction = 0; disjunction < clause.atoms.length; ++disjunction) {
      //check if disjunction contains a cardinality constraint
      if (clause.constraints != null
              && disjunction < clause.constraints.length
              && clause.constraints[disjunction] != null) {
        if (clause.constraints[disjunction].length > 1)
          throw new RuntimeException("Sat solver cannot do multiple cardinality constraints in disjunction");
        WeightedSatClause.Constraint constraint = clause.constraints[disjunction][0];
        //GEQ
        if (constraint.lb == 1 &&  constraint.ub == Integer.MAX_VALUE) {
          constraints.append(cost);
          for (int atom = 0; atom < clause.signs[disjunction].length; ++atom) {
            constraints.append(" " + (clause.signs[disjunction][atom] != invert ? "" : "-")
                    + (clause.atoms[disjunction][atom] + 1));
          }
          for (int atomInConstraint = 0; atomInConstraint < constraint.indices.length; ++atomInConstraint) {
            constraints.append(" " + (invert ? "-" : "") + (constraint.indices[atomInConstraint] + 1));
          }
          constraints.append(" 0\n");
          ++clauseCount;
        } else if ((constraint.lb == Integer.MIN_VALUE && constraint.ub < Integer.MAX_VALUE)) {
          int[][] nBalls = requestMBallsOutOfN(constraint.ub + 1, constraint.indices.length);
          int actualCost = cost < detWeight ? cost / nBalls.length : cost;
          for (int[] balls : nBalls) {
            //System.out.println(Arrays.toString(balls));
            //System.out.println(Arrays.toString(clause.atoms[disjunction]));
            constraints.append(actualCost);
            for (int atom = 0; atom < clause.signs[disjunction].length; ++atom) {
              constraints.append(" " + (clause.signs[disjunction][atom] != invert ? "" : "-")
                      + (clause.atoms[disjunction][atom] + 1));
            }
            for (int atomIndex : balls){
              constraints.append(" " + (invert ? "" : "-") + (constraint.indices[atomIndex] + 1));
            }
            constraints.append(" 0\n");
            ++clauseCount;
          }
        } else
          throw new RuntimeException("Sat solver can only do >=1 or <=N constraints");


      } else {
        constraints.append(cost);
        for (int atom = 0; atom < clause.signs[disjunction].length; ++atom) {
          constraints.append(" " + (clause.signs[disjunction][atom] != invert ? "" : "-")
                  + (clause.atoms[disjunction][atom] + 1));
        }
        constraints.append(" 0\n");
        ++clauseCount;
      }
    }
  }


  public int getMaxFlips() {
    return maxFlips;
  }

  public void setMaxFlips(int maxFlips) {
    this.maxFlips = maxFlips;
  }

  public void setSeed(long seed) {
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
    //random.setSeed(0);
//    System.out.println("Solving with Kautz Boy");
//    System.out.println(inputCNF.getPath());
    try {

      if (debug) {
        PrintStream out = new PrintStream(inputCNF);
        out.println("p wcnf " + atomCount + "  " + clauseCount);
        out.println(constraints.toString());
        out.flush();
      }
      if (useInitStates) {
        PrintStream out = new PrintStream(initVarsFile);
        for (int i = 0; i < initStates.length; ++i) {
          if (initStates[i]) out.print((i + 1) + " ");
        }
        out.flush();
      }

      Process p = Runtime.getRuntime().exec("maxwalksat -low -cutoff " + maxFlips + " -tries " + maxRestarts
              + " -noise " + (int) (greedy * 100) + " 100"
              + (useInitStates ? " -init " + initVarsFile.getPath() : ""));
      PrintStream kautzMWSinput = new PrintStream(p.getOutputStream());
      kautzMWSinput.println("p wcnf " + atomCount + "  " + clauseCount);
      kautzMWSinput.println(constraints);
      kautzMWSinput.close();
      profiler.start("solve");

      BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

      BufferedReader stdError = new BufferedReader(new
              InputStreamReader(p.getErrorStream()));
      //for (String line = stdError.readLine(); line != null; line = stdError.readLine())
      //  System.err.println(line);

      HashSet<Integer> bestResult = new HashSet<Integer>();
      int lowestCostOfUnsatisfied = Integer.MAX_VALUE;
      for (String line = stdInput.readLine(); line != null; line = stdInput.readLine()) {
        if (line.startsWith("Begin assign with")) {
          int cost = Integer.valueOf(line.substring(line.indexOf("=") + 1, line.indexOf("(")).trim());
          if (cost < lowestCostOfUnsatisfied) {
            bestResult.clear();
            lowestCostOfUnsatisfied = cost;
            for (String atoms = stdInput.readLine(); !atoms.startsWith("End"); atoms = stdInput.readLine()) {
              for (String atom : atoms.trim().split(" ")) {
                bestResult.add(Integer.valueOf(atom) - 1);
              }
            }
          }
        }
      }


      profiler.end();

      boolean[] result = new boolean[atomCount];
      for (int i = 0; i < result.length; ++i)
        if (bestResult.contains(i)) result[i] = true;

      return result;
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    return null;
  }

  public void setStates(boolean[] states) {
    this.initStates = states;
  }

  public void setProfiler(Profiler profiler) {
    this.profiler = profiler;
  }


  public void setGreedyProbability(double greedy) {
    this.greedy = greedy;
  }
}