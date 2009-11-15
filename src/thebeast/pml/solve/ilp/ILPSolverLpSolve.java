package thebeast.pml.solve.ilp;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.value.RelationValue;
import thebeast.nod.value.TupleValue;
import thebeast.nod.variable.RelationVariable;
import thebeast.pml.PropertyName;
import thebeast.pml.TheBeast;
import thebeast.util.HeapDoubleSorter;
import thebeast.util.NullProfiler;
import thebeast.util.Profiler;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 06-Feb-2007 Time: 22:30:52
 */
public class ILPSolverLpSolve implements ILPSolver {

    private LpSolve solver;
    private int numRows, numCols;
    private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
    private boolean enforceInteger = false;
    private boolean verbose = false;
    private Profiler profiler = new NullProfiler();
    private boolean writeLp = false;
    private long timeout = 1000;
    private int bbDepthLimit = -50;//3;
    private int count = 0;
    private int bbRule;
    private String paramFile;
    private boolean bbRuleSet = false;
    private boolean paramFileSet = false;
    private boolean breakAtFirst = false;
    private int maxRank = Integer.MAX_VALUE;
    private HashMap<Integer, Double> costs = new HashMap<Integer, Double>();
    private boolean throwException = false;


    public ILPSolverLpSolve copy() {
        ILPSolverLpSolve result = new ILPSolverLpSolve();
        result.verbose = verbose;
        result.timeout = timeout;
        result.bbDepthLimit = bbDepthLimit;
        result.writeLp = writeLp;
        result.bbRule = bbRule;
        result.enforceInteger = enforceInteger;
        result.maxRank = maxRank;
        result.paramFile = paramFile;
        return result;
    }

