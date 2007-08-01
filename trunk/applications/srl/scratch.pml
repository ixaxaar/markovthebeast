include "types.clean.pml";
include "model.pml";



//weight w_activeright : Predicate x Argument -> Double+;
//factor: for Predicate p, Int t, Argument a if pred(t,p,Active)
//  add [|Int c, Int b: arg(c,a) & span(c,b,_) & b > t | >= 1] * w_activeright(p,a);
//set collector.all.w_activeright = true;

weight w_activeright : Predicate -> Double+;
factor: for Predicate p, Int t if pred(t,p,Active)
  add [|Int c, Int b: candidate(c) & arg(c,A0) & span(c,b,_) & b > t | >= 1] * w_activeright(p);
set collector.all.w_activeright = true;

load corpus from "one-sentence.crp";
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
//set learner.solver.model.solver.bbDepthLimit=200;
set learner.maxCandidates = 1;
//set learner.useGreedy = false;
set learner.maxViolations = 1000;

learn for 10 epochs;

print weights.w_activeright;
print weights.w_positionvoice;
print weights.w_positionpredvoice;

//set evalrestrict.arg(*,"V") = true;
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

