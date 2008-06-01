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

//fertility
//source words
weight w_srcfertbias: Int -> Double;
factor: for Int s, Int f if source(s,_) add [srcfert(s,f)] * w_srcfertbias(f);

weight w_srcfertword: SourceWord x Int -> Double;
factor: for Int s, Int f, SourceWord w if source(s,w) add [srcfert(s,f)] * w_srcfertword(w,f);

weight w_srcfertword_p1: SourceWord x Int -> Double;
factor: for Int s, Int f, SourceWord w if source(s+1,w) add [srcfert(s,f)] * w_srcfertword_p1(w,f);

weight w_srcfertword_m1: SourceWord x Int -> Double;
factor: for Int s, Int f, SourceWord w if source(s-1,w) add [srcfert(s,f)] * w_srcfertword_m1(w,f);

weight w_srcm1null: M1Score -> Double;
factor: for Int s, M1Score m if source(s,_) & m1srcnull(s,m) add [srcfert(s,0)] * w_srcm1null(m);

weight w_srcfertrank: Int x Int x M1Score -> Double;
factor: for Int s, M1Score p, Int r, Int f if source(s,_) & srcm1ranks(s,r,p) & fertility(f)
  add [srcfert(s,f)] * w_srcfertrank(f,r,p);

//target words
weight w_tgtfertbias: Int -> Double;
factor: for Int t, Int f if target(t,_) add [tgtfert(t,f)] * w_tgtfertbias(f);

weight w_tgtfertword: TargetWord x Int -> Double;
factor: for Int t, Int f, TargetWord w if target(t,w) add [tgtfert(t,f)] * w_tgtfertword(w,f);

weight w_tgtfertword_p1: TargetWord x Int -> Double;
factor: for Int t, Int f, TargetWord w if target(t,w) add [tgtfert(t+1,f)] * w_tgtfertword_p1(w,f);

weight w_tgtfertword_m1: TargetWord x Int -> Double;
factor: for Int t, Int f, TargetWord w if target(t,w) add [tgtfert(t-1,f)] * w_tgtfertword_m1(w,f);

weight w_tgtm1null: M1Score -> Double;
factor: for Int t, M1Score m if target(t,_) & m1tgtnull(t,m) add [tgtfert(t,0)] * w_tgtm1null(m);

weight w_tgtfertrank: Int x Int x M1Score -> Double;
factor: for Int t, M1Score p, Int r, Int f if target(t,_) & tgtm1ranks(t,r,p) & fertility(f)
  add [tgtfert(t,f)] * w_tgtfertrank(f,r,p);






