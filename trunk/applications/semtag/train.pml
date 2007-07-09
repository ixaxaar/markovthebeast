include "model.pml";

load weights from dump "/tmp/semtag.weights";
//load weights from dump "/tmp/epoch_9.dmp";
set instancesCacheSize = 5;
load instances from "/tmp/semtag.instances";

set learner.average = true;
set learner.loss = "globalF1";
//set learner.solver.model.solver = "cbc";
set learner.solver.integer = true;

//set learner.solver.model.solver.implementation = "clp";
//set learner.update = "perceptron";
//set learner.solver.model = "sat";
//set learner.solver.model.solver.maxFlips = 1000000;
//set learner.solver.maxIterations = 2;
//set learner.maxCandidates = 1;
set learner.maxViolations = 1000;
learn for 3 epochs;
save weights to dump "/tmp/semtag.trained.weights";
