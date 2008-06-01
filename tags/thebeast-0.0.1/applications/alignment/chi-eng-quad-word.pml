weight w_word_down: SourceWord x TargetWord x TargetWord-> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw, TargetWord tw2
  if source(src,sw) & target(tgt,tw)  & target(tgt+1,tw2) & src > 0 & tgt > 0
  add [aligndown(src,tgt)] * w_word_down(sw,tw,tw2);

//weight w_word_down_undef: Double-;
//factor: for Int src, Int tgt, SourceWord sw, TargetWord tw, TargetWord tw2
//  if source(src,sw) & target(tgt,tw) & target(tgt+1,tw2) & undefined(w_word_down(sw,tw,tw2)) & src > 0 & tgt > 0
//  add [aligndown(src,tgt)] * w_word_down_undef;

weight w_word_right: SourceWord x SourceWord x TargetWord-> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw, SourceWord sw2
  if source(src,sw) & target(tgt,tw) & source(src+1,sw2) & src > 0 & tgt > 0
  add [alignright(src,tgt)] * w_word_right(sw,sw2,tw);

//weight w_word_right_undef: Double-;
//factor: for Int src, Int tgt, SourceWord sw, TargetWord tw, SourceWord sw2
//  if source(src,sw) & target(tgt,tw) & source(src+1,sw2) & undefined(w_word_right(sw,sw2,tw)) & src > 0 & tgt > 0
//  add [alignright(src,tgt)] * w_word_right_undef;

weight w_word_diag: SourceWord x SourceWord x TargetWord x TargetWord-> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw, SourceWord sw2, TargetWord tw2
  if source(src,sw) & target(tgt,tw) & source(src+1,sw2) & target(tgt+1,tw2) & src > 0 & tgt > 0
  add [aligndiag(src,tgt)] * w_word_diag(sw,sw2,tw,tw2);

weight w_word_inv: SourceWord x SourceWord x TargetWord x TargetWord-> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw, SourceWord sw2, TargetWord tw2
  if source(src,sw) & target(tgt,tw) & source(src-1,sw2) & target(tgt+1,tw2) & src > 1 & tgt > 0
  add [aligninv(src,tgt)] * w_word_inv(sw,sw2,tw,tw2);


//weight w_word_diag_undef: Double-;
//factor: for Int src, Int tgt, SourceWord sw, TargetWord tw, SourceWord sw2, TargetWord tw2
//  if source(src,sw) & target(tgt,tw) & source(src+1,sw2) & target(tgt+1,tw2) & undefined(w_word_diag(sw,sw2,tw,tw2)) & src > 0 & tgt > 0
//  add [aligndiag(src,tgt)] * w_word_diag_undef;

