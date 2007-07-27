include "types.pml";
include "model.pml";

//set instancesCacheSize = 5;
set corpusCacheSize = 20;

//load weights from dump "/tmp/srl.weights.dmp";
load weights from dump "/tmp/epoch_10.dmp";

//set learner.solver.integer = true;
set solver.model.initIntegers = true;
set solver.model.solver.bbDepthLimit=200;

load corpus from "corpora/dev-set.crp";

save corpus(0-1000) to ram;

test to ram;
