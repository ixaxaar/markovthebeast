package thebeast.pml;

import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.value.TupleValue;
import thebeast.nod.value.RelationValue;
import thebeast.util.Profiler;
import thebeast.util.NullProfiler;
import thebeast.osi.OsiSolver;
import thebeast.osi.OsiSolverJNI;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 06-Feb-2007 Time: 22:30:52
 */
public class ILPSolverOsi implements ILPSolver {

  private OsiSolver solver =OsiSolverJNI.create(OsiSolverJNI.Implementation.CLP) ;
  private int numRows, numCols;
  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private boolean enforceInteger = false;
  private boolean verbose = false;
  private Profiler profiler = new NullProfiler();
  private boolean writeLp = false;

  public void init() {
    //solver.reset();
    solver = OsiSolverJNI.create(OsiSolverJNI.Implementation.CLP);
    solver.setObjSense(-1);
    solver.setHintParam(OsiSolver.OsiHintParam.OsiDoReducePrint, true, OsiSolver.OsiHintStrength.OsiHintTry);
    
    solver.setCbcLogLevel(0);
    //solver.setCbcLogLevel(0);
    numRows = 0;
    numCols = 0;

  }

  public void add(RelationVariable variables, RelationVariable constraints) {
    //System.out.println(constraints.value());
//    for (int i = 0; i < variables.value().size(); ++i)
//      solver.addCol(0, new int[0], new double[0], 0, 1.0, 0.0);
    int newCols = variables.value().size();
    int[][] rows = new int[newCols][0];
    double[][] elements = new double[newCols][0];
    double[] zeros = new double[newCols];
    double[] colub = new double[newCols];
    Arrays.fill(colub,1.0);
    if (newCols > 0) solver.addCols(newCols,rows, elements, zeros, colub, zeros);
    for (TupleValue var : variables.value()) {
      int index = var.intElement("index").getInt();
      double weight = var.doubleElement("weight").getDouble();
      solver.setObjCoeff(index, weight);
      if (enforceInteger) solver.setInteger(index);
      ++this.numCols;
    }
    int newRows = constraints.value().size();
    int[][] cols = new int[newRows][];
    double[][] elem = new double[newRows][];
    double[] rowlb = new double[newRows];
    double[] rowub = new double[newRows];
    int row = 0;
    for (TupleValue constraint : constraints.value()) {
      double lb = constraint.doubleElement("lb").getDouble();
      double ub = constraint.doubleElement("ub").getDouble();
      RelationValue values = constraint.relationElement("values");
      int length = values.size();
      int[] indices = new int[length];
      double[] weights = new double[length];
      int index = 0;
      for (TupleValue nonZero : values) {
        indices[index] = nonZero.intElement("index").getInt();
        weights[index++] = nonZero.doubleElement("weight").getDouble();
      }
      cols[row] = indices;
      elem[row] = weights;
      rowlb[row] = lb;
      rowub[row] = ub;
      ++row;
      //solver.addRow(length,indices, weights, lb, ub);
    }
    solver.addRows(newRows, cols, elem, rowlb, rowub);
  }

  public static double convert(double orginal) {
    if (orginal == Double.POSITIVE_INFINITY) return OsiSolver.INF;
    if (orginal == Double.NEGATIVE_INFINITY) return -OsiSolver.INF;
    return orginal;
  }

  public RelationVariable solve() {
    solver.resolve();
    //solver.branchAndBound();
    //System.out.println("solver.getNcolumns() = " + solver.getNcolumns());;
    double[] solution = solver.getColSolution();
    int[] indices = new int[numCols];
    for (int index = 0; index < solution.length; ++index) {
      indices[index] = index;
    }
    //RelationVariable variable = interpreter.createRelationVariable(builder.getRelation());
    RelationVariable variable = interpreter.createRelationVariable(IntegerLinearProgram.getResultHeading());
    variable.assignByArray(indices, solution);
    return variable;

  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  public void setProfiler(Profiler profiler) {

  }


  public Object getProperty(PropertyName name) {
    return null;
  }

  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("verbose"))
      setVerbose((Boolean) value);
  }

  public void setProperty(String name, Object value) {
    if ("verbose".equals(name))
      setVerbose((Boolean) value);

  }
}
