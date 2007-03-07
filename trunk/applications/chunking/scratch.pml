//types from conll00 "corpora/train.np.goldtags.train.txt";
//types to "corpora/train.np.goldtags.train.pml";
include "corpora/train.np.goldtags.train.pml";
                                                                    
include "conll00.pml";
include "chunking.pml";
include "tagging.pml";
include "joint.pml";

observed: word, prefix, postfix, case, cardinal;
hidden: chunk, pos;

set instancesCacheSize = 3;

load corpus from conll00 "corpora/train.np.goldtags.train.txt";

save corpus (0-10) to ram;

collect;

save corpus to instances "/tmp/chunk.inst.dmp";

//set learner.solver.maxIterations = 0;

//set learner.solver = "local";

set learner.solver.ilp.solver = "osi";
set learner.solver.ilp.solver.implementation = "clp";
set learner.solver.maxIterations = 1;
set learner.maxCandidates = 2;

learn for 10 epochs;

//set learner.solver = "cut";

//learn for 4 epochs;

print learner.profiler;

print weights.ch_pos_2;