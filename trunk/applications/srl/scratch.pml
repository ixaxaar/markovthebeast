include "types.clean.pml";
include "model.pml";

//weight w_activeright : Predicate x Argument -> Double+;
//factor: for Predicate p, Int t, Argument a if pred(t,p,Active)
//  add [|Int c, Int b: arg(c,a) & span(c,b,_) & b > t | >= 1] * w_activeright(p,a);
//set collector.all.w_activeright = true;

//weight w_activeright : Predicate -> Double+;
//factor: for Predicate p, Int t if pred(t,p,Active)
//  add [|Int c, Int b: candidate(c) & arg(c,A0) & span(c,b,_) & b > t | >= 1] * w_activeright(p);
//set collector.all.w_activeright = true;

load corpus from "corpora/one-sentence.crp";
//load corpus from "corpora/small-train-set.crp";

save corpus to ram;

collect;

types to "types.pml";

save corpus to instances "/tmp/srl.instances.dmp";

//set learner.solver.integer = true;
set learner.loss = "avgF1";
//set learner.loss.restrict.arg(*,"V") = true;
set learner.solver.model.initIntegers = true;
set learner.solver.maxIterations = 10;
set learner.solver.model.solver.bbDepthLimit=5;

set learner.solver.order.implyArg = 1;
set learner.solver.order.implyIsarg = 1;
set learner.solver.order.argpair = 2;
set learner.solver.order.argpairvoice = 2;

//set learner.average = true;

//set learner.solver.history = true;

set learner.minOrder = 2;
set learner.maxCandidates=10;
set learner.minCandidates=1;

learn for 10 epochs;

//print weights.w_activeright;
//print weights.w_positionvoice;
//print weights.w_positionpredvoice;

//set evalrestrict.arg(*,"V") = true;

set solver.model.initIntegers = true;
set solver.model.solver.bbDepthLimit=5;
set solver.order.implyArg = 1;
set solver.order.implyIsarg = 1;
set solver.order.argpair = 2;
set solver.order.argpairvoice = 2;

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

