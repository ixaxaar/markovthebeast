include "../corpora/train.open.1000.types.pml";
include "model1.pml";

load corpus from "../corpora/devel.open.atoms";
//load corpus from "../data/train/train.open.atoms";
save corpus (0-100) to ram;


//load weights from dump "/tmp/conll08.weights";

set solver.model.initIntegers = true;

next;
//solve;
//print eval;

print learner.profiler;
