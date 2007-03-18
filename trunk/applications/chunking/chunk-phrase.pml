//local word unigram based factors
weight w_lexical: Word x Chunk -> Double;
factor:
  for Int i, Chunk c, Word w_i
  if word(i,w_i) & !rare(w_i)
  add [chunk(i,i,c)] * w_lexical(w_i,c);