include "model1.pml";

hidden: follows, activeGroup, activeTarget;
observed: group, target, source, groupScore, followsScore;

load corpus from $1;


set solver.ground.atLeastOneGroup = true;
set solver.ground.atLeastOneEnd = true;
set solver.ground.atLeastOneBegin = true;
set solver.ground.groupImpliesTargetActive = true;
//set solver.ground.acyclicity = true;
/*
set solver.ground.atMostOneGroup = true;
set solver.ground.atMostOneEnd = true;
set solver.ground.atMostOneBegin = true;
set solver.ground.targetImpliesGroupActive = true;
set solver.ground.followsActiveBegin = true;
set solver.ground.followsActiveEnd = true;
*/

set printer = "mt";

save corpus to ram;

next;

set solver.showIterations = true;
set solver.model.solver.bbDepthLimit=20;
set solver.model.initIntegers = true;
//set solver.integer = true;
//set solver.model.solver.writeLP = true;

solve;
//solve;
//solve;
//set solver.integer = true;

print atoms.follows;
print atoms.activeTarget;
print atoms.activeGroup;

print atoms;

print solver.profiler;

//print formulas;
//print solver.model.fractionals;
//print solver.model;
//print solver.history;


