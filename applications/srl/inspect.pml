include "types.pml";
include "model-pairs.pml";

//load corpus from $1;
//load corpus from "one-sentence.atoms";
//load corpus from "dendrite/dev-set.atoms";
load corpus from "dendrite/small-train-set.atoms";

save corpus(0-10) to ram;

load weights from dump "/tmp/srl.clean.weights.dmp";

//set evalrestrict.arg(*,"V") = true;

//set solver.model.initIntegers = true;
//set solver.model.solver.bbDepthLimit=-50;

//test to ram;


