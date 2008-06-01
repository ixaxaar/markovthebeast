weight w_bias: Double-;
factor: for Int src, Int tgt if source(src,_) & target(tgt,_)
  add [align(src,tgt)] * w_bias;

weight w_word: SourceWord x TargetWord -> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw
  if source(src,sw) & target(tgt,tw) add [align(src,tgt)] * w_word(sw,tw);

//weight w_reldist: RelDistance -> Double;
//factor: for Int src, Int tgt, RelDistance d
//  if source(src,_) & target(tgt,_) & reldist(tgt,src, d) add [align(tgt,src)] * w_reldist(d);

weight w_reldistreal: RelDistance -> Double;
factor: for Int src, Int tgt, Double d
  if source(src,_) & target(tgt,_) & reldistreal(tgt,src, d) add [align(tgt,src)] * d * w_reldistreal;


//weight w_m1src: M1Score -> Double;
//factor: for Int src, Int tgt, M1Score s
//  if source(src,_) & target(tgt,_) & m1src2tgt(src,tgt, s) add [align(src,tgt)] * w_m1src(s);
//set collector.all.w_m1src = true;

weight w_m1srcprob: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1src2tgt(src,tgt, p) add [align(src,tgt)] * p * w_m1srcprob;

weight w_m1reldistsrc: Double;
factor: for Int src, Int tgt, Double p, Double d
  if source(src,_) & target(tgt,_) & m1src2tgt(src,tgt, p) & reldistreal(src,tgt,d)
  add [align(src,tgt)] * (p * d) * w_m1reldistsrc;

//weight w_m1tgt: M1Score -> Double;
//factor: for Int src, Int tgt, M1Score s
//  if source(src,_) & target(tgt,_) & m1tgt2src(src,tgt, s) add [align(src,tgt)] * w_m1tgt(s);
//set collector.all.w_m1tgt = true;

weight w_m1tgtprob: Double;
factor: for Int src, Int tgt, Double p
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p) add [align(src,tgt)] * p * w_m1tgtprob;

weight w_m1reldisttgt: Double;
factor: for Int src, Int tgt, Double p, Double d
  if source(src,_) & target(tgt,_) & m1tgt2srcprob(src,tgt, p) & reldistreal(src,tgt,d)
  add [align(src,tgt)] * (p * d) * w_m1tgtprob;


/*
weight w_m1tgtreldist: M1Score x RelDistance-> Double;
factor: for Int src, Int tgt, M1Score s, RelDistance d
  if source(src,_) & target(tgt,_) & m1tgt2src(src,tgt, s) & reldist(src,tgt,d)
  add [align(src,tgt)] * w_m1tgtreldist(s,d);
set collector.all.w_m1tgtreldist = true;

weight w_m1srcreldist: M1Score x RelDistance-> Double;
factor: for Int src, Int tgt, M1Score s, RelDistance d
  if source(src,_) & target(tgt,_) & m1src2tgt(src,tgt, s) & reldist(src,tgt,d)
  add [align(src,tgt)] * w_m1srcreldist(s,d);
set collector.all.w_m1srcreldist = true;
*/

weight w_m1highestsrc: Int -> Double;
factor: for Int src, Int tgt, Int r
  if source(src,_) & target(tgt,_) & srchighestm1(src,tgt, r) add [align(src,tgt)] * w_m1highestsrc(r);

weight w_m1highesttgt: Int -> Double;
factor: for Int src, Int tgt, Int r
  if source(src,_) & target(tgt,_) & tgthighestm1(src,tgt, r) add [align(src,tgt)] * w_m1highesttgt(r);

/*
weight w_m1tgtrankreldist: Int x RelDistance-> Double;
factor: for Int src, Int tgt, Int r, RelDistance d
  if source(src,_) & target(tgt,_) & tgthighestm1(src,tgt, r) & reldist(src,tgt,d)
  add [align(src,tgt)] * w_m1tgtrankreldist(r,d);
//set collector.all.w_m1tgtrankreldist = true;

weight w_m1srcrankreldist: Int x RelDistance-> Double;
factor: for Int src, Int tgt, Int r, RelDistance d
  if source(src,_) & target(tgt,_) & srchighestm1(src,tgt, r) & reldist(src,tgt,d)
  add [align(src,tgt)] * w_m1srcrankreldist(r,d);
//set collector.all.w_m1srcrankreldist = true;

set collector.all.w_reldist = true;
*/


