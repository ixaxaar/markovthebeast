include "conll00.pml";
include "chunking.pml";
//include "tagging.pml";
include "global.pml";

//include "pos-unigram.pml";
//include "pos-brill.pml";
//include "pos-unknowns.pml";
//include "pos-pos.pml";
//include "chunk-bigram.pml";
include "chunk-phrase.pml";
include "chunk-phrase-noisypos.pml";
//include "chunk-pos-forbid.pml";
//include "chunk-pos.pml";
//include "chunk-chunk.pml";

observed:
  word, case, cardinal, hyphen, count, highestfreq, firstname, lastname, orgname, company, placename, stopword,
  prefix1, prefix2, prefix3, prefix4, postfix1, postfix2, postfix3, postfix4, noisypos, noisycpos;

//hidden: chunk, pos;
hidden: chunk;

global: cpos, verycoarse, rare, brill;
