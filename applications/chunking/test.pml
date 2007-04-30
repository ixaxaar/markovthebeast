include "corpora/train.types.pml";

include "model.pml";

load global from "global.txt";
load global.rare from "corpora/rare.txt";

set instancesCacheSize = 3;

load corpus from conll00 "corpora/test.conll";

load weights from dump "/tmp/weights.dmp";

set solver.ilp.solver = "lpsolve";
set solver.integer = true;
set solver.deterministicFirst = false;
set solver.maxIterations = 20;

test to ram;
