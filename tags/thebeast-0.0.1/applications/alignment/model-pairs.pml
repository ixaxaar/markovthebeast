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
index: srcm1ranks(*,*,_);
index: tgtm1ranks(*,*,_);
index: alignsrcpair(*,*,_);
index: aligntgtpair(*,*,_);


hidden: align, alignsrcpair, aligntgtpair;
observed: source, target, reldist, srcchar, tgtchar, srccount, tgtcount,
  m1src2tgt, m1tgt2src, srchighestm1, tgthighestm1,m1srcnull,m1tgtnull,srcm1ranks, tgtm1ranks;
global: fertility;
auxiliary: alignsrcpair, aligntgtpair;

load global from "global.atoms";

//making solutions small to begin with
//factor[0]: for Int s if source(s,_): |Int t: target(t,_) & align(s,t)| <= 4;
//factor[0]: for Int t if target(t,_): |Int s: source(s,_) & align(s,t)| <= 4;

//make pairs and alignment consistent
factor[1]: for Int s1, Int s2, Int t if source(s1,_) & source(s2,_) & target(t,_) & s2 > s1:
  align(s1,t) & align(s2,t) => alignsrcpair(s1,s2,t);

factor[1]: for Int s1, Int s2, Int t if source(s1,_) & source(s2,_) & target(t,_) & s2 > s1:
  alignsrcpair(s1,s2,t) => align(s2,t);

factor[1]: for Int s1, Int s2, Int t if source(s1,_) & source(s2,_) & target(t,_) & s2 > s1:
  alignsrcpair(s1,s2,t) => align(s1,t);

//make pairs and alignment consistent
factor[1]: for Int t1, Int t2, Int s if target(t1,_) & target(t2,_) & source(s,_) & t2 > t1:
  align(s,t1) & align(s,t2) => aligntgtpair(t1,t2,s);

factor[1]: for Int t1, Int t2, Int s if target(t1,_) & target(t2,_) & source(s,_) & t2 > t1:
  aligntgtpair(t1,t2,s) => align(s,t2);

factor[1]: for Int t1, Int t2, Int s if target(t1,_) & target(t2,_) & source(s,_) & t2 > t1:
  aligntgtpair(t1,t2,s) => align(s,t1);

include "chi-eng-pairs.pml";
