package thebeast.pml.solve.ilp;

import ilog.concert.*;
import ilog.cplex.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import thebeast.nod.statement.Interpreter;
import thebeast.nod.value.RelationValue;
import thebeast.nod.value.TupleValue;
import thebeast.nod.variable.RelationVariable;
import thebeast.pml.PropertyName;
import thebeast.pml.TheBeast;
import thebeast.util.NullProfiler;
import thebeast.util.Profiler;

public class ILPSolverCplex implements ILPSolver {

	private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
	private boolean verbose = false;
    private long timeout = 1000;
    private IloCplex cplex;
    IloLinearNumExpr objFunc;
    private IloIntVar[] vars;
    int nvars = 0;
    private String logFilename="/tmp/cplex.log";
    private FileOutputStream logFile;
    private String paramFile;
    private boolean paramFileSet = false;
    private boolean writeLP = false;
    private int count = 0;
    private boolean debug = false;
    
    public ILPSolverCplex copy() {
        ILPSolverCplex result = new ILPSolverCplex();
        result.timeout = timeout;
        return result;
    }
    

	public void init() {		
        try{	
        	if (cplex != null)
        		cplex.end();
        	if (logFile == null)
        		logFile = new FileOutputStream(logFilename);
        	cplex = new IloCplex();
        	cplex.setOut(logFile);
        	cplex.setWarning(logFile);
        	objFunc = cplex.linearNumExpr();
        	cplex.addMaximize(objFunc);         	
        	vars = new IloIntVar[100000];
        	nvars = 0;        	
		} catch (IloException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void add(RelationVariable variables, RelationVariable constraints) {
		try {
		 for (TupleValue var : variables.value()) {
	        int index = var.intElement("index").getInt();
	        double weight = var.doubleElement("weight").getDouble();
	        	if (vars[index] == null) {
	        		vars[index] = cplex.boolVar();
	        		nvars++;
	        	}
	     		cplex.setLinearCoef(cplex.getObjective(),vars[index],weight);
	     }
		 
		 for (TupleValue constraint : constraints.value()) {
			 IloLinearNumExpr constraintExpr = cplex.linearNumExpr();
             double lb = constraint.doubleElement("lb").getDouble();
             double ub = constraint.doubleElement("ub").getDouble();             
             RelationValue values = constraint.relationElement("values");

             //iterate over all values of the constraint
 			for (TupleValue nonZero : values) {
 				int index = nonZero.intElement("index").getInt();
 				double weight = nonZero.doubleElement("weight").getDouble();
 				constraintExpr.addTerm(weight,vars[index]); 		 				
 			}
 			
 			if (ub == lb) { //" = "
 				cplex.addEq(constraintExpr,lb);
			} 
 			else if (ub == Double.POSITIVE_INFINITY) { //">="
 				cplex.addGe(constraintExpr,lb);
			} 
 			else {// " <= "
 				cplex.addLe(constraintExpr,ub);
			}
		 }
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	public void addIntegerConstraints(RelationVariable variables) {
		// TODO Auto-generated method stub

	}

	public RelationVariable solve() {
		try{
			count++;			
			if (paramFileSet) cplex.readParam(paramFile);
			if (writeLP) {
				cplex.exportModel("/tmp/cplex_" + count + ".lp");
				cplex.writeParam("/tmp/cplex_params.prm");
			}
			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Best objective value = " + cplex.getObjValue());
				double[] solution = cplex.getValues(vars,0,nvars);								
				int[] indices = new int[nvars];
			    for (int index = 0; index < solution.length; index++) {
			        indices[index] = index;
			        if (debug){
			        	cplex.output().println("Sol["+index+"]="+solution[index]);
			        }
			    }
			    RelationVariable variable = interpreter.createRelationVariable(IntegerLinearProgram.getResultHeading());
			    variable.assignByArray(indices, solution);
			    return variable;
				
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
		return null;
		
	}



	public void setProperty(PropertyName name, Object value) {
		if (name.getHead().equals("timeout"))
            setTimeout((Integer) value);
	    else if (name.getHead().equals("paramFile"))
	        setParamFile((String) value);
	    else if (name.getHead().equals("logFilename"))
	        setLogFilename((String) value);
	    else if (name.getHead().equals("writeLP"))
	        setWriteLP((Boolean) value);
	    else if (name.getHead().equals("debug"))
	        setDebug((Boolean) value);
	}


	
	public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
        
	public Object getProperty(PropertyName name) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setProfiler(Profiler profiler) {
		// TODO Auto-generated method stub

	}

	public boolean isWriteLP() {
        return writeLP;
    }

    public void setWriteLP(boolean writeLP) {
        this.writeLP = writeLP;
    }


    public String getParamFile() {
        return paramFile;
    }

    public void setParamFile(String paramFile) {
        this.paramFile = paramFile;
        this.paramFileSet = true;
    }
    
    public void setLogFilename(String logFilename){
    	this.logFilename = logFilename;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public void setDebug(boolean debug){
    	this.debug = debug;
    }
}
