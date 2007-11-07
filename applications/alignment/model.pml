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

index: reldist(*,*,_);
index: m1src2tgt(*,*,_);
index: m1tgt2src(*,*,_);
index: srchighestm1(*,*,_);
index: tgthighestm1(*,*,_);

hidden: align;
observed: source, target, reldist, srcchar, tgtchar, srccount, tgtcount,
  m1src2tgt, m1tgt2src, srchighestm1, tgthighestm1;

include "chi-eng.pml";