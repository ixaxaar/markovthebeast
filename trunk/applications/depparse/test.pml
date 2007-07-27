include "corpora/train.types.pml";

include "model.pml";

set instancesCacheSize = 5;

load corpus from conll06 "test.conll";

load weights from dump "/tmp/depparse.weights.dmp";
//load weights from dump "/tmp/epoch_9.dmp";

set solver.profile = true;
set solver.maxIterations = 100;
set solver.integer = true;
set solver.model.solver.bbDepthLimit=200;

test to ram;

print learner.profiler;
