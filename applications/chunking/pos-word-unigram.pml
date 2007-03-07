//local word unigram based factors
weight w_word: Word x Pos -> Double;
factor:
  for Int i, Pos p, Word w_i
  if word(i,w_i)
  add [pos(i,p)] * w_word(w_i,p);

weight w_word_m1: Word x Pos -> Double;
factor:
  for Int i, Pos p, Word w_i_m1
  if word(i,_) & word(i-1,w_i_m1)
  add [pos(i,p)] * w_word_m1(w_i_m1,p);

weight w_word_p1: Word x Pos -> Double;
factor:
  for Int i, Pos p, Word w_i_p1
  if word(i,_) & word(i+1,w_i_p1)
  add [pos(i,p)] * w_word_p1(w_i_p1,p);

weight w_word_m2: Word x Pos -> Double;
factor:
  for Int i, Pos p, Word w_i_m1
  if word(i,_) & word(i-2,w_i_m1)
  add [pos(i,p)] * w_word_m2(w_i_m1,p);

weight w_word_p2: Word x Pos -> Double;
factor:
  for Int i, Pos p, Word w_i_p1
  if word(i,_) & word(i+2,w_i_p1)
  add [pos(i,p)] * w_word_p2(w_i_p1,p);  

