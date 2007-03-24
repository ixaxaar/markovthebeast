include "corpora/train.types.pml";

include "conll00.pml";
include "chunking.pml";
include "tagging.pml";
include "global.pml";
include "joint.pml";

load global from "global.txt";
load global.rare from "corpora/rare.txt";


set instancesCacheSize = 3;

load corpus from conll00 "corpora/train.conll";

save corpus (0-100) to ram;

//load weights from dump "/tmp/weights.dmp";
load weights from dump "/tmp/epoch_30.dmp";


set solver.ilp.solver = "lpsolve";
set solver.integer = true;
set solver.bbDepthLimit = 10;
set solver.deterministicFirst = false;
set solver.maxIterations = 10;
