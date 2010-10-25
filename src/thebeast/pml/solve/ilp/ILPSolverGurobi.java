package thebeast.pml.solve.ilp;

import gurobi.*;

import thebeast.nod.statement.Interpreter;
import thebeast.nod.value.RelationValue;
import thebeast.nod.value.TupleValue;
import thebeast.nod.variable.RelationVariable;
import thebeast.pml.PropertyName;
import thebeast.pml.TheBeast;
import thebeast.util.Profiler;

import java.util.Hashtable;

/**
 * Gurobi interface class
 */
public class ILPSolverGurobi implements ILPSolver {

  private GRBEnv env;
  private GRBModel  model;
  
  private Hashtable<String, GRBVar> C;

	
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private boolean writeLp = false;
  private long timeout = 1000;
  private int bbDepthLimit = -50;//3;
  private int bbRule;
  private String paramFile;
  private boolean breakAtFirst = false;
  private int maxRank = Integer.MAX_VALUE;

  
  public ILPSolverGurobi copy() {
	  ILPSolverGurobi result = new ILPSolverGurobi();
      result.timeout = timeout;
      result.bbDepthLimit = bbDepthLimit;
      result.writeLp = writeLp;
      result.bbRule = bbRule;
      result.maxRank = maxRank;
      result.paramFile = paramFile;
      return result;
  }
  
  public void init() {
 
    
	try {
		env = new GRBEnv("gurobi.log");
		model = new GRBModel(env);
		model.set(GRB.IntAttr.ModelSense, -1);
	} catch (GRBException e) {
		e.printStackTrace();
	}
	C = new Hashtable<String, GRBVar>(1000);
  }

  public void delete() {
   
  }

  public void add(RelationVariable variables, RelationVariable constraints) {
   
	 for (TupleValue var : variables.value()) {
        int index = var.intElement("index").getInt();
        double weight = var.doubleElement("weight").getDouble();
        
        //System.out.println("var: " + String.valueOf(index) + " weight: " + weight);
     	try {
     		C.put(String.valueOf(index), model.addVar(0.0, 1.0, weight, GRB.BINARY, ""));
		} catch (GRBException e) {
			e.printStackTrace();
		}
      }
      
      try {
    	//model.set(GRB.IntAttr.ModelSense, -1);
  		model.update();
  		} catch (GRBException e) {
  			e.printStackTrace();
  		}

  		//we iterate over all constraints
  		for (TupleValue constraint : constraints.value()) {
    	  
    			//use this to build the expression for gurobi
		    	GRBLinExpr expr = new GRBLinExpr();
    	
		    	//System.out.println("------------------");
    	  
			double lb = constraint.doubleElement("lb").getDouble();
			double ub = constraint.doubleElement("ub").getDouble();
			RelationValue values = constraint.relationElement("values");
		

			//iterate over all values of the constraint
			for (TupleValue nonZero : values) {
		  
				int var = nonZero.intElement("index").getInt();
				double weight = nonZero.doubleElement("weight").getDouble();
		  
			  expr.addTerm(weight, C.get(String.valueOf(var)));
			  //System.out.print(weight+"*C"  + String.valueOf(var));
			  //System.out.print(" + ");
			}

			char type;
					
			if (ub == lb) {
				type = GRB.EQUAL;
				//System.out.println(" = ");
			} else if (ub == Double.POSITIVE_INFINITY) {
				type = GRB.GREATER_EQUAL;
				//System.out.print(" >= ");
			} else {
				type = GRB.LESS_EQUAL;
				//System.out.print(" <= ");
			}

			//System.out.print(lb);
		
			try {
				model.addConstr(expr, type, type == GRB.LESS_EQUAL ? ub : lb, "");
			} catch (GRBException e) {
				e.printStackTrace();
			}
      	}
      
      /*int numOfVars = 0;
      int numOfConstr = 0;
      try {
			numOfConstr = model.get(GRB.IntAttr.NumConstrs);
			numOfVars = model.get(GRB.IntAttr.NumVars);
		} catch (GRBException e1) {
			e1.printStackTrace();
		}
		
		//System.out.println("Number of constraints: " + numOfConstr);
		//System.out.println("Number of variables: " + numOfVars);
		  
		 */
      
  }

  public void addIntegerConstraints(RelationVariable variables) {
    
  }


  public RelationVariable solve() {

	try {
		model.optimize();
	} catch (GRBException e) {
		e.printStackTrace();
	}
	
	int numOfVars = 0;
	try {
		numOfVars = model.get(GRB.IntAttr.NumVars);
	} catch (GRBException e1) {
		e1.printStackTrace();
	}
	
	//System.out.println("Number of variables: " + numOfVars);
	
	double[] solution = new double[numOfVars];
	
	for (int i = 0; i < numOfVars; i++) {
		try {
			solution[i] = C.get(String.valueOf(i)).get(GRB.DoubleAttr.X);
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}
	
    int[] indices = new int[numOfVars];
    for (int index = 0; index < solution.length; index++) {
        indices[index] = index;
    }
    RelationVariable variable = interpreter.createRelationVariable(IntegerLinearProgram.getResultHeading());
    variable.assignByArray(indices, solution);
    return variable;
   
  }

  public void setVerbose(boolean verbose) {
   
  }

  public void setProfiler(Profiler profiler) {

  }


  public Object getProperty(PropertyName name) {
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

  }

  public void setProperty(String name, Object value) {
    if ("verbose".equals(name))
      setVerbose((Boolean) value);

  }
}
