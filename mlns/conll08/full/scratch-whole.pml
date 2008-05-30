include "../corpora/train.open.1000.types.pml";
include "model1.pml";

load corpus from "../corpora/train.open.1000.atoms";
//load corpus from "../data/train/train.open.atoms";
save corpus (0-100) to ram;


set collector.cutoff = 1;
//print collector;
collect;

//print weights;

save weights to dump "/tmp/conll08-clear.weights";

save corpus to instances "/tmp/conll08.dmp";

set learner.solver.model.initIntegers = false;
set learner.solver.maxIterations = 20;
//set learner.loss.restrict.role(*,*,'NONE') = true;
//set learner.minOrder = 1;

//print learner;

learn for 10 epochs;

save weights to dump "/tmp/conll08.weights";

next;
//set solver.model.initIntegers = true;
//solve;
//print eval;

print learner.profiler;
