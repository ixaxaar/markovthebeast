include "corpora/train.types.pml";

include "model.pml";

set instancesCacheSize = 5;

//load corpus from conll00 "train.conll";

load weights from dump $1;

//print weights.bias;

//load instances from dump "/tmp/depparse.instances.2.dmp";
load instances from dump "/disk/home/dendrite/s0349492/tmp/depparse.instances.1000.dmp";
//set learner.solver.ilp.solver = "osi";
//set learner.solver.ilp.solver.implementation = "clp";
set learner.solver.model.solver.writeLP = true;
set learner.solver.maxIterations = 5;
set learner.solver.timeout = 1000;
set learner.solver.integer = true;
//set learner.solver.deterministicFirst = false;
set learner.update = "mira";
set learner.update.signs = true;
set learner.maxCandidates = 1;
set learner.minOrder = 0;
set learner.average = true;
set learner.loss = "avgF1";
//set learner.loss = "globalNumErrors";
set learner.profile = true;

set learner.solver.order.label_leq1 = 0;
set learner.solver.order.head_leq1 = 0;
set learner.solver.order.overlap1 = 1;
set learner.solver.order.overlap2 = 1;
set learner.solver.order.nocycles = 2;
set learner.solver.order.f_2nd = 3;

learn for 10 epochs;

save weights to dump "/tmp/depparse.weights.dmp";

print learner.profiler;
