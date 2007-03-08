include "corpora/train.types.pml";

include "conll00.pml";
include "chunking.pml";
include "tagging.pml";
include "joint.pml";

set instancesCacheSize = 3;

load corpus from conll00 "corpora/train.conll";

load weights from dump "/tmp/blank.weights.dmp";

load instances from dump "/tmp/instances.dmp";

set learner.solver.ilp.solver = "osi";
set learner.solver.ilp.solver.implementation = "clp";
set learner.solver.maxIterations = 2;
set learner.update = "mira";
set learner.maxCandidates = 2;
set learner.average = true;

learn for 5 epochs;

save weights to dump "/tmp/weights.dmp";
