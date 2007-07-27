include "types.clear.pml";
include "model.pml";

//load corpus from "one-sentence.crp";
load corpus from "corpora/small-train-set.crp";

//save corpus to ram;

collect;

types to "types.pml";

save corpus to instances "/tmp/srl.instances.dmp";

//set learner.solver.integer = true;
set learner.loss = "avgF1";
set learner.solver.model.initIntegers = true;
set learner.solver.maxIterations = 10;
//set learner.useGreedy = false;
//set learner.maxViolations = 1000;

learn for 10 epochs;

next;

//print weights.w_label;


