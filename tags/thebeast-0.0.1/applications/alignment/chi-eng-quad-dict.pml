weight w_char2char_down: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & charToChar(src,tgt) & charToChar(src,tgt+1) & src > 0 & tgt > 0
  add [aligndown(src,tgt)] * w_char2char_down;

weight w_char2char_diag: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & charToChar(src,tgt) & charToChar(src+1,tgt+1) & src > 0 & tgt > 0
  add [aligndiag(src,tgt)] * w_char2char_diag;

weight w_char2char_inv: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & charToChar(src,tgt) & charToChar(src-1,tgt+1) & src > 1 & tgt > 0
  add [aligninv(src,tgt)] * w_char2char_inv;


weight w_char2char_right: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & charToChar(src,tgt) & charToChar(src+1,tgt) & src > 0 & tgt > 0 
  add [alignright(src,tgt)] * w_char2char_right;

/*

weight w_word2word_down: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & wordToWord(src,tgt) & wordToWord(src,tgt+1) & src > 0 & tgt > 0
  add [aligndown(src,tgt)] * w_word2word_down;

weight w_word2word_diag: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & wordToWord(src,tgt) & wordToWord(src+1,tgt+1) & src > 0 & tgt > 0
  add [aligndiag(src,tgt)] * w_word2word_diag;

weight w_word2word_right: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & wordToWord(src,tgt) & wordToWord(src+1,tgt) & src > 0 & tgt > 0
  add [alignright(src,tgt)] * w_word2word_right;


*/