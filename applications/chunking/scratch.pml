//types from conll00 "corpora/train.np.goldtags.train.txt";
//types to "corpora/train.np.goldtags.train.pml";
include "corpora/train.np.goldtags.train.pml";
                                                                    
include "conll00.pml";
include "chunking.pml";
include "tagging.pml";
include "global.pml";

include "pos-unigram.pml";
include "pos-brill.pml";
include "pos-unknowns.pml";
include "pos-pos.pml";
//include "chunk-bigram.pml";
//include "chunk-phrase.pml";
//include "chunk-pos.pml";
//include "chunk-chunk.pml";


observed:
  word, case, cardinal, hyphen, count, highestfreq,
  prefix1, prefix2, prefix3, prefix4, postfix1, postfix2, postfix3, postfix4;

hidden: chunk, pos;

global: cpos, verycoarse, rare, brill;

set instancesCacheSize = 20;

load global from "global.txt";
load global.brill from "brill.txt";
load global.rare from "corpora/rare.txt";

load corpus from conll00 "corpora/train.np.goldtags.train.txt";
//save corpus to dump "/tmp/corpus.dmp";
//load corpus from dump "/tmp/corpus.dmp";


save corpus (0-100) to ram;

/*
set collector.all.w_case = true;
set collector.all.w_hyphen = true;
set collector.all.w_cardinal = true;
set collector.all.w_prefix1 = true;
set collector.all.w_prefix2 = true;
set collector.all.w_prefix3 = true;
set collector.all.w_prefix4 = true;
set collector.all.w_postfix1 = true;
set collector.all.w_postfix2 = true;
set collector.all.w_postfix3 = true;
set collector.all.w_postfix4 = true;

//set collector.all.w_word = true;
//set collector.all.w_word_m1 = true;
//set collector.all.w_word_p1 = true;
//set collector.all.w_word_m2 = true;
//set collector.all.w_word_p2 = true;
//set collector.all.w_pos_1 = true;
//set collector.all.w_pos_2 = true;
//set collector.all.w_pos_3 = true;
set collector.all.w_forbid_1 = true;
set collector.all.w_forbid_2 = true;
//set collector.all.ch_word_3 = true;
*/
//set collector.init = -100.0;
//set collector.all.w_pos_2 = true;
//set collector.all.w_forbid_1 = true;
//set collector.all.w_forbid_2 = true;

collect;

save weights to dump "/tmp/weights.scratch.blank.dmp";

//load weights from dump "/tmp/weights.scratch.blank.dmp";

save corpus to instances "/tmp/chunk.inst.dmp";

//set learner.solver.maxIterations = 0;

//set learner.solver = "local";

set learner.solver.ilp.solver = "lpsolve";
//set learner.solver.ilp.solver = "cbc";
//set learner.solver.ilp.solver.maxNodes = 4;
//set learner.solver.ilp.solver.gap = 20.0;
//set learner.solver.ilp.solver.implementation = "cbc";
set learner.solver.maxIterations = 10;
set learner.solver.integer = true;
set learner.solver.deterministicFirst = false;
set learner.update = "mira";
set learner.update.signs = true;
set learner.maxCandidates = 10;
//set learner.loss = "avgF1";
set learner.loss = "avgNumErrors";
set learner.profile = true;
//set learner.penalizeGold = true;
set learner.maxViolations = 1;
//set learner.useGreedy = true;

//next; print atoms.brill;

//learn for 1 epochs;

//set learner.solver = "cut";

//learn for 10 epochs;

//print learner.profiler;

//load corpus from conll00 "corpora/test.conll";

//save corpus (0-100) to ram;

set solver.ilp.solver = "lpsolve";
//set solver.integer = true;
//set solver.deterministicFirst = true;
//set learner.solver.maxIterations = 10;


//print weights.ch_pos_2;