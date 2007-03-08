//types from conll00 "corpora/train.np.goldtags.train.txt";
//types to "corpora/train.np.goldtags.train.pml";
include "corpora/train.np.goldtags.train.pml";
                                                                    
include "conll00.pml";
include "chunking.pml";
include "tagging.pml";

include "pos-word-unigram.pml";
include "pos-unknowns.pml";
include "pos-pos.pml";
include "chunk-bigram.pml";
include "chunk-pos.pml";
include "chunk-chunk.pml";

observed:
  word, case, cardinal, hyphen, count,
  prefix1, prefix2, prefix3, prefix4, postfix1, postfix2, postfix3, postfix4;

hidden: chunk, pos;

set instancesCacheSize = 3;

load corpus from conll00 "corpora/train.np.goldtags.train.txt";
//save corpus to dump "/tmp/corpus.dmp";
//load corpus from dump "/tmp/corpus.dmp";


save corpus (0-100) to ram;

collect;

save corpus to instances "/tmp/chunk.inst.dmp";

//set learner.solver.maxIterations = 0;

//set learner.solver = "local";

set learner.solver.ilp.solver = "lpsolve";
//set learner.solver.ilp.solver = "osi";
//set learner.solver.ilp.solver.implementation = "clp";
set learner.solver.maxIterations = 10;
set learner.solver.integer = true;
set learner.solver.deterministicFirst = true;
set learner.update = "mira";
set learner.maxCandidates = 10;

//learn for 2 epochs;

//set learner.solver = "cut";

learn for 5 epochs;

print learner.profiler;

//load corpus from conll00 "corpora/test.conll";

save corpus (0-10) to ram;

set solver.ilp.solver = "lpsolve";
set solver.integer = true;
set solver.deterministicFirst = true;
set learner.solver.maxIterations = 10;


//print weights.ch_pos_2;