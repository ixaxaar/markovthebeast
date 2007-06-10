include "model1.pml";

hidden: follows, activeGroup, activeTarget;
observed: group, target, source, groupScore, followsScore;

load corpus from "input.atoms";

set solver.ground.exactlyOneGroup = true;
set solver.ground.groupImpliesTargetActive = true;
set solver.ground.targetImpliesGroupActive = true;
set solver.ground.followsActiveBegin = true;
set solver.ground.followsActiveEnd = true;
//set solver.ground.atLeastOneEnd = true;
set solver.ground.atMostOneEnd = true;
//set solver.ground.atLeastOneBegin = true;
set solver.ground.atMostOneBegin = true;

save corpus to ram;

next;

set solver.model.initIntegers = true;

solve;
//set solver.integer = true;



