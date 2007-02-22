package thebeast.pml;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.value.RelationValue;
import thebeast.nod.value.TupleValue;
import thebeast.nod.variable.RelationVariable;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 06-Feb-2007 Time: 22:30:52
 */
public class ILPSolverLpSolve implements ILPSolver {

  private LpSolve solver;
  private int numRows, numCols;
  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private boolean enforceInteger = true;
  private boolean verbose = false;

  public void init() {
    try {
      solver = LpSolve.makeLp(0, 0);
      solver.setMaxim();
      solver.setBbDepthlimit(3);
      numRows = 0;
      numCols = 0;
      solver.setVerbose(verbose ? 5 : 0);
    } catch (LpSolveException e) {
      e.printStackTrace();
    }
  }

  public void add(RelationVariable variables, RelationVariable constraints) {
    //System.out.println(constraints.value());
    if (solver == null)
      throw new RuntimeException("Solver not initialized, please call init() first");
    try {
      for (int i = 0; i < variables.value().size(); ++i)
        solver.addColumnex(0, new double[0], new int[0]);
      for (TupleValue var : variables.value()) {
        int index = var.intElement("index").getInt();
        double weight = var.doubleElement("weight").getDouble();
        solver.setObj(index + 1, weight);
        solver.setBounds(index + 1, 0, 1);
        if (enforceInteger) solver.setInt(index + 1, true);
        ++numCols;
      }
      solver.setAddRowmode(true);
      for (TupleValue constraint : constraints.value()) {
        double lb = constraint.doubleElement("lb").getDouble();
        double ub = constraint.doubleElement("ub").getDouble();
        RelationValue values = constraint.relationElement("values");
        int length = values.size();
        int[] shifted = new int[length];
        double[] weights = new double[length];
        int index = 0;
        for (TupleValue nonZero : values) {
          shifted[index] = nonZero.intElement("index").getInt() + 1;
          weights[index++] = nonZero.doubleElement("weight").getDouble();
        }
        int type = ub == lb ? LpSolve.EQ : ub == Double.POSITIVE_INFINITY ?
                LpSolve.GE : LpSolve.LE;
        solver.addConstraintex(length, weights, shifted, type, type == LpSolve.LE ? ub : lb);
        ++numRows;
      }
      solver.setAddRowmode(false);
    } catch (LpSolveException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }


  public RelationVariable solve() {
    try {
      solver.writeLp("/tmp/debug.lp");
      solver.solve();
      double[] solution = new double[numCols];
      solver.getVariables(solution);
      for (int index = 0; index < solution.length; ++index) {
        builder.id("index").num(index).id("value").num(solution[index]).tupleForIds();
      }
      builder.relation(solution.length);
      return interpreter.createRelationVariable(builder.getRelation());
    } catch (LpSolveException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public void setVerbose(boolean verbose) {
    if (solver != null) solver.setVerbose(verbose ? 5 : 0);
    else this.verbose = verbose;
  }


  public Object getProperty(PropertyName name) {
    return null;
  }

  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("verbose"))
      setVerbose((Boolean)value);      
  }

  public void setProperty(String name, Object value) {
    if ("verbose".equals(name))
      setVerbose((Boolean)value);

  }
}
