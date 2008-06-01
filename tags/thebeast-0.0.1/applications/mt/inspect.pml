include "model4.pml";

load corpus from "corpora/cow.2";

set printer = "mtm4";


save corpus to ram;

next;

set solver.showIterations = true;
set solver.model.solver.bbDepthLimit=20;
set solver.model.initIntegers = true;
//set solver.integer = true;
//set solver.model.solver.writeLP = true;

//solve;
//solve;
//solve;
//set solver.integer = true;

//print atoms.follows;
//print atoms.activeTarget;
//print atoms.activeGroup;

//print atoms;

//print solver.profiler;

//print formulas;
//print solver.model.fractionals;
//print solver.model;
//print solver.history;


