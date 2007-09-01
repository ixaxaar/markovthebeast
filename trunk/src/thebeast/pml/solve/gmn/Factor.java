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

  private FactorFormula formula;
  private HashMap<Variable, Constant> assignment = new HashMap<Variable, Constant>();
  private HashSet<Node> nodes = new HashSet<Node>();
  private double weight;

  


}
