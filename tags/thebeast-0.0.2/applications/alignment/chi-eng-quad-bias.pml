weight w_bias_down: Double-;
factor: for Int src, Int tgt if source(src,_) & target(tgt,_) & target(tgt+1,_) & src > 0 & tgt > 0
  add [aligndown(src,tgt)] * w_bias_down;

weight w_bias_right: Double-;
factor: for Int src, Int tgt if source(src,_) & target(tgt,_) & source(src+1,_) & src > 0 & tgt > 0
  add [alignright(src,tgt)] * w_bias_right;

weight w_bias_diag: Double-;
factor: for Int src, Int tgt if source(src,_) & target(tgt,_) & source(src+1,_) & target(tgt+1,_) & src > 0 & tgt > 0
  add [aligndiag(src,tgt)] * w_bias_diag;

weight w_bias_inv: Double-;
factor: for Int src, Int tgt if source(src,_) & target(tgt,_) & source(src-1,_) & target(tgt+1,_) & src > 1 & tgt > 0
  add [aligninv(src,tgt)] * w_bias_inv;
