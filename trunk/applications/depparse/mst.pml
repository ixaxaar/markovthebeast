/* This file contains (approximately) the set of features implemented in Ryan McDonald MstParser 0.2
   There are a few differences:
   - instead of ordering head and modifier in terms of token position and conjoining
     a attachment-direction attribute to each feature we just leave features in their head-modifier order.
   - instead of only conjoining a single token with the edge label we use the label in almost all features
   - we don't use unigram features whatsoever (use coarser bigram features instead)   */

include "mst-between-pos.pml";
include "mst-unigram.pml";
include "mst-bigram.pml";
include "mst-outer-pos.pml";
include "mst-outer-cpos.pml";
include "mst-inner-pos.pml";
include "mst-inner-cpos.pml";
include "mst-left-pos.pml";
include "mst-left-cpos.pml";
include "mst-right-pos.pml";
include "mst-right-cpos.pml";
