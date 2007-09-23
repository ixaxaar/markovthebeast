package thebeast.pml.solve.weightedsat;

import thebeast.pml.PropertyName;

import java.util.ArrayList;

/**
 * Max-Product MAP solver. Transforms cardinality constraints. Divides conjunctions. Works in log space Normalizes
 * messages to be 0 if false.
 */
public class MaxProduct implements WeightedSatSolver {


  private ArrayList<Edge> edges = new ArrayList<Edge>();
  private ArrayList<Node> nodes = new ArrayList<Node>();
  private ArrayList<Factor> factors = new ArrayList<Factor>();

  private int maxIterations = 1000;
  private double maxEps = 0.01;
  private int iteration;

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

    public Factor(double weight) {
      this.weight = weight;
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
            if (edge.previous.node2factor > 0) edge.maxContribution += edge.previous.node2factor;
          }
        }
      }
    }
  }

  /**
   * Represents an edge in the factor graph between a node and a factor.
   */
  private static class Edge {
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
      //to node
      double ifTrue = factor.weight + maxContribution;
      double max = Double.NEGATIVE_INFINITY;
      for (Edge edge : factor.edges) {
        if (edge != this && edge.maxContribution > max) max = edge.maxContribution;
      }
      if (factor.edges.size() == 1) max = 0;
      double ifFalse = factor.weight + max;
      current.factor2node = sign ? ifTrue - ifFalse : ifFalse - ifTrue;
      return Math.abs(current.factor2node - previous.factor2node);      
    }

    double updateMessagesToFactor() {
      //to node
      current.node2factor = node.score;
      for (Edge edge : node.edges) {
        if (edge != this) current.node2factor += edge.previous.factor2node;
      }
      if (!sign) current.node2factor = -current.node2factor;
      return Math.abs(current.node2factor - previous.node2factor);
    }

    void makeCurrentPrevious(){
      previous.factor2node = current.factor2node;
      previous.node2factor = current.node2factor;
    }

    void initialize(){
      previous.factor2node = Math.log(1.0 - Math.random());
      previous.node2factor = Math.log(1.0 - Math.random());
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

  public double getMaxEps() {
    return maxEps;
  }

  public void setMaxEps(double maxEps) {
    this.maxEps = maxEps;
  }

  /**
   * Performs belief propagation until a max number of iterations or no more message changes.
   */
  private void propagate(){
    for (Edge edge : edges) edge.initialize();
    double eps = Double.MAX_VALUE;
    for (iteration = 0; iteration < maxIterations && eps > maxEps; ++iteration){
      eps = Double.MIN_VALUE;
      for (Factor factor : factors)
        factor.updateMaxContributions();
      for (Edge edge : edges){
        double delta = edge.updateMessagesToFactor();
        if (delta > eps) eps = delta;
        delta = edge.updateMessagesToNode();
        if (delta > eps) eps = delta;
      }
      for (Edge edge : edges){
        edge.makeCurrentPrevious();
      }
    }
    for (Node node : nodes)
      node.updateBelief();

  }


  public void init() {

  }

  public void addAtoms(double[] scores) {
    for (double score : scores){
      nodes.add(new Node(score,nodes.size()));
    }
  }

  public void addClauses(WeightedSatClause... clausesToAdd) {
    for (WeightedSatClause wsc : clausesToAdd){
      WeightedSatClause normalized = wsc.expandCardinalityConstraints().normalize();
      if (normalized == null) continue;
      for (WeightedSatClause clause : normalized.separate()){
        Factor factor = new Factor(clause.score);
        factors.add(factor);
        for (int i = 0; i < clause.atoms[0].length; ++i){
          Node node = nodes.get(clause.atoms[0][i]);
          Edge edge = new Edge(node,factor, clause.signs[0][i]);
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
    int i = 0;
    for (Node node : nodes)
      result[i++] = node.belief > 0;
    return result;
  }


  public int getIterationCount() {
    return iteration;
  }

  public void setStates(boolean[] states) {

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
}
