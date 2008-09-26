package thebeast.pml.solve.weightedsat;

import thebeast.pml.PropertyName;
import thebeast.util.Profiler;

import java.util.ArrayList;
import java.util.Random;

/**
 * Max-Product MAP solver. Transforms cardinality constraints. Divides conjunctions. Works in log space Normalizes
 * messages to be 0 if false.
 */
public class MaxProduct implements WeightedSatSolver {


  private ArrayList<Edge> edges = new ArrayList<Edge>();
  private ArrayList<Node> nodes = new ArrayList<Node>();
  private ArrayList<Factor> factors = new ArrayList<Factor>();
  private Random random = new Random();
  private boolean debug = false;

  private int maxIterations = 1000;
  private double maxEps = 0.01;
  private int iteration;
  private boolean[] best, current;
  private double currentScore, bestScore;

  /**
   * A node in the factor graph.
   */
  private static class Node {
    ArrayList<Edge> edges = new ArrayList<Edge>();
    double score;
    double belief;
    int index;

    public Node(double score, int index) {
      this.score = score;
      this.index = index;
    }

    public String toString() {
      return "N" + index + "(" + score + ")";
    }

    /**
     * Updates the local belief for this node.
     */
    void updateBelief() {
      belief = score;
      for (Edge edge : edges) {
        //belief += (edge.sign ? 1.0 : -1.0) * edge.current.factor2node;
        belief += edge.current.factor2node;
      }
    }


  }

  /**
   * A factor in the factor graph.
   */
  private static class Factor {
    ArrayList<Edge> edges = new ArrayList<Edge>();
    double weight;
    int index;

    public Factor(double weight, int index) {
      this.weight = weight;
      this.index = index;
    }

    public String toString() {
      return "F" + index + "(" + weight + ")";
    }

    /**
     * Calculates the maximal score we can get for each node i if we fix this node to a state where it causes the clause
     * to be true.
     */
    void updateMaxContributions() {
      for (Edge edge : edges) {
        edge.maxContribution = 0;
        for (Edge other : edges) {
          if (other != edge) {
            if (edge.previous.node2factor > 0) edge.maxContribution += other.previous.node2factor;
          }
        }
      }
    }

    /**
     * Calculates the contribution of this factor given the provided state
     *
     * @param state a state vector
     * @return weight of this factor if the disjunction holds in the provided states, 0.0 otherwise.
     */
    double score(boolean[] state) {
      for (Edge edge : edges)
        if (state[edge.node.index] == edge.sign) return weight;
      return 0.0;
    }
  }

  /**
   * Represents an edge in the factor graph between a node and a factor.
   */
  private class Edge {
    Messages previous = new Messages();
    Messages current = new Messages();
    Node node;
    Factor factor;
    boolean sign;
    double maxContribution;


    public Edge(Node node, Factor factor, boolean sign) {
      this.node = node;
      this.factor = factor;
      this.sign = sign;
    }

    double updateMessagesToNode() {
      double ifTrue = factor.weight + maxContribution;
      double max = Double.NEGATIVE_INFINITY;
      for (Edge edge : factor.edges) {
        if (edge != this) {
          double contribution = edge.maxContribution;
          contribution += edge.previous.node2factor;
          if (this.previous.node2factor > 0) contribution -= this.previous.node2factor;
          if (contribution > max) max = contribution;
        }
      }
      if (factor.edges.size() == 1) max = 0;
      double ifFalse = factor.weight + max;
      current.factor2node = sign ? ifTrue - ifFalse : ifFalse - ifTrue;
      return Math.abs(logToProb(current.factor2node) - logToProb(previous.factor2node));
    }

    double updateMessagesToFactor() {
      current.node2factor = node.score;
      for (Edge edge : node.edges) {
        if (edge != this) current.node2factor += edge.previous.factor2node;
      }
      if (!sign) current.node2factor = -current.node2factor;
      return Math.abs(logToProb(current.node2factor) - logToProb(previous.node2factor));
    }

