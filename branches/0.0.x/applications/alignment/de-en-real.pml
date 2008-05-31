weight w_bias: Double-;
factor: for Int src, Int tgt if source(src,_) & target(tgt,_) & src > 0 & tgt > 0
  add [align(src,tgt)] * w_bias;

weight w_bias_src_null: Double;
factor: for Int src if source(src,_) & src > 0
  add [align(src,0)] * w_bias_src_null;

weight w_bias_tgt_null: Double;
factor: for Int tgt if target(tgt,_) & tgt > 0
  add [align(0,tgt)] * w_bias_tgt_null;

weight w_word: SourceWord x TargetWord -> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw
  if source(src,sw) & target(tgt,tw) add [align(src,tgt)] * w_word(sw,tw);

weight w_nullhighestm1src: Double;
factor: for Int s, Double p
  if source(s,_) & s > 0 & srcm1ranks(s,0,p)
  add [align(s,0)] * p * w_nullhighestm1src;

weight w_nullhighestm1tgt: Double;
factor: for Int t, Double p
  if target(t,_) & t > 0 & tgtm1ranks(t,0,p)
  add [align(0,t)] * p * w_nullhighestm1tgt;


weight w_wordreldist: SourceWord x TargetWord -> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw, Double d
  if source(src,sw) & target(tgt,tw) & reldistreal(src,tgt,d) & src > 0 & tgt > 0
  add [align(src,tgt)] * abs(d) * w_wordreldist(sw,tw);


/*
weight w_wordreldist_p: SourceWord x TargetWord -> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw, Double d
  if source(src,sw) & target(tgt,tw) & reldistreal(src,tgt,d) & dleq(0.0,d) & src > 0 & tgt > 0
  add [align(src,tgt)] * d * w_wordreldist_p(sw,tw);

weight w_wordreldist_n: SourceWord x TargetWord -> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw, Double d
  if source(src,sw) & target(tgt,tw) & reldistreal(src,tgt,d) & dleq(d,0.0) & src > 0 & tgt > 0
  add [align(src,tgt)] * d * w_wordreldist_n(sw,tw);
*/


//weight w_srcchar: SourceChar x TargetWord -> Double;
//factor: for Int src, Int tgt, SourceChar sc, TargetWord tw
//  if source(src,_) & srcchar(src,_,sc) & target(tgt,tw)  & src > 0 add [align(src,tgt)] * w_srcchar(sc,tw);


weight w_word_undef: Double-;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw
  if source(src,sw) & target(tgt,tw) & undefined(w_word(sw,tw)) add [align(src,tgt)] * w_word_undef;

weight w_reldistreal: Double-;
factor: for Int src, Int tgt, Double d
  if source(src,_) & target(tgt,_) & reldistreal(src,tgt, d) add [align(src,tgt)] * abs(d) * w_reldistreal;

weight w_reldistreal_p: Double-;
factor: for Int src, Int tgt, Double d
  if source(src,_) & target(tgt,_) & reldistreal(src,tgt, d) & dleq(0.0,d) add [align(src,tgt)] * abs(d) * w_reldistreal_p;

weight w_reldistreal_n: Double-;
factor: for Int src, Int tgt, Double d
  if source(src,_) & target(tgt,_) & reldistreal(src,tgt, d) & dleq(d,0.0) add [align(src,tgt)] * abs(d) * w_reldistreal_n;


weight w_m1srcprob: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt, p) add [align(src,tgt)] * p * w_m1srcprob;

weight w_m1srcprobnull1: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src, 0, p) add [align(src,tgt)] * (1.0 - p) * w_m1srcprobnull1;

weight w_m1srcprobnull2: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1src2tgtprob(0, tgt, p) add [align(src,tgt)] * (1.0 - p) * w_m1srcprobnull2;

weight w_m1tgtprob: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p) add [align(src,tgt)] * p * w_m1tgtprob;

weight w_m1tgtprobnull1: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,0, p) add [align(src,tgt)] * (1.0 - p) * w_m1tgtprobnull1;

weight w_m1tgtprobnull2: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(0,tgt, p) add [align(src,tgt)] * (1.0 - p) * w_m1tgtprobnull2;


//check the m1 probability of next target word at same source position depending on the
//POS tag pair at target position


weight w_m1reldistsrc: Double;
factor: for Int src, Int tgt, Double p, Double d
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt, p) & reldistreal(src,tgt,d)
  add [align(src,tgt)] * (p * (1.0 - abs(d))) * w_m1reldistsrc;

weight w_m1reldisttgt: Double;
factor: for Int src, Int tgt, Double p, Double d
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p) & reldistreal(src,tgt,d) 
  add [align(src,tgt)] * (p * (1.0 - abs(d))) * w_m1reldisttgt;


weight w_m1highestsrc: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & srchighestm1(src,tgt, 0) add [align(src,tgt)] * w_m1highestsrc;

weight w_m1highesttgt: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & tgthighestm1(src,tgt, 0) add [align(src,tgt)] * w_m1highesttgt;




