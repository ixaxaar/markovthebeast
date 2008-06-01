include "types.clean.pml";
include "model-pairs.pml";

load corpus from $1;
//load corpus from "corpora/small-train-set.crp";

save corpus to ram;

collect;

types to "types.pml";

save corpus to instances "/tmp/srl.instances.dmp";

set learner.solver.integer = true;
set learner.loss = "avgF1";
//set learner.loss.restrict.arg(*,"V") = true;
set learner.solver.model.initIntegers = true;
set learner.solver.maxIterations = 10;
set learner.solver.model.solver.bbDepthLimit=5;


//set learner.average = true;

//set learner.solver.history = true;

//set learner.minOrder = 2;
set learner.maxCandidates=1;
set learner.minCandidates=1;

learn for 10 epochs;

//print weights.w_activeright;
//print weights.w_positionvoice;
//print weights.w_positionpredvoice;

//set evalrestrict.arg(*,"V") = true;

set solver.model.initIntegers = true;
set solver.model.solver.bbDepthLimit=5;

test to ram;

/*
next;

set evalrestrict.arg(*,"V") = true;

print atoms.arg;
solve;
print atoms.arg;

print eval;

next; solve; print eval;
next; solve; print eval;

//print weights.w_label;
*/

