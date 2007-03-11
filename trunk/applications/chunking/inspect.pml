include "corpora/train.types.pml";

include "conll00.pml";
include "chunking.pml";
include "tagging.pml";
include "joint.pml";

set instancesCacheSize = 3;

load corpus from conll00 "corpora/test.conll";

load weights from dump "/tmp/weights.dmp";

save corpus (0-100) to ram;

set solver.ilp.solver = "lpsolve";
set solver.integer = true;
set solver.deterministicFirst = false;
set learner.solver.maxIterations = 10;
