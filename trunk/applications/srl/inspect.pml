include "types.pml";
include "model.pml";

//set instancesCacheSize = 5;
set corpusCacheSize = 20;

load weights from dump "/tmp/srl.weights.dmp";
//load weights from dump "/tmp/epoch_6.dmp";

//set learner.solver.integer = true;
set solver.model.initIntegers = true;

load corpus from "corpora/dev-set.crp";

set printer = "conll05";

save corpus(0-400) to ram;

//test to ram;
