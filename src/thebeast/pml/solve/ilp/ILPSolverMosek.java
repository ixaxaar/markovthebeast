package thebeast.pml.solve.ilp;

import mosek.*;
import mosek.Error;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.value.RelationValue;
import thebeast.nod.value.TupleValue;
import thebeast.nod.variable.RelationVariable;
import thebeast.pml.PropertyName;
import thebeast.pml.TheBeast;
import thebeast.util.Profiler;

import java.lang.Exception;

/**
 * @author Sebastian Riedel
 */
public class ILPSolverMosek implements ILPSolver {

  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private Env env = new Env();
  private Task task;
  private boolean hasIntegers = false;
  private boolean verbose = false;
  private double relGapTol = 1.00e-06;
  private boolean relGapTolSet = false;

  public ILPSolverMosek() {
    try {
      //env.connectStream(new msgclass(), Env.streamtype.log);
      //env.
      env.init();
    } catch (Error error) {
      error.printStackTrace();
    } catch (Warning warning) {
      warning.printStackTrace();
    }
  }

  public void init() {
    try {
      if (task != null) task.dispose();
      task = new Task(env, 0, 0);
      task.set_Progress(null);
      task.putobjsense(Env.objsense.maximize);
      task.connectProgress(null);
      if (relGapTolSet) task.putdouparam(Env.dparam.mio_tol_rel_gap,relGapTol);
      task.set_Stream(Env.streamtype.log,verbose ? new msgclass() : null);
      hasIntegers = false;
    } catch (Warning warning) {
      warning.printStackTrace();
    } catch (Error error) {
      error.printStackTrace();
    }
  }

  public void add(RelationVariable variables, RelationVariable constraints) {
    if (task == null)
      throw new RuntimeException("Solver not initialized, please call init() first");
    try {
      int[] ncArray = new int[1];
      task.getnumvar(ncArray);
      int[] nrArray = new int[1];
      task.getnumcon(nrArray);
      int newvarcount = variables.value().size();
      int newconscount = constraints.value().size();
      int numCols = ncArray[0] + newvarcount;
      int numRows = nrArray[0] + constraints.value().size();
      //System.out.println(variables.value());
      //System.out.println(numRows);
      task.putmaxnumcon(numRows);
      task.putmaxnumvar(numCols);
      task.append(Env.accmode.var, newvarcount);

      for (TupleValue var : variables.value()) {
        int index = var.intElement("index").getInt();
        double c = var.doubleElement("weight").getDouble();
        task.putbound(Env.accmode.var, index, Env.boundkey.ra, 0.0, 1.0);
        task.putcj(index, c);
      }
      task.append(Env.accmode.con, newconscount);
      int currentNumConstraints = nrArray[0];
      for (TupleValue constraint : constraints.value()) {
        double lb = constraint.doubleElement("lb").getDouble();
        double ub = constraint.doubleElement("ub").getDouble();

        RelationValue values = constraint.relationElement("values");
        int length = values.size();
        int[] indices = new int[length];
        double[] weights = new double[length];
        int index = 0;
        for (TupleValue nonZero : values) {
          int var = nonZero.intElement("index").getInt();
          if (var > numCols)
            throw new RuntimeException("The constraint " + constraint + " contains a variable which has not been added" +
                    "yet: nr " + (var));
          indices[index] = var;
          weights[index++] = nonZero.doubleElement("weight").getDouble();
        }
        task.putbound(Env.accmode.con, currentNumConstraints, Env.boundkey.ra, lb, ub);
        task.putavec(Env.accmode.con, currentNumConstraints++, indices, weights);
      }
    } catch (Warning warning) {
      warning.printStackTrace();
    } catch (mosek.Error error) {
      error.printStackTrace();
    } catch (ArrayLengthException e) {
      e.printStackTrace();
    }

  }

  public void addIntegerConstraints(RelationVariable variables) {
    int[] indices = variables.getIntColumn("index");
    if (indices.length > 0) hasIntegers = true;
    try {
      for (int index : indices)
        task.putvartype(index, Env.variabletype.type_int);
    } catch (Warning warning) {
      warning.printStackTrace();
    } catch (Error error) {
      error.printStackTrace();
    }

  }

