//types from conll06 "corpora/english_ptb_train.1of2.conll";
//types to "corpora/english_ptb_train.1of2.pml";

include "corpora/english_ptb_train.1of2.pml";
include "conll06.pml";
include "nonproj.pml";

hidden: link, dep;
observed: word, pos, cpos, prefix;

include "mst.pml";

set instancesCacheSize = 2;

load corpus from conll06 "corpora/english_ptb_train.1of2.conll";
//load corpus from dump "/tmp/test.dmp";
//save corpus (0-5) to dump "/tmp/test.dmp";
save corpus (0-100) to ram;
//collect;
//save weights to dump "/tmp/weights.blank.dmp";
load weights from dump "/tmp/weights.blank.dmp";
save corpus to instances "/tmp/instances.dmp";
load instances from dump "/tmp/instances.dmp";

set learner.average = true;
set learner.solver.maxIterations = 0;
learn for 5 epochs;
print learner.profiler;
//print weights;
//save corpus to instances "/tmp/instances.dmp";
//save corpus to instances "/disk/scratch/tmp/instances.dmp";
//save corpus to instances "/disk/home/dendrite/s0349492/tmp/instances.dmp";

//set solver.ilp.solver.verbose = false;
//set learner.numEpochs = 1;

//learn for 3 epochs;
//print learner.profiler;
//load weights from dump "/tmp/weights.dmp";

//learn;

//scores from "example.scores";
                                                             
//collect;

//learn 10;

//print weights;
//print atoms;
//learn 1;

//print weights;

//clear atoms;
//clear scores;

//solve 5;

//print atoms;

//print scores;

//greedy;

//print atoms;

//solve 10;

//learn 1;
//learn ;
//learn 1 for 10 epochs;
//average

//print atoms;