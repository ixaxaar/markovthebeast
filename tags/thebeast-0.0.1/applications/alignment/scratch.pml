include "types.clean.pml";
include "model.pml";

load corpus from $1;

collect;

save weights to dump "/tmp/align.blank.weights";

save corpus to instances "/tmp/align.instances";

set learner.solver.order.tgtchunk = 1;
set learner.solver.order.srcchunk = 1;
set learner.solver.order.cluster = 1;


set learner.update = "mira";
set learner.solver.model.initIntegers = true;
//set learner.solver.integer = true;
set learner.maxCandidates = 1;
set learner.loss = "globalF1";

learn for 100 epochs;

print weights;

save corpus to ram;

next;

set printer = "align";

set solver.integer = true;

print atoms;

solve;

print atoms;

print eval;
