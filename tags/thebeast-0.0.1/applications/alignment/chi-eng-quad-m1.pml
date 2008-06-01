weight w_m1srcdown: Double;
factor: for Int src, Int tgt, Double p1, Double p2
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt, p1) & m1src2tgtprob(src,tgt+1, p2)& src > 0 & tgt > 0
  add [aligndown(src,tgt)] * p1 * p2 * w_m1srcdown;

weight w_m1tgtdown: Double;
factor: for Int src, Int tgt, Double p1, Double p2
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p1) & m1tgt2srcprob(src,tgt+1, p2)& src > 0 & tgt > 0
  add [aligndown(src,tgt)] * p1 * p2 * w_m1tgtdown;

weight w_m1srcright: Double;
factor: for Int src, Int tgt, Double p1, Double p2
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt, p1) & m1src2tgtprob(src+1,tgt, p2)& src > 0 & tgt > 0
  add [alignright(src,tgt)] * p1 * p2 * w_m1srcright;

weight w_m1tgtright: Double;
factor: for Int src, Int tgt, Double p1, Double p2
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p1) & m1tgt2srcprob(src+1,tgt, p2)& src > 0 & tgt > 0
  add [alignright(src,tgt)] * p1 * p2 * w_m1tgtright;

weight w_m1srcdiag: Double;
factor: for Int src, Int tgt, Double p1, Double p2
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt, p1) & m1src2tgtprob(src+1,tgt+1, p2)& src > 0 & tgt > 0
  add [aligndiag(src,tgt)] * p1 * p2 * w_m1srcdiag;

weight w_m1srcinv: Double;
factor: for Int src, Int tgt, Double p1, Double p2
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt, p1) & m1src2tgtprob(src-1,tgt+1, p2)& src > 1 & tgt > 0
  add [aligninv(src,tgt)] * p1 * p2 * w_m1srcinv;


weight w_m1tgtdiag: Double;
factor: for Int src, Int tgt, Double p1, Double p2
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p1) & m1tgt2srcprob(src+1,tgt+1, p2)& src > 0 & tgt > 0
  add [aligndiag(src,tgt)] * p1 * p2 * w_m1tgtdiag;

weight w_m1tgtinv: Double;
factor: for Int src, Int tgt, Double p1, Double p2
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p1) & m1tgt2srcprob(src-1,tgt+1, p2)& src > 1 & tgt > 0
  add [aligninv(src,tgt)] * p1 * p2 * w_m1tgtinv;
