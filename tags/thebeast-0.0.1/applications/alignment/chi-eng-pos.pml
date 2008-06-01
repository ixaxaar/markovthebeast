weight w_pos: SourcePos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp
  if sourcepos(src,sp) & targetpos(tgt,tp) add [align(src,tgt)] * w_pos(sp,tp);

weight w_pos_sm1: SourcePos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp
  if sourcepos(src-1,sp) & source(src,_) & targetpos(tgt,tp) add [align(src,tgt)] * w_pos_sm1(sp,tp);

weight w_pos_sp1: SourcePos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp
  if sourcepos(src+1,sp) &source(src,_) & targetpos(tgt,tp) add [align(src,tgt)] * w_pos_sp1(sp,tp);

weight w_pos_tm1: SourcePos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp
  if sourcepos(src,sp) &  targetpos(tgt-1,tp) & target(tgt,_) add [align(src,tgt)] * w_pos_tm1(sp,tp);

weight w_pos_tp1: SourcePos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp
  if sourcepos(src,sp) &  targetpos(tgt+1,tp) & target(tgt,_) add [align(src,tgt)] * w_pos_tp1(sp,tp);

weight w_pos_sm1_tm1: SourcePos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp
  if sourcepos(src-1,sp) & source(src,_) & targetpos(tgt-1,tp) & target(tgt,_)
  add [align(src,tgt)] * w_pos_sm1_tm1(sp,tp);

weight w_pos_sp1_tp1: SourcePos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp
  if sourcepos(src+1,sp) & source(src,_) & targetpos(tgt+1,tp) & target(tgt,_)
  add [align(src,tgt)] * w_pos_sp1_tp1(sp,tp);

weight w_pos_sp1_tp1_both: SourcePos x SourcePos x TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, SourcePos sp, TargetPos tp, SourcePos sp2, TargetPos tp2
  if sourcepos(src+1,sp) & sourcepos(src,sp2) & targetpos(tgt+1,tp) & targetpos(tgt,tp2)
  add [align(src,tgt)] * w_pos_sp1_tp1_both(sp,sp2,tp,tp2);
