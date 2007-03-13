include "pos-unigram.pml";
include "pos-unknowns.pml";
include "pos-pos.pml";
include "chunk-bigram.pml";
include "chunk-pos.pml";

observed:
  word, case, cardinal, hyphen, count,
  prefix1, prefix2, prefix3, prefix4, postfix1, postfix2, postfix3, postfix4;

hidden: chunk, pos;

global: cpos, verycoarse, rare;
