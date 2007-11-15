weight w_bias: Double-;
factor: for Int src, Int tgt if source(src,_) & target(tgt,_) & src > 0 & tgt > 0
  add [align(src,tgt)] * w_bias;

weight w_bias_src_null: Double;
factor: for Int src if source(src,_) & src > 0
  add [align(src,0)] * w_bias_src_null;

weight w_bias_tgt_null: Double;
factor: for Int tgt if target(tgt,_) & tgt > 0
  add [align(tgt,0)] * w_bias_tgt_null;

weight w_word: SourceWord x TargetWord -> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw
  if source(src,sw) & target(tgt,tw) add [align(src,tgt)] * w_word(sw,tw);

weight w_word_undef: Double-;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw
  if source(src,sw) & target(tgt,tw) & undefined(w_word(sw,tw)) add [align(src,tgt)] * w_word_undef;

weight w_reldistreal_p: Double;
factor: for Int src, Int tgt, Double d
  if source(src,_) & target(tgt,_) & reldistreal(tgt,src, d) & dleq(0.0,d) add [align(tgt,src)] * d * w_reldistreal_p;

weight w_reldistreal_n: Double;
factor: for Int src, Int tgt, Double d
  if source(src,_) & target(tgt,_) & reldistreal(tgt,src, d) & dleq(d,0.0) add [align(tgt,src)] * d * w_reldistreal_n;

weight w_m1srcprob: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt, p) add [align(src,tgt)] * p * w_m1srcprob;

weight w_m1reldistsrc_p: Double;
factor: for Int src, Int tgt, Double p, Double d
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt, p) & reldistreal(src,tgt,d) & dleq(0.0,d)
  add [align(src,tgt)] * (p * d) * w_m1reldistsrc_p;

weight w_m1reldistsrc_n: Double;
factor: for Int src, Int tgt, Double p, Double d
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt, p) & reldistreal(src,tgt,d) & dleq(d,0.0)
  add [align(src,tgt)] * (p * d) * w_m1reldistsrc_n;

weight w_m1tgtprob: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p) add [align(src,tgt)] * p * w_m1tgtprob;

weight w_m1reldisttgt_p: Double;
factor: for Int src, Int tgt, Double p, Double d
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p) & reldistreal(src,tgt,d) & dleq(0.0,d)
  add [align(src,tgt)] * (p * d) * w_m1reldisttgt_p;

weight w_m1reldisttgt_n: Double;
factor: for Int src, Int tgt, Double p, Double d
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p) & reldistreal(src,tgt,d) & dleq(d,0.0)
  add [align(src,tgt)] * (p * d) * w_m1reldisttgt_n;


weight w_m1highestsrc: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & srchighestm1(src,tgt, 0) add [align(src,tgt)] * w_m1highestsrc;

weight w_m1highesttgt: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & tgthighestm1(src,tgt, 0) add [align(src,tgt)] * w_m1highesttgt;




