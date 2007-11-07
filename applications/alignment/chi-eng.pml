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

set collector.all.w_reldist = true;

factor srcmaxsize: for Int s if source(s,_) & s > 0: |Int t: target(t,_) & align(s,t)| <= 5;
factor srcminsize: for Int s if source(s,_) & s > 0: |Int t: target(t,_) & align(s,t)| >= 1;
factor tgtmaxsize: for Int t if target(t,_) & t > 0: |Int s: source(s,_) & align(s,t)| <= 5;
factor tgtminsize: for Int t if target(t,_) & t > 0: |Int s: source(s,_) & align(s,t)| >= 1;

//factor srcnull: for Int s if source(s,_) : align(s,0) => |Int t: target(t,_) & align(s,t) & t > 0| <= 0;
factor srcnull: for Int s, Int t if source(s,_) & target(t,_) & t > 0: !(align(s,0) & align(s,t));
//factor tgtnull: for Int t if target(t,_) : align(0,t) => |Int s: source(s,_) & align(s,t) & s > 0| <= 0;
factor tgtnull: for Int s, Int t if source(s,_) & target(t,_) & s > 0: !(align(0,t) & align(s,t));

/*
factor cluster: for Int t1, Int t2, Int s1, Int s2
  if source(s1,_) & source(s2,_) & target(t1,_) & target(t2,_) & t1 > 0 & t2 > 0 & s1 > 0 & s2 > 0 :
  align(s1,t1) & align(s2,t1) & align(s1,t2) => align(s2,t2);

factor tgtchunk: for Int t1, Int t2, Int t3, Int s
  if target(t1,_) & target(t3,_) & target(t2,_) & t2 < t3 & t1 < t2 & s > 0 & t1 > 0 :
  align(s,t1) & align(s,t3) => align(s,t2);

factor srcchunk: for Int s1, Int s2, Int s3, Int t
  if source(s1,_) & source(s3,_) & source(s2,_) & s2 < s3 & s1 < s2 & t > 0 & s1 > 0:
  align(s1,t) & align(s3,t) => align(s2,t);
*/

/*
weight w_srcpair: Int -> Double-;
factor srcpair: for Int s, Int t1, Int t2
  if source(s,_) & target(t1,_) & target(t2,_) & t1 < t2 & t1 > 0
  add [align(s,t1) & align(s,t2)] * w_srcpair(bins(0,1,2,3,4,5,10,t2-t1));
*/
//set collector.all.w_srcpair=true;

/*
weight w_srcmaxsize5: Double+;
factor srcmaxsize5: for Int s if source(s,_)
  add [|Int t: target(t,_) & align(t,s)| <= 5] * w_srcmaxsize5;

weight w_srcmaxsize1: Double+;
factor srcmaxsize1: for Int s if source(s,_)
  add [|Int t: target(t,_) & align(t,s)| <= 1] * w_srcmaxsize1;

weight w_srcmaxsize2: Double+;
factor srcmaxsize2: for Int s if source(s,_)
  add [|Int t: target(t,_) & align(t,s)| <= 2] * w_srcmaxsize2;

weight w_srcmaxsize3: Double+;
factor srcmaxsize3: for Int s if source(s,_)
  add [|Int t: target(t,_) & align(t,s)| <= 3] * w_srcmaxsize3;

weight w_srcmaxsize4: Double+;
factor srcmaxsize4: for Int s if source(s,_)
  add [|Int t: target(t,_) & align(t,s)| <= 4] * w_srcmaxsize4;


set collector.all.w_srcmaxsize1=true;
set collector.all.w_srcmaxsize2=true;
set collector.all.w_srcmaxsize3=true;
set collector.all.w_srcmaxsize4=true;
set collector.all.w_srcmaxsize5=true;
*/

//weight w_dist: Int -> Double-;
//factor: for Int src, Int tgt if source(src,_) & target(tgt,_)
//  add [align(tgt,src)] * w_dist(tgt-src);
/*
weight w_worddist: SourceWord x TargetWord x RelDistance -> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw, RelDistance d
  if source(src,sw) & target(tgt,tw) & reldist(tgt,src, d) add [align(tgt,src)] * w_worddist(sw,tw,d);
*/
/*
factor cluster: for Int t1, Int t2, Int s1, Int s2
  if source(s1,_) & source(s2,_) & target(t1,_) & target(t2,_) & t2  > t1 & s2 > s1 :
  align(t1,s1) & align(t1,s2) & align(t2,s1) => align(t2,s2);
*/




