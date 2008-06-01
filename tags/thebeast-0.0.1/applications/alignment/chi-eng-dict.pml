weight w_word2word: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & wordToWord(src,tgt) add [align(src,tgt)] * w_word2word;

weight w_word2char: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & wordToChar(src,tgt) add [align(src,tgt)] * w_word2char;

weight w_char2word: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & charToWord(src,tgt) add [align(src,tgt)] * w_char2word;

weight w_char2char: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & charToChar(src,tgt) add [align(src,tgt)] * w_char2char;

weight w_word2word_pos: SourcePos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp
  if sourcepos(src,sp) & targetpos(tgt,tp) & wordToWord(src,tgt) add [align(src,tgt)] * w_word2word_pos(sp,tp);

weight w_word2char_pos: SourcePos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp
  if sourcepos(src,sp) & targetpos(tgt,tp) & wordToChar(src,tgt) add [align(src,tgt)] * w_word2char_pos(sp,tp);

weight w_char2word_pos: SourcePos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp
  if sourcepos(src,sp) & targetpos(tgt,tp) & charToWord(src,tgt) add [align(src,tgt)] * w_char2word_pos(sp,tp);

weight w_char2char_pos: SourcePos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp
  if sourcepos(src,sp) & targetpos(tgt,tp) & charToChar(src,tgt) add [align(src,tgt)] * w_char2char_pos(sp,tp);

/*
weight w_word2word_pos_down: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt+1,p2) & wordToWord(src,tgt+1)
  add [align(src,tgt)] * w_word2word_pos_down(p1,p2);

weight w_word2word_pos_up: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt-1,p2) & wordToWord(src,tgt-1)
  add [align(src,tgt)] * w_word2word_pos_up(p1,p2);

*/
/*
weight w_word2word_down: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & wordToWord(src+1,tgt)
  add [align(src,tgt)] * w_word2word_down;

weight w_word2word_up: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & wordToWord(src-1,tgt)
  add [align(src,tgt)] * w_word2word_up;

weight w_word2word_right: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & wordToWord(src,tgt+1)
  add [align(src,tgt)] * w_word2word_right;

weight w_word2word_left: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & wordToWord(src,tgt-1) 
  add [align(src,tgt)] * w_word2word_left;

weight w_word2word_right_diag: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & wordToWord(src+1,tgt+1)
  add [align(src,tgt)] * w_word2word_right_diag;

weight w_word2word_left_diag: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & wordToWord(src-1,tgt-1)
  add [align(src,tgt)] * w_word2word_left_diag;
*/

