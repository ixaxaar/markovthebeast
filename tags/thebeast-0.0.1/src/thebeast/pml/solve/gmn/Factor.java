package thebeast.pml.solve.gmn;

import thebeast.pml.formula.FactorFormula;
import thebeast.pml.term.Variable;
import thebeast.pml.term.Constant;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Sebastian Riedel
 */
public class Factor {

  private FactorFormula formula, grounded;
  private HashMap<Variable, Constant> assignment = new HashMap<Variable, Constant>();
  private HashSet<Node> nodes = new HashSet<Node>();
  private double weight;


  Factor(FactorFormula formula, double weight) {
    this.formula = formula;
    this.weight = weight;
  }

  void addAssignment(Variable var, Constant constant){
    assignment.put(var,constant);
  }

  void addNode(Node node){
    nodes.add(node);
  }

}
