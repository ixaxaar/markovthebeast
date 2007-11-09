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

weight w_m1srcnull: M1Score -> Double;
factor: for Int src, Int tgt, M1Score s
  if source(src,_) & target(tgt,_) & m1src2tgt(src, 0, s) add [align(src,tgt)] * w_m1srcnull(s);
set collector.all.w_m1srcnull = true;

weight w_m1tgtnull: M1Score -> Double;
factor: for Int src, Int tgt, M1Score s
  if source(src,_) & target(tgt,_) & m1tgt2src(0,tgt, s) add [align(src,tgt)] * w_m1tgtnull(s);
set collector.all.w_m1tgtnull = true;

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

//src pairs
weight w_srcpairbias: Double;
factor: for Int i1, Int i2, Int i if source(i1,_) & source(i2,_) & target(i,_) & i2 > i1
  add[alignsrcpair(i1,i2,i)] * w_srcpairbias;

weight w_srcpairdist: Int -> Double;
factor: for Int i1, Int i2, Int i if source(i1,_) & source(i2,_) & target(i,_) & i2 > i1
  add[alignsrcpair(i1,i2,i)] * w_srcpairdist(bins(0,1,2,3,4,5,10,i2 - i1));

weight w_srcpairreldist: RelDistance x RelDistance -> Double;
factor: for Int i1, Int i2, Int i, RelDistance d1, RelDistance d2 if reldist(i1,i,d1) & reldist(i2,i,d2) & i2 > i1
  add[alignsrcpair(i1,i2,i)] * w_srcpairreldist(d1,d2);
set collector.all.w_srcpairreldist = true;

weight w_srcpairword1dist: SourceWord x Int -> Double;
factor: for Int i1, Int i2, Int i, SourceWord w1 if source(i1,w1) & source(i2,_) & target(i,_) & i2 > i1
  add[alignsrcpair(i1,i2,i)] * w_srcpairword1dist(w1, bins(0,1,2,3,4,5,10,i2 - i1));

weight w_srcpairword2dist: SourceWord x Int -> Double;
factor: for Int i1, Int i2, Int i, SourceWord w2 if source(i1,_) & source(i2,w2) & target(i,_) & i2 > i1
  add[alignsrcpair(i1,i2,i)] * w_srcpairword2dist(w2, bins(0,1,2,3,4,5,10,i2 - i1));

weight w_srcpairwordsdist: SourceWord x SourceWord x Int -> Double;
factor: for Int i1, Int i2, Int i, SourceWord w1, SourceWord w2 if source(i1,w1) & source(i2,w2) & target(i,_) & i2 > i1
  add[alignsrcpair(i1,i2,i)] * w_srcpairwordsdist(w1, w2, bins(0,1,2,3,4,5,10,i2 - i1));

weight w_srcpairm1sdist: M1Score x M1Score x Int -> Double;
factor: for Int i1, Int i2, Int i, M1Score w1, M1Score w2 if m1src2tgt(i1,i,w1) & m1src2tgt(i2,i,w2) & i2 > i1
  add[alignsrcpair(i1,i2,i)] * w_srcpairm1sdist(w1, w2, bins(0,1,2,3,4,5,10,i2 - i1));

weight w_srcpair3words: SourceWord x SourceWord x TargetWord -> Double;
factor: for Int i1, Int i2, Int i, SourceWord w1, SourceWord w2, TargetWord w
  if source(i1,w1) & source(i2,w2) & target(i,w) & i2 > i1
  add[alignsrcpair(i1,i2,i)] * w_srcpair3words(w1, w2, w);

weight w_srcpairw1t: SourceWord x TargetWord -> Double;
factor: for Int i1, Int i2, Int i, SourceWord w1, TargetWord w
  if source(i1,w1) & source(i2,_) & target(i,w) & i2 > i1
  add[alignsrcpair(i1,i2,i)] * w_srcpairw1t(w1, w);

weight w_srcpairw2t: SourceWord x TargetWord -> Double;
factor: for Int i1, Int i2, Int i, SourceWord w2, TargetWord w
  if source(i1,_) & source(i2,w2) & target(i,w) & i2 > i1
  add[alignsrcpair(i1,i2,i)] * w_srcpairw2t(w2, w);

