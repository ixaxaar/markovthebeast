include "../data/train/train.open.types.pml";
include "model1.pml";

load corpus from "../data/devel/devel.open.atoms";
//load corpus from "../data/train/train.open.atoms";
save corpus (0-100) to ram;


//load weights from dump "/tmp/conll08.weights";

set solver.model.initIntegers = true;

next;
//solve;
//print eval;

print learner.profiler;
