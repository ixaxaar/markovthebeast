weight w_reldist_down: Double-;
factor: for Int src, Int tgt, Double d
  if source(src,_) & target(tgt,_) & target(tgt+1,_) & reldistreal(tgt,src, d) & src > 0 & tgt > 0
  add [aligndown(tgt,src)] * abs(d) * w_reldist_down;

weight w_reldist_right: Double-;
factor: for Int src, Int tgt, Double d
  if source(src,_) & target(tgt,_) & source(src+1,_) & reldistreal(tgt,src, d) & src > 0 & tgt > 0
  add [alignright(tgt,src)] * abs(d) * w_reldist_right;

weight w_reldist_diag: Double-;
factor: for Int src, Int tgt, Double d
  if source(src,_) & target(tgt,_) & source(src+1,_) & source(tgt+1,_) & reldistreal(tgt,src, d) & src > 0 & tgt > 0
  add [aligndiag(tgt,src)] * abs(d) * w_reldist_diag;

weight w_reldist_inv: Double-;
factor: for Int src, Int tgt, Double d
  if source(src,_) & target(tgt,_) & source(src-1,_) & source(tgt+1,_) & reldistreal(tgt,src, d) & src > 1 & tgt > 0
  add [aligninv(tgt,src)] * abs(d) * w_reldist_inv;


weight w_m1dist_srcdown: Double;
factor: for Int src, Int tgt, Double p1, Double p2, Double d
  if source(src,_) & target(tgt,_) & reldistreal(src,tgt,d) & m1src2tgtprob(src,tgt, p1) & m1src2tgtprob(src,tgt+1, p2) & src > 0 & tgt > 0
  add [aligndown(src,tgt)] * p1 * p2 * (1.0 - d) * w_m1dist_srcdown;

weight w_m1dist_tgtdown: Double;
factor: for Int src, Int tgt, Double p1, Double p2, Double d
  if source(src,_) & target(tgt,_) & reldistreal(src,tgt,d) & m1tgt2srcprob(src,tgt, p1) & m1tgt2srcprob(src,tgt+1, p2)& src > 0 & tgt > 0
  add [aligndown(src,tgt)] * p1 * p2 * (1.0 - d) * w_m1dist_tgtdown;

weight w_m1dist_srcright: Double;
factor: for Int src, Int tgt, Double p1, Double p2, Double d
  if source(src,_) & target(tgt,_) & reldistreal(src,tgt,d) & m1src2tgtprob(src,tgt, p1) & m1src2tgtprob(src+1,tgt, p2)& src > 0 & tgt > 0
  add [alignright(src,tgt)] * p1 * p2 * (1.0 - d) * w_m1dist_srcright;

weight w_m1dist_tgtright: Double;
factor: for Int src, Int tgt, Double p1, Double p2, Double d
  if source(src,_) & target(tgt,_) & reldistreal(src,tgt,d) & m1tgt2srcprob(src,tgt, p1) & m1tgt2srcprob(src+1,tgt, p2)& src > 0 & tgt > 0
  add [alignright(src,tgt)] * p1 * p2 * (1.0 - d) * w_m1dist_tgtright;

weight w_m1dist_srcdiag: Double;
factor: for Int src, Int tgt, Double p1, Double p2, Double d
  if source(src,_) & target(tgt,_) & reldistreal(src,tgt,d) & m1src2tgtprob(src,tgt, p1) & m1src2tgtprob(src+1,tgt+1, p2)& src > 0 & tgt > 0
  add [aligndiag(src,tgt)] * p1 * p2 * (1.0 - d) * w_m1dist_srcdiag;

weight w_m1dist_tgtdiag: Double;
factor: for Int src, Int tgt, Double p1, Double p2, Double d
  if source(src,_) & target(tgt,_) & reldistreal(src,tgt,d) & m1tgt2srcprob(src,tgt, p1) & m1tgt2srcprob(src+1,tgt+1, p2)& src > 0 & tgt > 0
  add [aligndiag(src,tgt)] * p1 * p2 * (1.0 - d) * w_m1dist_tgtdiag;

weight w_m1dist_srcinv: Double;
factor: for Int src, Int tgt, Double p1, Double p2, Double d
  if source(src,_) & target(tgt,_) & reldistreal(src,tgt,d) & m1src2tgtprob(src,tgt, p1) & m1src2tgtprob(src-1,tgt+1, p2)& src > 1 & tgt > 0
  add [aligninv(src,tgt)] * p1 * p2 * (1.0 - d) * w_m1dist_srcinv;

weight w_m1dist_tgtinv: Double;
factor: for Int src, Int tgt, Double p1, Double p2, Double d
  if source(src,_) & target(tgt,_) & reldistreal(src,tgt,d) & m1tgt2srcprob(src,tgt, p1) & m1tgt2srcprob(src-1,tgt+1, p2)& src > 1 & tgt > 0
  add [aligninv(src,tgt)] * p1 * p2 * (1.0 - d) * w_m1dist_tgtinv;