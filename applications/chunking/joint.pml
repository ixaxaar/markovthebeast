include "pos-word-unigram.pml";
include "pos-case-unigram.pml";
//include "pos-etc.pml";

include "chunk-bigram.pml";

/* this one is very inefficient (because it would look for possible token pairs and
   most of them will have chunk over them).
weight ch_pos_d: Pos x Pos x Chunk x Int -> Double+;
factor:
  for Int b, Int e, Pos p_b, Pos p_e, Chunk c
  if e > b
  add [pos(b,p_b) & pos(e,p_e) => chunk(b,e,c)] * ch_pos_d(p_b,p_e,c, bins(1,2,3,4,5,10,e-b));
*/

weight ch_pos_2: Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p_1, Pos p_2, Chunk c
  add [pos(t,p_1) & pos(t+1,p_2) => chunk(t,t+1,c)] * ch_pos_2(p_1,p_2,c);

