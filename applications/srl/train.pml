include "types.pml";
include $1;

set instancesCacheSize = 5;
set corpusCacheSize = 20;

//load weights from dump "/tmp/epoch_6.dmp";
load weights from dump $2;
//load weights from dump "/tmp/srl.weights.dmp";

load instances from dump "/disk/home/dendrite/s0349492/tmp/srl.instances.dmp";

set learner.profile = true;

//set learner.solver.integer = true;
//set learner.loss = "globalF1";
set learner.loss = "globalNumErrors";
//set learner.loss.restrict.arg(*,'V') = true;
set learner.solver.model.initIntegers = true;
set learner.solver.model.solver.timeout = 10;
set learner.solver.maxIterations = 50;
set learner.solver.model.solver.bbDepthLimit=-50;
//set learner.useGreedy = false;

set learner.minOrder = 1;
set learner.maxCandidates=1;
//set learner.minCandidates=1;

set learner.update.signs = true;
//set learner.average = true;
set learner.solver.history = false;
set learner.solver.model.solver.writeLP = false;

//set learner.maxViolations = 1000;

//inspect Labeller;

//print weights.w_path;

//learn for 10 epochs;
set learner.solver.model.initIntegers = false;
set learner.solver.maxIterations = 30;
learn for 10 epochs;


set learner.solver.integer = true;
//set learner.solver.model.initIntegers = true;
//set learner.solver.maxIterations = 20;
//learn for 8 epochs;

//print weights.w_isarg_bias;
print learner.profiler;

save weights to dump "/tmp/srl.weights.dmp";


