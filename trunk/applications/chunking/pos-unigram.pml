weight w_pos: Pos -> Double;
factor:
  for Int i, Pos p if word(i,_) add [pos(i,p)] * w_pos(p);

//local word unigram based factors
weight w_word: Word x Pos -> Double;
factor:
  for Int i, Pos p, Word w_i, Int c
  if word(i,w_i) & count(i,c) & c >= 5
  add [pos(i,p)] * w_word(w_i,p);

weight w_word_m1: Word x Pos -> Double;
factor:
  for Int i, Pos p, Word w_i_m1, Int c
  if word(i,_) & word(i-1,w_i_m1)& count(i,c) & c >= 5
  add [pos(i,p)] * w_word_m1(w_i_m1,p);

weight w_word_p1: Word x Pos -> Double;
factor:
  for Int i, Pos p, Word w_i_p1, Int c
  if word(i,_) & word(i+1,w_i_p1)& count(i,c) & c >= 5
  add [pos(i,p)] * w_word_p1(w_i_p1,p);

weight w_word_m2: Word x Pos -> Double;
factor:
  for Int i, Pos p, Word w_i_m1, Int c
  if word(i,_) & word(i-2,w_i_m1)& count(i,c) & c >= 5
  add [pos(i,p)] * w_word_m2(w_i_m1,p);

weight w_word_p2: Word x Pos -> Double;
factor:
  for Int i, Pos p, Word w_i_p1, Int c
  if word(i,_) & word(i+2,w_i_p1)& count(i,c) & c >= 5
  add [pos(i,p)] * w_word_p2(w_i_p1,p);  

