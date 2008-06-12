/* Predicate definitions */

// The words of the source string
predicate src_word: Int x SourceWord;

// The words of the target string
predicate tgt_word: Int x TargetWord;

// the true alignments: align(src,tgt) means that token src in the source is aligned
// to token tgt in the target string
predicate align: Int x Int;

// Word to word translation probabilities from IBM model 4
// Note that in practice it might make more sense to provide these probabilties
// one a instance-by-instance basis. This table could become very large and as is
// thebeast would keep it in memory.
predicate model4: SourceWord x TargetWord x Double;

/* Loading the MLN formulae (local and global ones) */
include "align-global.pml";
include "align-local.pml";

/* Defining which predicates are hidden, observed and global. Do not forget this! */
observed: src_word, tgt_word;
hidden: align;
global: model4;


