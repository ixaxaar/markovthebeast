weight w_bias: Double-;
factor: for Int src, Int tgt if source(src,_) & target(tgt,_)
  add [align(src,tgt)] * w_bias;

weight w_word: SourceWord x TargetWord -> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw
  if source(src,sw) & target(tgt,tw) add [align(src,tgt)] * w_word(sw,tw);

weight w_word_undef: Double-;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw
  if source(src,sw) & target(tgt,tw) & undefined(w_word(sw,tw)) add [align(src,tgt)] * w_word_undef;

weight w_reldistreal: Double;
factor: for Int src, Int tgt, Double d
  if source(src,_) & target(tgt,_) & reldistreal(tgt,src, d) add [align(tgt,src)] * d * w_reldistreal;

weight w_m1srcprob: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt, p) add [align(src,tgt)] * p * w_m1srcprob;

weight w_m1reldistsrc: Double;
factor: for Int src, Int tgt, Double p, Double d
  if source(src,_) & target(tgt,_) & m1src2tgtprob(src,tgt, p) & reldistreal(src,tgt,d)
  add [align(src,tgt)] * (p * d) * w_m1reldistsrc;

weight w_m1tgtprob: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p) add [align(src,tgt)] * p * w_m1tgtprob;

weight w_m1reldisttgt: Double;
factor: for Int src, Int tgt, Double p, Double d
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p) & reldistreal(src,tgt,d)
  add [align(src,tgt)] * (p * d) * w_m1reldisttgt;


weight w_m1highestsrc: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & srchighestm1(src,tgt, 0) add [align(src,tgt)] * w_m1highestsrc;

weight w_m1highesttgt: Double;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & tgthighestm1(src,tgt, 0) add [align(src,tgt)] * w_m1highesttgt;

weight w_m1nothighestsrc: Double-;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & !srchighestm1(src,tgt, 0) add [align(src,tgt)] * w_m1nothighestsrc;

weight w_m1nothighesttgt: Double-;
factor: for Int src, Int tgt
  if source(src,_) & target(tgt,_) & !tgthighestm1(src,tgt, 0) add [align(src,tgt)] * w_m1nothighesttgt;



