include "types.pml";
include "de-en-model.pml";

set instancesCacheSize = 5;
set corpusCacheSize = 20;

//load weights from dump "/tmp/epoch_6.dmp";
load weights from dump $1;
//load weights from dump "/tmp/srl.weights.dmp";

load instances from dump "/tmp/alignment.instances.dmp";

set learner.update = "mira";
set learner.average = false;
set learner.update.signs = true;
set learner.solver.model.initIntegers = true;
//set learner.solver.model.solver = "cbc";
//set learner.solver.integer = true;
set learner.solver.maxIterations = 50;
set learner.solver.model.solver.bbDepthLimit = -50;
//set learner.solver.model.solver.breakAtFirst = true;
set learner.maxCandidates = 1;
//set learner.minOrder = 1;
set learner.solver.timeout = 10000;
//set learner.loss = "globalF1";
set learner.loss.restrict.align(0,*) = true;
set learner.loss.restrict.align(*,0) = true;
//set learner.loss.restrict.aligndown(*,*) = true;
//set learner.loss.restrict.aligndiag(*,*) = true;
//set learner.loss.restrict.alignright(*,*) = true;

set learner.loss = "globalNumErrors";
set learner.profile = true;
set learner.solver.model.solver.timeout = 10;
//set learner.maxAtomCount = 25000;

learn for 10 epochs;
//set learner.solver.model.solver.breakAtFirst = false;
//learn for 10 epochs;

print learner.profiler;

save weights to dump "/tmp/alignment.weights.dmp";
