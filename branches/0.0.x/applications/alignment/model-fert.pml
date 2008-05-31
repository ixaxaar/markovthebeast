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

index: reldist(*,*,_);
index: m1src2tgt(*,*,_);
index: m1tgt2src(*,*,_);
index: srchighestm1(*,*,_);
index: tgthighestm1(*,*,_);

hidden: align, srcfert, tgtfert;
observed: source, target, reldist, srcchar, tgtchar, srccount, tgtcount,
  m1src2tgt, m1tgt2src, srchighestm1, tgthighestm1,m1srcnull,m1tgtnull,srcm1ranks, tgtm1ranks;
global: fertility;

load global from "global.atoms";

//ensure exactly one fertility
factor [ground-all]: for Int s if source(s,_): |Int f: srcfert(s,f) & fertility(f)| >=1;
factor [ground-all]: for Int s if source(s,_): |Int f: srcfert(s,f) & fertility(f)| <=1;
factor [ground-all]: for Int t if target(t,_): |Int f: tgtfert(t,f) & fertility(f)| >=1;
factor [ground-all]: for Int t if target(t,_): |Int f: tgtfert(t,f) & fertility(f)| <=1;

//ensure consistency between matrix and fertilities
factor [0]: for Int s, Int f if source(s,_) & fertility(f): srcfert(s,f) => |Int t: target(t,_) & align(s,t)| <= f;
factor [0]: for Int s, Int f if source(s,_) & fertility(f): srcfert(s,f) => |Int t: target(t,_) & align(s,t)| >= f;
factor [0]: for Int t, Int f if target(t,_) & fertility(f): tgtfert(t,f) => |Int s: source(s,_) & align(s,t)| <= f;
factor [0]: for Int t, Int f if target(t,_) & fertility(f): tgtfert(t,f) => |Int s: source(s,_) & align(s,t)| >= f;

include "chi-eng-fert.pml";
