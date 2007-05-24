package thebeast.pml.solve.ilp;

import thebeast.nod.variable.RelationVariable;
import thebeast.util.Profiler;
import thebeast.pml.PropertyName;
import mosek.Env;

/**
 * @author Sebastian Riedel
 */
public class ILPSolverMosek implements ILPSolver {

  Env env;

  public void init() {

  }

  public void add(RelationVariable variables, RelationVariable constraints) {

  }

  public void addIntegerConstraints(RelationVariable variables) {

  }

  public RelationVariable solve() {
    return null;
  }

  public void setVerbose(boolean verbose) {

  }

  public void setProfiler(Profiler profiler) {

  }

  public void setProperty(PropertyName name, Object value) {

  }

  static class msgclass extends mosek.Stream {
    public msgclass() {
      super();
    }

    public void print(String msg) {
      System.out.print(msg);
    }
  }

  public Object getProperty(PropertyName name) {
    return null;
  }

  static final int NUMCON = 2;
  static final int NUMVAR = 2;
  static final int NUMANZ = 4;

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
      env.connectStream(env_msg_obj, mosek.Env.streamtype.log);
      // Initialize the environment.
      env.init();
      // Create a task object linked with the environment env.
      task = new mosek.Task(env, NUMCON, NUMVAR);
      // Directs the log task stream to the user specified
      // method task_msg_obj.print
      msgclass task_msg_obj = new msgclass();
      task.connectStream(task_msg_obj, mosek.Env.streamtype.log);
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
