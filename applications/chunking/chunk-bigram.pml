/*
//word_b, word_e
weight ch_word: Word x Word x Chunk -> Double;
factor:
  for Int b, Int e, Word w_b, Word w_e, Chunk c
  if word(b,w_b) & word(e,w_e) & e >= b
  add [chunk(b,e,c)] * ch_word(w_b,w_e,c);

weight ch_word_d: Word x Word x Chunk x Int-> Double;
factor:
  for Int b, Int e, Word w_b, Word w_e, Chunk c
  if word(b,w_b) & word(e,w_e) & e >= b
  add [chunk(b,e,c)] * ch_word_d(w_b,w_e,c,bins(1,2,3,4,5,10,e-b));

//word_b-1, word_e+1
weight ch_word_bm1ep1: Word x Word x Chunk -> Double;
factor:
  for Int b, Int e, Word w_bm1, Word w_ep1, Chunk c
  if word(b-1,w_bm1) & word(e+1,w_ep1) & e >= b
  add [chunk(b,e,c)] * ch_word_bm1ep1(w_bm1,w_ep1,c);

weight ch_word_bm1ep1_d: Word x Word x Chunk x Int -> Double;
factor:
  for Int b, Int e, Word w_bm1, Word w_ep1, Chunk c
  if word(b-1,w_bm1) & word(e+1,w_ep1) & e >= b
  add [chunk(b,e,c)] * ch_word_bm1ep1_d(w_bm1,w_ep1,c,bins(1,2,3,4,5,10,e-b));
*/