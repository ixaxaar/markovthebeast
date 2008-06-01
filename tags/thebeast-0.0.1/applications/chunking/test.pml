include "corpora/train.types.pml";

include "model.pml";

load global from "global.txt";
load global.rare from "corpora/rare.txt";

set instancesCacheSize = 3;

load corpus from conll00noisy "corpora/test.conll";

//load weights from dump "/tmp/chunking.weights.dmp";
load weights from dump $1;

//save corpus (0-100) to ram;

//set solver.ilp.solver = "lpsolve";
//set solver.integer = true;
//set solver.maxIterations = 20;
set solver.ilp.solver = "lpsolve";
set solver.integer = true;
set solver.bbDepthLimit = 10;
set solver.maxIterations = 10;

test to ram;
//test to ram;
