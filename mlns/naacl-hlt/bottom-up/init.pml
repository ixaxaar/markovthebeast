include $1;
include "model1.pml";

load corpus from $2;
//load corpus from "../data/train/train.open.atoms";
//save corpus (0-100) to ram;


set collector.cutoff = 1;
//print collector;
collect;

//print weights;

save weights to dump "/tmp/conll08-clear.weights";

save corpus to instances "/tmp/conll08.dmp";