    void makeCurrentPrevious() {
      previous.factor2node = current.factor2node;
      previous.node2factor = current.node2factor;
    }

    void initialize() {
      previous.factor2node = .5 - random.nextFloat();
      previous.node2factor = .5 - random.nextFloat();
    }

    public String toString() {
      return String.format("%-10s%-10s(%4.2f)%5s %-6.2f%5s %-6.2f",
              node, factor, maxContribution,
              "<-", current.factor2node,
              "->", current.node2factor);
    }

  }

  private static class Messages {
    double factor2node;
    double node2factor;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
  }

  public void setSeed(int seed) {
    random = new Random(seed);
  }

  public double getMaxEps() {
    return maxEps;
  }

  public void setMaxEps(double maxEps) {
    this.maxEps = maxEps;
  }

  /**
   * Performs belief propagation until a max number of iterations or no more message changes.
   */
  private void propagate() {
    best = new boolean[nodes.size()];
    current = new boolean[nodes.size()];
    bestScore = Double.NEGATIVE_INFINITY;
    for (Edge edge : edges) edge.initialize();
    double eps = Double.MAX_VALUE;
    for (iteration = 0; iteration < maxIterations && eps > maxEps; ++iteration) {
      if (debug) System.out.println("Iteration:" + iteration);
      eps = Double.MIN_VALUE;
      for (Factor factor : factors)
        factor.updateMaxContributions();
      for (Edge edge : edges) {
        double delta = edge.updateMessagesToFactor();
        if (delta > eps) eps = delta;
        delta = edge.updateMessagesToNode();
        if (delta > eps) eps = delta;
        //if (debug) System.out.println(edge);
      }
      for (Edge edge : edges) {
        edge.makeCurrentPrevious();
      }

      currentScore = 0;
      //determine current state
      for (Node node : nodes){
        node.updateBelief();
        current[node.index] = node.belief > 0;
        if (current[node.index]) currentScore += node.score;
      }
      //calculate it's score
      for (Factor factor : factors)
        currentScore += factor.score(current);

      if (currentScore > bestScore){
        boolean[] tmp = best;
        best = current;
        current = tmp;
        bestScore = currentScore;
      }

      if (debug) {
        System.out.println("Eps: " + eps);
        System.out.println("Current: " + currentScore);
        System.out.println("Best: " + bestScore);
      }
    }

  }


  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public void init() {
    factors.clear();
    nodes.clear();
    edges.clear();
  }

  public void addAtoms(double[] scores) {
    for (double score : scores) {
      nodes.add(new Node(score, nodes.size()));
    }
  }

  public void addClauses(WeightedSatClause... clausesToAdd) {
    for (WeightedSatClause wsc : clausesToAdd) {
      WeightedSatClause normalized = wsc.expandCardinalityConstraints().normalize();
      if (normalized == null) continue;
      for (WeightedSatClause clause : normalized.separate()) {
        Factor factor = new Factor(clause.score, factors.size());
        factors.add(factor);
        for (int i = 0; i < clause.atoms[0].length; ++i) {
          Node node = nodes.get(clause.atoms[0][i]);
          Edge edge = new Edge(node, factor, clause.signs[0][i]);
          factor.edges.add(edge);
          node.edges.add(edge);
          edges.add(edge);
        }
      }
    }
  }

  public boolean[] solve() {
    propagate();
    boolean[] result = new boolean[nodes.size()];
    System.arraycopy(best,0,result,0,best.length);
    return result;
  }


  public int getIterationCount() {
    return iteration;
  }

  public void setStates(boolean[] states) {

  }

  public void setProfiler(Profiler profiler) {

  }

  public WeightedSatSolver copy() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setProperty(PropertyName name, Object value) {
    if ("maxIterations".equals(name.getHead()))
      setMaxIterations((Integer) value);
    else if ("maxEps".equals(name.getHead()))
      setMaxEps((Double) value);


  }

  public Object getProperty(PropertyName name) {
    return null;
  }

  private static double logToProb(double log) {
    return 1.0 / (1.0 + Math.exp(-log));
  }

}
