types from conll00noisy "corpora/train.conll";
types to "corpora/train.types.pml";

include "model.pml";

load global from "global.txt";
load global.rare from "corpora/rare.txt";

load corpus from conll00noisy "corpora/train.conll";

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

set collector.all.w_word = true;
set collector.all.w_word_m1 = true;
set collector.all.w_word_p1 = true;
set collector.all.w_word_m2 = true;
set collector.all.w_word_p2 = true;
//set collector.all.w_pos_1 = true;
set collector.all.w_pos_2 = true;
set collector.all.w_pos_3 = true;
set collector.all.w_forbid_1 = true;
set collector.all.w_forbid_2 = true;

set collector.init = -2.0;
*/
//set collector.all.w_forbid_1 = true;
//set collector.all.w_forbid_2 = true;
//set collector.all.w_forbid_3 = true;
//set collector.all.w_forbid_3 = true;
//set collector.all.w_forbid_4 = true;
//set collector.all.w_forbid_5 = true;
//set collector.all.w_forbid_6 = true;
//set collector.all.w_pos_2 = true;
//set collector.all.w_pos_3 = true;


collect;

//save weights to dump "/tmp/blank.weights.dmp";
save weights to dump "/tmp/chunking.blank.weights.dmp";

save corpus to instances "/tmp/instances.dmp";
