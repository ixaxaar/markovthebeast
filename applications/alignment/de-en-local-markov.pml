//using m1 src->tgt
weight w_m1srcprob_down: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2, Double p
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt+1,p2) & m1src2tgtprob(src,tgt+1, p)
  add [align(src,tgt)] * p * w_m1srcprob_down(p1,p2);

weight w_m1srcprob_up: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2, Double p
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt-1,p2) & m1src2tgtprob(src,tgt-1, p)
  add [align(src,tgt)] * p * w_m1srcprob_up(p1,p2);

weight w_m1srcprob_diag_down: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2, Double p
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt+1,p2) & m1src2tgtprob(src+1,tgt+1, p)
  add [align(src,tgt)] * p * w_m1srcprob_diag_down(p1,p2);

weight w_m1srcprob_diag_up: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2, Double p
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt-1,p2) & m1src2tgtprob(src-1,tgt-1, p)
  add [align(src,tgt)] * p * w_m1srcprob_diag_up(p1,p2);

weight w_m1srcprob_right: TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, Double p
  if source(src,_) & targetpos(tgt,p1) & m1src2tgtprob(src+1,tgt, p)
  add [align(src,tgt)] * p * w_m1srcprob_right(p1);

weight w_m1srcprob_left: TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, Double p
  if source(src,_) & targetpos(tgt,p1) & m1src2tgtprob(src-1,tgt, p)
  add [align(src,tgt)] * p * w_m1srcprob_left(p1);

//using m1 tgt->src
weight w_m1tgtprob_down: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2, Double p
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt+1,p2) & m1tgt2srcprob(src,tgt+1, p)
  add [align(src,tgt)] * p * w_m1tgtprob_down(p1,p2);

weight w_m1tgtprob_up: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2, Double p
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt-1,p2) & m1tgt2srcprob(src,tgt-1, p)
  add [align(src,tgt)] * p * w_m1tgtprob_up(p1,p2);

weight w_m1tgtprob_diag_down: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2, Double p
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt+1,p2) & m1tgt2srcprob(src+1,tgt+1, p)
  add [align(src,tgt)] * p * w_m1tgtprob_diag_down(p1,p2);

weight w_m1tgtprob_diag_up: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2, Double p
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt-1,p2) & m1tgt2srcprob(src-1,tgt-1, p)
  add [align(src,tgt)] * p * w_m1tgtprob_diag_up(p1,p2);

weight w_m1tgtprob_right: TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, Double p
  if source(src,_) &  targetpos(tgt,p1) & m1tgt2srcprob(src+1,tgt, p)
  add [align(src,tgt)] * p * w_m1tgtprob_right(p1);

weight w_m1tgtprob_left: TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, Double p
  if source(src,_) &  targetpos(tgt,p1) & m1tgt2srcprob(src-1,tgt, p)
  add [align(src,tgt)] * p * w_m1tgtprob_left(p1);

/*
//highest m1 scores
//using m1 src->tgt
weight w_m1srchighest_down: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt+1,p2) & srchighestm1(src,tgt+1, 0)
  add [align(src,tgt)] * w_m1srchighest_down(p1,p2);

weight w_m1srchighest_up: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt-1,p2) & srchighestm1(src,tgt-1, 0)
  add [align(src,tgt)] * w_m1srchighest_up(p1,p2);

weight w_m1srchighest_diag_down: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt+1,p2) & srchighestm1(src+1,tgt+1, 0)
  add [align(src,tgt)] * w_m1srchighest_diag_down(p1,p2);

weight w_m1srchighest_diag_up: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt-1,p2) & srchighestm1(src-1,tgt-1, 0)
  add [align(src,tgt)] * w_m1srchighest_diag_up(p1,p2);

weight w_m1srchighest_right: TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1
  if source(src,_) & targetpos(tgt,p1) & srchighestm1(src+1,tgt, 0)
  add [align(src,tgt)] * w_m1srchighest_right(p1);

weight w_m1srchighest_left: TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1
  if source(src,_) & targetpos(tgt,p1) & srchighestm1(src-1,tgt, 0)
  add [align(src,tgt)] * w_m1srchighest_left(p1);

//using m1 src->tgt
weight w_m1tgthighest_down: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt+1,p2) & tgthighestm1(src,tgt+1, 0)
  add [align(src,tgt)] * w_m1tgthighest_down(p1,p2);

weight w_m1tgthighest_up: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt-1,p2) & tgthighestm1(src,tgt-1, 0)
  add [align(src,tgt)] * w_m1tgthighest_up(p1,p2);

weight w_m1tgthighest_diag_down: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt+1,p2) & tgthighestm1(src+1,tgt+1, 0)
  add [align(src,tgt)] * w_m1tgthighest_diag_down(p1,p2);

weight w_m1tgthighest_diag_up: TargetPos x TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1, TargetPos p2
  if source(src,_) & targetpos(tgt,p1) & targetpos(tgt-1,p2) & tgthighestm1(src-1,tgt-1, 0)
  add [align(src,tgt)] * w_m1tgthighest_diag_up(p1,p2);

weight w_m1tgthighest_right: TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1
  if source(src,_) & targetpos(tgt,p1) & tgthighestm1(src+1,tgt, 0)
  add [align(src,tgt)] * w_m1tgthighest_right(p1);

weight w_m1tgthighest_left: TargetPos -> Double;
factor: for Int src, Int tgt, TargetPos p1
  if source(src,_) & targetpos(tgt,p1) & tgthighestm1(src-1,tgt, 0)
  add [align(src,tgt)] * w_m1tgthighest_left(p1);

*/











/*
//no tags
//using m1 src->tgt
weight w_m1srcprob_nt_down: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt+1, p)
  add [align(src,tgt)] * p * w_m1srcprob_nt_down;

weight w_m1srcprob_nt_up: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt-1, p)
  add [align(src,tgt)] * p * w_m1srcprob_nt_up;

weight w_m1srcprob_nt_diag_down: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src+1,tgt+1, p)
  add [align(src,tgt)] * p * w_m1srcprob_nt_diag_down;

weight w_m1srcprob_nt_diag_up: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src-1,tgt-1, p)
  add [align(src,tgt)] * p * w_m1srcprob_nt_diag_up;

weight w_m1srcprob_nt_right: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src+1,tgt, p)
  add [align(src,tgt)] * p * w_m1srcprob_nt_right;

weight w_m1srcprob_nt_left: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src-1,tgt, p)
  add [align(src,tgt)] * p * w_m1srcprob_nt_left;

//using m1 tgt->src
weight w_m1tgtprob_nt_down: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt+1, p)
  add [align(src,tgt)] * p * w_m1tgtprob_nt_down;

weight w_m1tgtprob_nt_up: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt-1, p)
  add [align(src,tgt)] * p * w_m1tgtprob_nt_up;

weight w_m1tgtprob_nt_diag_down: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src+1,tgt+1, p)
  add [align(src,tgt)] * p * w_m1tgtprob_nt_diag_down;

weight w_m1tgtprob_nt_diag_up: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src-1,tgt-1, p)
  add [align(src,tgt)] * p * w_m1tgtprob_nt_diag_up;

weight w_m1tgtprob_nt_right: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src+1,tgt, p)
  add [align(src,tgt)] * p * w_m1tgtprob_nt_right;

weight w_m1tgtprob_nt_left: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src-1,tgt, p)
  add [align(src,tgt)] * p * w_m1tgtprob_nt_left;
*/
