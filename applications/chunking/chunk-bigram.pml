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
