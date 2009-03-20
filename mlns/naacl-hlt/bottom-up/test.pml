include "../corpora/train.open.1000.types.pml";
include "model1.pml";

load corpus from $1;
//load corpus from "../data/train/train.open.atoms";
save corpus (0-100) to ram;

load weights from dump $2;

set solver.model.initIntegers = true;
set solver.model.solver.timeout = 10;

test to printer $3;

//next;
//solve;
//print eval;

print learner.profiler;
