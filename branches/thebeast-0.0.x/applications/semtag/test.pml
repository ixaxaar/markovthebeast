include "model.pml";

//factor: for Int t1, Int t2 if t1 < t2: !(slot(t1,"TOLOC") & slot(t2,"TOLOC"));
//factor: for Int t1: !(slot(t1,"FROMLOC") & slot(t1+1,"FROMLOC"));


load corpus from "corpora/test.data";

load weights from dump "/tmp/epoch_4.dmp";
//load weights from dump "/tmp/semtag.trained.weights";

set solver.integer = true;

test to ram;