include "types.pml";
include "model.pml";

//set instancesCacheSize = 5;
set corpusCacheSize = 20;

load weights from dump "/tmp/srl.weights.dmp";
//load weights from dump "/tmp/epoch_29.dmp";

//set learner.solver.integer = true;
set solver.model.initIntegers = true;
set solver.model.solver.bbDepthLimit=200;

load corpus from "corpora/dev-set.crp";

save corpus(0-400) to ram;

test to ram;
