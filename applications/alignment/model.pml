predicate align: Int x Int;
predicate source: Int x SourceWord;
predicate target: Int x TargetWord;
predicate reldist: Int x Int x RelDistance;
predicate srcchar: Int x Int x SourceChar;
predicate tgtchar: Int x Int x TargetChar;
predicate srccount: Int x Int;
predicate tgtcount: Int x Int;
predicate m1src2tgt: Int x Int x M1Score;
predicate m1tgt2src: Int x Int x M1Score;
predicate srchighestm1: Int x Int x Int;
predicate tgthighestm1: Int x Int x Int;

//not used in this model
predicate srcfert: Int x Int;
predicate tgtfert: Int x Int;
predicate fertility: Int;
predicate m1srcnull: Int x M1Score;
predicate m1tgtnull: Int x M1Score;
predicate srcm1ranks: Int x Int x M1Score;
predicate tgtm1ranks: Int x Int x M1Score;
predicate alignsrcpair: Int x Int x Int;
predicate aligntgtpair: Int x Int x Int;

index: reldist(*,*,_);
index: m1src2tgt(*,*,_);
index: m1tgt2src(*,*,_);
index: srchighestm1(*,*,_);
index: tgthighestm1(*,*,_);

hidden: align;
observed: source, target, reldist, srcchar, tgtchar, srccount, tgtcount,
  m1src2tgt, m1tgt2src, srchighestm1, tgthighestm1;

factor [0]: for Int s if source(s,_) & s > 0: |Int t: target(t,_) & align(s,t)| <= 5;
factor [1]: for Int s if source(s,_) & s > 0: |Int t: target(t,_) & align(s,t)| >= 1;
factor [0]: for Int t if target(t,_) & t > 0: |Int s: source(s,_) & align(s,t)| <= 5;
factor [1]: for Int t if target(t,_) & t > 0: |Int s: source(s,_) & align(s,t)| >= 1;

factor [1]: for Int s, Int t if source(s,_) & target(t,_) & t > 0: !(align(s,0) & align(s,t));
factor [1]: for Int s, Int t if source(s,_) & target(t,_) & s > 0: !(align(0,t) & align(s,t));

//factor [complement,collect,positive](1):
//  for s,t,ws,wt add [[source(s,ws) & target(t,wt) => align(s,t)]] * w1(ws,wt);  
//factor [complement,collect](2): for s,t,ws,wt add [[source(s,ws) & target(t,wt) => align(s,t)]] * w2(ws,wt);
//factor srcnull: for Int s if source(s,_) : align(s,0) => |Int t: target(t,_) & align(s,t) & t > 0| <= 0;
//factor tgtnull: for Int t if target(t,_) : align(0,t) => |Int s: source(s,_) & align(s,t) & s > 0| <= 0;

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


  

include "chi-eng.pml";