  public RelationVariable solve() {
    try {
      task.optimize();
      int[] ncArray = new int[1];
      task.getnumvar(ncArray);
      double[] solution = new double[ncArray[0]];
      int numCols = solution.length;
      task.getsolutionslice(hasIntegers ? mosek.Env.soltype.itg : Env.soltype.bas,
              mosek.Env.solitem.xx, /* Which part of solution. */
              0, /* Index of first variable. */
              solution.length, /* Index of last variable+1 */
              solution);
//      System.out.println(solution[21]);
//      System.out.println(solution[55]);
//      System.out.println(solver.getStatustext(solver.getStatus()));
      int[] indices = new int[numCols];
      for (int index = 0; index < solution.length; ++index) {
        indices[index] = index;
      }
      RelationVariable variable = interpreter.createRelationVariable(IntegerLinearProgram.getResultHeading());
      variable.assignByArray(indices, solution);
      return variable;

    } catch (Warning warning) {
      warning.printStackTrace();
    } catch (Error error) {
      error.printStackTrace();
    } catch (ArrayLengthException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void setVerbose(boolean verbose) {

  }

  public void setProfiler(Profiler profiler) {

  }

  public ILPSolver copy() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("verbose"))
      verbose = (Boolean) value;
    else if (name.getHead().equals("relGapTol"))
      setRelGapTolerance((Double)value);
  }

  public void setRelGapTolerance(double gap){
    relGapTol = gap;
    relGapTolSet = true;
  }

  public Object getProperty(PropertyName name) {
    return null;
  }


  static class msgclass extends mosek.Stream {
    public msgclass() {
      super();
    }

    public void print(String msg) {
      System.out.print(msg);
    }
  }


  static final int NUMCON = 2;
  static final int NUMVAR = 2;
  static final int NUMANZ = 4;

  //
  public static void main(String[] args) {
    // Since the value infinity is never used, we define
    // 'infinity' symbolic purposes only
    double infinity = 0;

    int[] bkc = {mosek.Env.boundkey.up, mosek.Env.boundkey.lo};
    double[] blc = {-infinity, -4.0};
    double[] buc = {250.0, infinity};

    int[] bkx = {mosek.Env.boundkey.lo, mosek.Env.boundkey.lo};
    double[] blx = {0.0, 0.0};
    double[] bux = {infinity, infinity};

    double[] c = {1.0, 0.64};

    int[] asub = {0, 1, 0, 1};
    double[] aval = {50.0, 3.0, 31.0, -2.0};

    int[] ptrb = {0, 2};
    int[] ptre = {2, 4};

    double[] xx = new double[NUMVAR];

    mosek.Env env = null;
    mosek.Task task = null;

    try {
      // Make mosek environment.
      env = new mosek.Env();
      // Direct the env log stream to the user specified
      // method env_msg_obj.print
      msgclass env_msg_obj = new msgclass();
      //env.connectStream(env_msg_obj, mosek.Env.streamtype.log);
      // Initialize the environment.
      env.init();
      // Create a task object linked with the environment env.
      task = new mosek.Task(env, NUMCON, NUMVAR);
      // Directs the log task stream to the user specified
      // method task_msg_obj.print
      msgclass task_msg_obj = new msgclass();
      //task.connectStream(task_msg_obj, mosek.Env.streamtype.log);
      //task.set_Progress(null);
      //task.connectProgress(null);
      task.set_Stream(Env.streamtype.log,null);
      task.set_Stream(Env.streamtype.log,new msgclass());
      task.inputdata(NUMCON, NUMVAR,
              c,
              0.0,
              ptrb,
              ptre,
              asub,
              aval,
              bkc,
              blc,
              buc,
              bkx,
              blx,
              bux);

      /* Specify integer variables. */
      for (int j = 0; j < NUMVAR; ++j)
        task.putvartype(j, mosek.Env.variabletype.type_int);

      /* A maximization problem */
      task.putobjsense(mosek.Env.objsense.maximize);
      /* Solve the problem */
      try {
        task.optimize();
      }
      catch (mosek.Warning e) {
        System.out.println("Mosek warning:");
        System.out.println(e.toString());
      }
      task.solutionsummary(mosek.Env.streamtype.msg);

      task.getsolutionslice(mosek.Env.soltype.itg,
              /* Integer solution.       */
              mosek.Env.solitem.xx,
              /* Which part of solution.  */
              0,
              /* Index of first variable. */
              NUMVAR,
              /* Index of last variable+1 */
              xx);
      for (int j = 0; j < NUMVAR; ++j)
        System.out.println("x[" + j + "]:" + xx[j]);
    }
    catch (mosek.ArrayLengthException e) {
      System.out.println("Error: An array was too short");
      System.out.println(e.toString());
    }
    catch (mosek.Exception e)
      /* Catch both mosek.Error and mosek.Warning */ {
      System.out.println("An error or warning was encountered");
      System.out.println(e.getMessage());
    }

    if (task != null) task.dispose();
    if (env != null) env.dispose();
  }

}
