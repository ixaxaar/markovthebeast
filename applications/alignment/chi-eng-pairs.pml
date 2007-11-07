weight w_bias: Double-;
factor: for Int src, Int tgt if source(src,_) & target(tgt,_)
  add [align(src,tgt)] * w_bias;

weight w_word: SourceWord x TargetWord -> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw
  if source(src,sw) & target(tgt,tw) add [align(src,tgt)] * w_word(sw,tw);

weight w_m1src: M1Score -> Double;
factor: for Int src, Int tgt, M1Score s
  if source(src,_) & target(tgt,_) & m1src2tgt(src,tgt, s) add [align(src,tgt)] * w_m1src(s);
set collector.all.w_m1src = true;

weight w_m1tgt: M1Score -> Double;
factor: for Int src, Int tgt, M1Score s
  if source(src,_) & target(tgt,_) & m1tgt2src(src,tgt, s) add [align(src,tgt)] * w_m1tgt(s);
set collector.all.w_m1tgt = true;

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

weight w_m1highestsrc: Int -> Double;
factor: for Int src, Int tgt, Int r
  if source(src,_) & target(tgt,_) & srchighestm1(src,tgt, r) add [align(src,tgt)] * w_m1highestsrc(r);

weight w_m1highesttgt: Int -> Double;
factor: for Int src, Int tgt, Int r
  if source(src,_) & target(tgt,_) & tgthighestm1(src,tgt, r) add [align(src,tgt)] * w_m1highesttgt(r);

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

weight w_reldist: RelDistance -> Double;
factor: for Int src, Int tgt, RelDistance d
  if source(src,_) & target(tgt,_) & reldist(tgt,src, d) add [align(tgt,src)] * w_reldist(d);

//pairs
weight w_srcpairbias: Double;
factor: for Int s1, Int s2 if source(s1,_) & source(s2,_) & s2 > s1
  add[alignsrcpair(s1,s2)] * w_srcpairbias;

weight w_srcpairdist: Int -> Double;
factor: for Int s1, Int s2 if source(s1,_) & source(s2,_) & s2 > s1
  add[alignsrcpair(s1,s2)] * w_srcpairdist(bins(0,1,2,3,4,5,10,s2 - s1));

weight w_srcpairword1dist: SourceWord x Int -> Double;
factor: for Int s1, Int s2, SourceWord w1 if source(s1,w1) & source(s2,_) & s2 > s1
  add[alignsrcpair(s1,s2)] * w_srcpairword1dist(w1, bins(0,1,2,3,4,5,10,s2 - s1));

weight w_srcpairword2dist: SourceWord x Int -> Double;
factor: for Int s1, Int s2, SourceWord w2 if source(s1,_) & source(s2,w2) & s2 > s1
  add[alignsrcpair(s1,s2)] * w_srcpairword2dist(w2, bins(0,1,2,3,4,5,10,s2 - s1));

weight w_srcpairwordsdist: SourceWord x SourceWord x Int -> Double;
factor: for Int s1, Int s2, SourceWord w1, SourceWord w2 if source(s1,w1) & source(s2,w2) & s2 > s1
  add[alignsrcpair(s1,s2)] * w_srcpairwordsdist(w1, w2, bins(0,1,2,3,4,5,10,s2 - s1));