weight w_srcpair3wordsdist: SourceWord x SourceWord x TargetWord x Int -> Double;
factor: for Int i1, Int i2, Int i, SourceWord w1, SourceWord w2, TargetWord w
  if source(i1,w1) & source(i2,w2) & target(i,w) & i2 > i1
  add[alignsrcpair(i1,i2,i)] * w_srcpair3wordsdist(w1, w2, w,bins(0,1,2,3,4,5,10,i2 - i1));


//tgt pairs
weight w_tgtpairbias: Double;
factor: for Int i1, Int i2, Int i if target(i1,_) & target(i2,_) & source(i,_) & i2 > i1
  add[aligntgtpair(i1,i2,i)] * w_tgtpairbias;

weight w_tgtpairdist: Int -> Double;
factor: for Int i1, Int i2, Int i if target(i1,_) & target(i2,_) & source(i,_) & i2 > i1
  add[aligntgtpair(i1,i2,i)] * w_tgtpairdist(bins(0,1,2,3,4,5,10,i2 - i1));

weight w_tgtpairreldist: RelDistance x RelDistance -> Double;
factor: for Int i1, Int i2, Int i, RelDistance d1, RelDistance d2 if reldist(i,i1,d1) & reldist(i,i2,d2) & i2 > i1
  add[aligntgtpair(i1,i2,i)] * w_tgtpairreldist(d1,d2);
set collector.all.w_srcpairreldist = true;

weight w_tgtpairword1dist: TargetWord x Int -> Double;
factor: for Int i1, Int i2, Int i, TargetWord w1 if target(i1,w1) & target(i2,_) & source(i,_) & i2 > i1
  add[aligntgtpair(i1,i2,i)] * w_tgtpairword1dist(w1, bins(0,1,2,3,4,5,10,i2 - i1));

weight w_tgtpairword2dist: TargetWord x Int -> Double;
factor: for Int i1, Int i2, Int i, TargetWord w2 if target(i1,_) & target(i2,w2) & source(i,_) & i2 > i1
  add[aligntgtpair(i1,i2,i)] * w_tgtpairword2dist(w2, bins(0,1,2,3,4,5,10,i2 - i1));

weight w_tgtpairwordsdist: TargetWord x TargetWord x Int -> Double;
factor: for Int i1, Int i2, Int i, TargetWord w1, TargetWord w2 if target(i1,w1) & target(i2,w2) & source(i,_) & i2 > i1
  add[aligntgtpair(i1,i2,i)] * w_tgtpairwordsdist(w1, w2, bins(0,1,2,3,4,5,10,i2 - i1));

weight w_tgtpairm1dist: M1Score x M1Score x Int -> Double;
factor: for Int i1, Int i2, Int i, M1Score w1, M1Score w2 if m1tgt2src(i,i1,w1) & m1tgt2src(i,i2,w2) & i2 > i1
  add[aligntgtpair(i1,i2,i)] * w_tgtpairm1dist(w1, w2, bins(0,1,2,3,4,5,10,i2 - i1));

weight w_tgtpair3words: TargetWord x TargetWord x SourceWord -> Double;
factor: for Int i1, Int i2, Int i, TargetWord w1, TargetWord w2, SourceWord w
  if target(i1,w1) & target(i2,w2) & source(i,w) & i2 > i1
  add[aligntgtpair(i1,i2,i)] * w_tgtpair3words(w1, w2, w);

weight w_tgtpairw1s: TargetWord x SourceWord -> Double;
factor: for Int i1, Int i2, Int i, TargetWord w1, SourceWord w
  if target(i1,w1) & target(i2,_) & source(i,w) & i2 > i1
  add[aligntgtpair(i1,i2,i)] * w_tgtpairw1s(w1, w);

weight w_tgtpairw2s: TargetWord x SourceWord -> Double;
factor: for Int i1, Int i2, Int i, TargetWord w2, SourceWord w
  if target(i1,_) & target(i2,w2) & source(i,w) & i2 > i1
  add[aligntgtpair(i1,i2,i)] * w_tgtpairw2s(w2, w);

weight w_tgtpair3wordsdist: TargetWord x TargetWord x SourceWord x Int -> Double;
factor: for Int i1, Int i2, Int i, TargetWord w1, TargetWord w2, SourceWord w
  if target(i1,w1) & target(i2,w2) & source(i,w) & i2 > i1
  add[aligntgtpair(i1,i2,i)] * w_tgtpair3wordsdist(w1, w2, w,bins(0,1,2,3,4,5,10,i2 - i1));