    public void init() {
        try {
            if (solver != null)
                solver.deleteLp();
            solver = LpSolve.makeLp(0, 0);
            solver.setMaxim();
            //solver.setBbDepthlimit(3);
            numRows = 0;
            numCols = 0;
            costs.clear();
            solver.setVerbose(verbose ? 4 : 0);
        } catch (LpSolveException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        if (solver != null) {

            solver.deleteLp();
            solver = null;
        }
    }

    public void add(RelationVariable variables, RelationVariable constraints) {
        //System.out.println(constraints.value());
        if (solver == null)
            throw new RuntimeException("Solver not initialized, please call init() first");
        try {
            int numRows = solver.getNrows() + constraints.value().size();
            int numCols = solver.getNcolumns() + variables.value().size();
            //System.out.println(variables.value());
            //System.out.println(numRows);
            solver.resizeLp(numRows, numCols);
            for (int i = 0; i < variables.value().size(); ++i)
                solver.addColumnex(0, new double[0], new int[0]);
            for (TupleValue var : variables.value()) {
                int index = var.intElement("index").getInt();
                double weight = var.doubleElement("weight").getDouble();
                if (index >= numCols)
                    throw new RuntimeException("Trying to add variable with index " + index + " but " +
                        "there are only " + numCols + " columns");
                try {
                    solver.setObj(index + 1, weight);
                    costs.put(index + 1, weight);
                    solver.setBounds(index + 1, 0, 1);
                    if (enforceInteger) solver.setInt(index + 1, true);
                } catch (LpSolveException e) {
                    throw new RuntimeException("Error when adding new variable " + index + " - num cols: "
                        + numCols + " num rows: " + numRows, e);
                }
                ++this.numCols;
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
                int negativeWeights = 0;
                for (TupleValue nonZero : values) {
                    int var = nonZero.intElement("index").getInt() + 1;
                    if (var > solver.getNcolumns())
                        throw new RuntimeException("The constraint " + constraint + " contains a variable which has not been added" +
                            "yet: nr " + (var - 1));
                    shifted[index] = var;
                    weights[index++] = nonZero.doubleElement("weight").getDouble();
                    if (weights[index - 1] < 0) ++negativeWeights;
                }
                int type = ub == lb ? LpSolve.EQ : ub == Double.POSITIVE_INFINITY ?
                    LpSolve.GE : LpSolve.LE;
                if (values.size() - negativeWeights < maxRank || type == LpSolve.LE) {
                    solver.addConstraintex(length, weights, shifted, type, type == LpSolve.LE ? ub : lb);
                } else {
                    double[] costs = new double[shifted.length];
                    int[] sorted = new int[shifted.length];
                    for (int i = 0; i < sorted.length; ++i) sorted[i] = i;
                    for (int i = 0; i < shifted.length; ++i) costs[i] = this.costs.get(shifted[i]);
                    HeapDoubleSorter sorter = new HeapDoubleSorter();
                    sorter.sort(costs, sorted);
                    int[] newIndices = new int[maxRank];
                    double[] newWeights = new double[maxRank];
                    for (int i = 0; i < maxRank; ++i) {
                        newIndices[i] = shifted[sorted[i]];
                        newWeights[i] = weights[sorted[i]];
                    }
                    solver.addConstraintex(maxRank, newWeights, newIndices, type, type == LpSolve.LE ? ub : lb);
                }
                ++this.numRows;
            }
            solver.setAddRowmode(false);
        } catch (LpSolveException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void addIntegerConstraints(RelationVariable variables) {
        int[] indices = variables.getIntColumn("index");
        try {
            for (int index : indices)
                solver.setInt(index + 1, true);
        } catch (LpSolveException e) {
            e.printStackTrace();
        }
    }


    public RelationVariable solve() {
        RelationVariable variable = interpreter.createRelationVariable(IntegerLinearProgram.getResultHeading());
        try {
            if (paramFileSet) solver.readParams(paramFile, "");
            else {
                solver.setBreakAtFirst(breakAtFirst);
                solver.setBbDepthlimit(bbDepthLimit);
                solver.setTimeout(timeout);
                if (bbRuleSet) solver.setBbRule(bbRule | LpSolve.NODE_GREEDYMODE | LpSolve.NODE_DYNAMICMODE
                    | LpSolve.NODE_RCOSTFIXING);
            }
            //solver.readParams();
            if (writeLp) solver.writeLp("/tmp/debug_" + count + ".lp");
            if (writeLp) solver.writeParams("/tmp/debug_" + count + ".params", "ILPSolverLpSolve.java");
            //System.out.println("solver.getNcolumns() = " + solver.getNcolumns());;
            //if (bbRuleSet) solver.setBbRule(bbRule);
            solver.solve();
            if (solver.getStatus() == LpSolve.INFEASIBLE)
                if (throwException)
                    throw new RuntimeException("ILP Problem infeasible");
                else
                    System.err.println("ILP Problem infeasible! This means that the ILP generated based on the MAP" +
                        "problem is not solvable. In other words, there are some hard constraints that cannot all" +
                        "be fulfilled at the same time. I don't like this, but let's go on for now. ");
            double[] solution = new double[numCols];
            solver.getVariables(solution);
//      System.out.println(solution[21]);
//      System.out.println(solution[55]);
//      System.out.println(solver.getStatustext(solver.getStatus()));
            int[] indices = new int[numCols];
            for (int index = 0; index < solution.length; ++index) {
                indices[index] = index;
            }
            variable.assignByArray(indices, solution);
            return variable;
        } catch (LpSolveException e) {
            if (throwException) {
                throw new RuntimeException(e);
            } else {
                System.err.println("Look, we observed the following exception and we should really worry about it. " +
                    "However, for now we let things flow and worry about it later.");
                e.printStackTrace();
                return variable;
            }
        }
    }


    public void setVerbose(boolean verbose) {
        if (solver != null) solver.setVerbose(verbose ? 5 : 0);
        else this.verbose = verbose;
    }

    public void setProfiler(Profiler profiler) {

    }


    public Object getProperty(PropertyName name) {
        if (name.getHead().equals("objective"))
            try {
                return solver.getObjective();
            } catch (LpSolveException e) {
                e.printStackTrace();
            }
        return null;
    }


    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getBbDepthLimit() {
        return bbDepthLimit;
    }

    public void setBbDepthLimit(int bbDepthLimit) {
        this.bbDepthLimit = bbDepthLimit;
    }

    public void setProperty(PropertyName name, Object value) {
        if (name.getHead().equals("timeout"))
            setTimeout((Integer) value);
        else if (name.getHead().equals("bbDepthLimit"))
            setBbDepthLimit((Integer) value);
        else if (name.getHead().equals("bbRule"))
            setBbRule((Integer) value);
        else if (name.getHead().equals("maxRank"))
            setMaxRank((Integer) value);
        else if (name.getHead().equals("breakAtFirst"))
            setBreakAtFirst((Boolean) value);
        else if (name.getHead().equals("params"))
            setParamFile((String) value);
        else if (name.getHead().equals("verbose"))
            setVerbose((Boolean) value);
        else if (name.getHead().equals("writeLP"))
            setWriteLp((Boolean) value);

    }


    public int getMaxRank() {
        return maxRank;
    }

    public void setMaxRank(int maxRank) {
        this.maxRank = maxRank;
    }

    public boolean isBreakAtFirst() {
        return breakAtFirst;
    }

    public void setBreakAtFirst(boolean breakAtFirst) {
        this.breakAtFirst = breakAtFirst;
    }

    public int getBbRule() {
        return bbRule;
    }

    public void setBbRule(int bbRule) {
        this.bbRule = bbRule;
        this.bbRuleSet = true;
    }

    public boolean isWriteLp() {
        return writeLp;
    }

    public void setWriteLp(boolean writeLp) {
        this.writeLp = writeLp;
    }


    public String getParamFile() {
        return paramFile;
    }

    public void setParamFile(String paramFile) {
        this.paramFile = paramFile;
        this.paramFileSet = true;
    }

    public void setProperty(String name, Object value) {
        if ("verbose".equals(name))
            setVerbose((Boolean) value);

    }
}
