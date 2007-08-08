include "types.pml";
include "model.pml";

//set instancesCacheSize = 5;
set corpusCacheSize = 20;

//load weights from dump "/tmp/srl.weights.dmp";
load weights from dump "/tmp/epoch_1.dmp";

//set learner.solver.integer = true;
set solver.model.initIntegers = true;

set solver.order.implyArg = 1;
//set solver.order.implyIsarg = 0;
//set solver.order.argpair = 2;
//set solver.order.argpairvoice = 2;
//set solver.order.duplicatearg = 2;
set solver.model.solver.bbDepthLimit=200;

//load corpus from "corpora/dev-set.crp";
//load corpus from "/disk/home/dendrite/s0349492/corpora/conll05/dev-set.crp";
load corpus from "/disk/home/dendrite/s0349492/corpora/conll05/small-train-set.crp";

set printer = "conll05";

save corpus(0-400) to ram;

//set evalrestrict.arg(*,'V') = true;

//test to ram;
