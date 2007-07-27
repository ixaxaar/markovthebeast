include "types.pml";
include "model.pml";

set instancesCacheSize = 5;
set corpusCacheSize = 20;

load weights from dump "/tmp/srl.clean.weights.dmp";

load instances from dump "/disk/home/dendrite/s0349492/tmp/srl.instances.dmp";

//set learner.solver.integer = true;
set learner.loss = "avgF1";
set learner.solver.model.initIntegers = true;
set learner.solver.maxIterations = 10;
set learner.solver.model.solver.bbDepthLimit=200;
//set learner.useGreedy = false;
set learner.maxCandidates=1;
set learner.update.signs = true;
set learner.average = true;

//set learner.maxViolations = 1000;

//inspect Labeller;

//print weights.w_path;

learn for 15 epochs;

save weights to dump "/tmp/srl.weights.dmp";


