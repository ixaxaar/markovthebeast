include "types.pml";
include "model-fert.pml";

set instancesCacheSize = 5;
set corpusCacheSize = 20;

//load weights from dump "/tmp/epoch_6.dmp";
load weights from dump $1;
//load weights from dump "/tmp/srl.weights.dmp";

load instances from dump "/tmp/alignment.instances.dmp";

set learner.update = "mira";
set learner.solver.model.initIntegers = true;
//set learner.solver.integer = true;
set learner.solver.maxIterations = 5;
set learner.solver.model.solver.bbDepthLimit = -50;
//set learner.solver.model.solver.breakAtFirst = true;
set learner.maxCandidates = 1;
set learner.loss = "globalF1";
//set learner.loss = "globalNumErrors";
set learner.profile = true;
set learner.solver.model.solver.timeout = 1;

//set learner.solver.order.tgtchunk = 1;
//set learner.solver.order.srcchunk = 1;
//set learner.solver.order.cluster = 1;
//set learner.solver.order.srcpair = 1;

/*
set learner.solver.ground.srcmaxfert = true;
set learner.solver.ground.tgtmaxfert = true;
set learner.solver.ground.srcminfert = true;
set learner.solver.ground.tgtminfert = true;
set learner.solver.ground.srcfert_leq1 = true;
set learner.solver.ground.srcfert_geq1 = true;
set learner.solver.ground.tgtfert_leq1 = true;
set learner.solver.ground.tgtfert_geq1 = true;
*/

//set learner.solver.order.srcmaxfert = 1;
//set learner.solver.order.srcminfert = 1;
//set learner.solver.order.tgtmaxfert = 1;
//set learner.solver.order.tgtminfert = 1;

learn for 50 epochs;
//set learner.solver.model.solver.breakAtFirst = false;
//learn for 10 epochs;


print learner.profiler;

save weights to dump "/tmp/alignment.weights.dmp";
