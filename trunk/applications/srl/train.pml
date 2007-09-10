include "types.pml";
include "model.pml";

set instancesCacheSize = 5;
set corpusCacheSize = 20;

//load weights from dump "/tmp/epoch_6.dmp";
load weights from dump $1;
//load weights from dump "/tmp/srl.weights.dmp";

load instances from dump "/disk/home/dendrite/s0349492/tmp/srl.instances.dmp";

set learner.profile = true;

//set learner.solver.integer = true;
set learner.loss = "globalF1";
//set learner.loss = "globalNumErrors";
//set learner.loss.restrict.arg(*,'V') = true;
set learner.solver.model.initIntegers = true;
set learner.solver.maxIterations = 20;
set learner.solver.model.solver.bbDepthLimit=5;
//set learner.useGreedy = false;

set learner.minOrder = 2;
set learner.maxCandidates=10;
set learner.minCandidates=1;

set learner.update.signs = true;
set learner.average = true;
set learner.solver.history = false;
set learner.solver.model.solver.writeLP = false;

set learner.solver.order.implyArg = 1;
//set learner.solver.order.atLeastOne = 2;
set learner.solver.order.cargimpliesarg = 2;
//set learner.solver.order.duplicatemod = 2;

//set learner.solver.model.solver.writeLP = true;
//set learner.solver.order.implyIsarg = 0;
//set learner.solver.ground.implyArg = true;
//set learner.solver.ground.implyIsarg = true;
//set learner.solver.ground.atMostOneArg = true;
//set learner.solver.order.argpair = 2;
//set learner.solver.order.argpairvoice = 2;
//set learner.solver.order.argpairvoicepred = 2;
//set learner.solver.order.duplicatearg = 2;

//set learner.maxViolations = 1000;

//inspect Labeller;

//print weights.w_path;

//learn for 10 epochs;
learn for 2 epochs;

//print weights.w_isarg_bias;
print learner.profiler;

save weights to dump "/tmp/srl.weights.dmp";


