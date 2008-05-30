include $1;
include "model1.pml";

load weights from dump "/tmp/conll08-clear.weights";

load instances from dump "/tmp/conll08.dmp";

set learner.solver.model.initIntegers = false;
//set learner.solver.integer = true;
set learner.solver.maxIterations = 20;
set learner.solver.model.solver.timeout = 1;
//set learner.loss.restrict.role(*,*,'NONE') = true;
//set learner.minOrder = 1;

//print learner;

learn for 3 epochs;

print learner.profiler;

save weights to dump "/tmp/conll08.weights";