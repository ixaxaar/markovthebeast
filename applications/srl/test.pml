include "types.pml";
include "model.pml";


//set instancesCacheSize = 5;
set corpusCacheSize = 20;

//load weights from dump "/tmp/srl.weights.dmp";
load weights from dump $1;

//print weights.w_argpair;
//load weights from dump "/tmp/epoch_1.dmp";

//set learner.solver.integer = true;
set solver.model.initIntegers = true;
set solver.model.solver.bbDepthLimit=200;


set solver.order.implyArg = 1;
//set solver.order.atLeastOne = 2;
set solver.order.cargimpliesarg = 2;
set solver.order.rargimpliesarg = 2;
set solver.order.duplicatearg = 2;
//set solver.order.duplicatemod = 2;
//set solver.order.implyIsarg = 0;
//set solver.order.argpair = 2;
//set solver.order.argpairvoice = 2;
//set solver.order.argpairvoicepred = 2;
//set solver.order.duplicatearg = 2;

//load corpus from "corpora/dev-set.crp";
load corpus from "/disk/home/dendrite/s0349492/corpora/conll05/dev-set.crp";
//load corpus from "/disk/home/dendrite/s0349492/corpora/conll05/test-set-wsj.crp";
//save corpus(0-400) to ram;

//set evalrestrict.arg(*,'V') = true;

set eval = "F1 SRL";

test to ram;
