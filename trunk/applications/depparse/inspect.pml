include "corpora/train.types.pml";

include "model.pml";

set instancesCacheSize = 5;

load corpus from conll06 "test.conll";

save corpus (0-100) to ram;

load weights from dump "/tmp/depparse.weights.dmp";

set solver.profile = true;
set solver.maxIterations = 100;
set solver.integer = true;
set solver.model.solver.bbDepthLimit=200;