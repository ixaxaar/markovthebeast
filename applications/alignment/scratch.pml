type SourceWord: ... ;
type TargetWord: ... ;
type RelDistance: ... ;

predicate align: Int x Int;
predicate source: Int x SourceWord;
predicate target: Int x TargetWord;
predicate reldist: Int x Int x RelDistance;

index: reldist(*,*,_);

hidden: align;
observed: source, target, reldist;

weight w_bias: Double-;
factor: for Int src, Int tgt if source(src,_) & target(tgt,_)
  add [align(tgt,src)] * w_bias;

weight w_word: SourceWord x TargetWord -> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw
  if source(src,sw) & target(tgt,tw) add [align(tgt,src)] * w_word(sw,tw);

//set collector.all.w_word=true;


//weight w_reldist: RelDistance -> Double;
//factor: for Int src, Int tgt, RelDistance d
//  if source(src,_) & target(tgt,_) & reldist(tgt,src, d) add [align(tgt,src)] * w_reldist(d);

//set collector.all.w_reldist = true;

//weight w_dist: Int -> Double-;
//factor: for Int src, Int tgt if source(src,_) & target(tgt,_)
//  add [align(tgt,src)] * w_dist(tgt-src);

weight w_worddist: SourceWord x TargetWord x RelDistance -> Double;
factor: for Int src, Int tgt, SourceWord sw, TargetWord tw, RelDistance d
  if source(src,sw) & target(tgt,tw) & reldist(tgt,src, d) add [align(tgt,src)] * w_worddist(sw,tw,d);

factor cluster: for Int t1, Int t2, Int s1, Int s2
  if source(s1,_) & source(s2,_) & target(t1,_) & target(t2,_) & t2  > t1 & s2 > s1 :
  align(t1,s1) & align(t1,s2) & align(t2,s1) => align(t2,s2);



factor tgtchunk: for Int t1, Int t2, Int t3, Int s
  if target(t1,_) & target(t3,_) & target(t2,_) & t2 < t3 & t1 < t2:
  align(t1,s) & align(t3,s) => align(t2,s);

factor srcchunk: for Int s1, Int s2, Int s3, Int t
  if source(s1,_) & source(s3,_) & source(s2,_) & s2 < s3 & s1 < s2:
  align(t,s1) & align(t,s3) => align(t,s2);


//factor srcmaxsize: for Int s if source(s,_) : |Int t: target(t,_) & align(t,s)| <= 5;
factor srcminsize: for Int s if source(s,_) : |Int t: target(t,_) & align(t,s)| >= 1;
//factor tgtmaxsize: for Int t if target(t,_) : |Int s: source(s,_) & align(t,s)| <= 5;
factor tgtminsize: for Int t if target(t,_) : |Int s: source(s,_) & align(t,s)| >= 1;


load corpus from "example.atoms";

collect;

save weights to dump "/tmp/align.blank.weights";

save corpus to instances "/tmp/align.instances";

//set learner.solver.order.tgtchunk = 1;
//set learner.solver.order.srcchunk = 1;


set learner.update = "mira";
set learner.maxCandidates = 1;


learn for 9 epochs;

print weights;

save corpus to ram;

next;

set printer = "align";

set solver.integer = true;

print atoms;

solve;

print atoms;

print eval;