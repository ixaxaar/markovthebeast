//types from conll06 "corpora/english_ptb_train.1of2.conll";
//types to "corpora/english_ptb_train.1of2.pml";

include "corpora/english_ptb_train.1of2.pml";
include "conll06.pml";
include "nonproj.pml";

hidden: link, dep;
observed: word, pos, cpos;

include "mst.pml";

corpus from conll06 "corpora/english_ptb_train.1of2.conll" (0 - 20);

scores from "example.scores";

collect;

set solver.ilp.verbose = true;
set learner.solver.maxIterations = 0;
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