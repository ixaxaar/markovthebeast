include "corpora/train.types.pml";

include "model.pml";

set instancesCacheSize = 10;

//load corpus from conll00 "corpora/train.conll";

load weights from dump "/tmp/depparse.weights.dmp";

load instances from dump "/tmp/depparse.instances.dmp";

set learner.solver.ilp.solver = "osi";
set learner.solver.ilp.solver.implementation = "clp";
set learner.solver.maxIterations = 5;
set learner.solver.integer = false;
set learner.solver.deterministicFirst = false;
set learner.update = "mira";
set learner.update.signs = false;
set learner.maxCandidates = 10;
set learner.average = true;
set learner.loss = "avgF1";
//set learner.loss = "globalNumErrors";
set learner.profile = true;
//set learner.penalizeGold = true;
set learner.maxViolations = 1000;
set learner.useGreedy = true;

learn for 1 epochs;

save weights to dump "/tmp/depparse.weights.dmp";

print learner.profiler;
