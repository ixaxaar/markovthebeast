include "model4.pml";

//factor[ground-all]: for Int head, Int mod
//  if mapping(18,head) & mapping(19,mod) & head < 0 & mod > 0: !(activeTarget(head) & activeTarget(mod));   

load corpus from $1;

set printer = "mtm4out";

save corpus to ram;

next;

set solver.showIterations = true;
set solver.model.solver.bbDepthLimit=20;
//set solver.model.solver.timeout = 2;
set solver.model.initIntegers = true;
//set solver.integer = true;
//set solver.model.solver.writeLP = true;

solve;
//solve;
//solve;
//set solver.integer = true;

//print atoms.follows;
//print atoms.activeTarget;
//print atoms.activeGroup;

print atoms;

print solver.profiler;

//print formulas;
//print solver.model.fractionals;
//print solver.model;
//print solver.history;

test to printer "/tmp/mt.out";


