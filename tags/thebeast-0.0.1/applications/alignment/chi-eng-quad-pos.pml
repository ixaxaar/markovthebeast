weight w_pos_down: SourcePos x TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp, TargetPos tp2
  if sourcepos(src,sp) & targetpos(tgt,tp) & targetpos(tgt+1,tp2) & tgt > 0 & src > 0
  add [aligndown(src,tgt)] * w_pos_down(sp,tp,tp2);

weight w_pos_diag: SourcePos x SourcePos x TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp, SourcePos sp2, TargetPos tp2
  if sourcepos(src,sp) & targetpos(tgt,tp) & targetpos(tgt+1,tp2) & sourcepos(src+1,sp2) & tgt > 0 & src > 0
  add [aligndiag(src,tgt)] * w_pos_diag(sp,sp2,tp, tp2);

weight w_pos_inv: SourcePos x SourcePos x TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp, SourcePos sp2, TargetPos tp2
  if sourcepos(src,sp) & targetpos(tgt,tp) & targetpos(tgt+1,tp2) & sourcepos(src-1,sp2) & tgt > 0 & src > 1
  add [aligninv(src,tgt)] * w_pos_inv(sp,sp2,tp, tp2);


weight w_pos_right: SourcePos x SourcePos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp, SourcePos sp2
  if sourcepos(src,sp) & targetpos(tgt,tp) & sourcepos(src+1,sp2) & tgt > 0 & src > 0
  add [alignright(src,tgt)] * w_pos_right(sp,sp2,tp);