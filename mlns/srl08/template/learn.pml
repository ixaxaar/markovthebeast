include "TYPES";
include "MODEL";

load corpus from "ATOMS";

load instances from dump "INSTANCES";
load weights from dump "WEIGHTS";

set learner.solver.model.initIntegers = false;
set learner.solver.maxIterations = 20;
set learner.saveAfterEpoch = false;
//print learner;

learn for EPOCHS epochs;


save weights to dump "WEIGHTS";

print learner.profiler;



