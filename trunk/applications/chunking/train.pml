include "corpora/train.types.pml";

include "model.pml";

load global from "global.txt";
load global.rare from "corpora/rare.txt";

set instancesCacheSize = 3;

load corpus from conll00noisy "corpora/train.conll";

//load weights from dump "/tmp/blank.weights.dmp";
load weights from dump "/tmp/chunking.blank.weights.dmp";

load instances from dump "/tmp/instances.dmp";

set learner.solver.ilp.solver = "lpsolve";
//set learner.solver.ilp.solver.timeout = 100;
//set learner.solver.ilp.solver.bbDepthLimit = 5;
//set learner.solver.ilp.solver.implementation = "clp";
set learner.solver.maxIterations = 10;
set learner.solver.integer = true;
//set learner.solver.deterministicFirst = true;
//set learner.solver.alternating = true;
set learner.update = "mira";
set learner.update.signs = true;
set learner.maxCandidates = 10;
set learner.average = true;
set learner.loss = "globalNumErrors";
set learner.profile = true;
set learner.useGreedy = true;
//set learner.

//set learner.maxViolations = 10;
learn for 10 epochs;

/*
set learner.maxCandidates = 1;
set learner.maxViolations = 0;
//set learner.update = "perceptron";
learn for 20 epochs;
*/

save weights to dump "/tmp/weights.dmp";

print learner.profiler;
