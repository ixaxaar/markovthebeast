//types from conll00 "corpora/train.np.goldtags.train.txt";
//types to "corpora/train.np.goldtags.train.pml";
include "corpora/train.np.goldtags.train.pml";
                                                                    
include "conll00.pml";
include "chunking.pml";
include "tagging.pml";
//include "global.pml";

include "pos-unigram.pml";
include "pos-unknowns.pml";
include "pos-pos.pml";
include "chunk-bigram.pml";
//include "chunk-phrase.pml";
include "chunk-pos.pml";
//include "chunk-chunk.pml";


observed:
  word, case, cardinal, hyphen, count, highestfreq,
  prefix1, prefix2, prefix3, prefix4, postfix1, postfix2, postfix3, postfix4;

hidden: chunk, pos;

set instancesCacheSize = 20;

//load global from "global.txt";

load corpus from conll00 "corpora/train.np.goldtags.train.txt";
//save corpus to dump "/tmp/corpus.dmp";
//load corpus from dump "/tmp/corpus.dmp";


save corpus (0-10) to ram;

//set collector.all.w_word = true;
//set collector.all.w_pos_2 = true;
//set collector.all.w_pos_3 = true;
set collector.all.w_forbid_1 = true;
set collector.all.w_forbid_2 = true;
//set collector.all.ch_word_3 = true;

collect;

save weights to dump "/tmp/weights.scratch.blank.dmp";

//load weights from dump "/tmp/weights.scratch.blank.dmp";

save corpus to instances "/tmp/chunk.inst.dmp";

//set learner.solver.maxIterations = 0;

//set learner.solver = "local";

//set learner.solver.ilp.solver = "lpsolve";
set learner.solver.ilp.solver = "osi";
set learner.solver.ilp.solver.implementation = "clp";
set learner.solver.maxIterations = 6;
set learner.solver.integer = false;
set learner.solver.deterministicFirst = true;
set learner.update = "mira";
set learner.update.signs = true;
set learner.maxCandidates = 100;
//set learner.loss = "avgF1";
set learner.loss = "avgNumErrors";
set learner.profile = true;

learn for 5 epochs;

//set learner.solver = "cut";

//learn for 10 epochs;

//print learner.profiler;

//load corpus from conll00 "corpora/test.conll";

//save corpus (0-100) to ram;

//set solver.ilp.solver = "lpsolve";
//set solver.integer = true;
//set solver.deterministicFirst = true;
//set learner.solver.maxIterations = 10;


//print weights.ch_pos_2;