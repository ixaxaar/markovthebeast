type SourceWord : ...;
type TargetWord : ...;

//Source-Position x Source-Word
predicate source: Int x SourceWord;

//Source-Position x Target-ID
predicate mapping: Int x Int;

//Target-Id x TargetWord x Translation-Prob
predicate target: Int x TargetWord x Double;

//Target-Id x Target-Id x Follows-Prob
predicate followsScore: Int x Int x Double;

//Target-Id x Target-Id
predicate follows: Int x Int;

//Target-Id
predicate activeTarget: Int;

index: follows(*,*);
index: followsScore(*,*,_);

factor atMostOneTarget: for Int i if source(i,_) :
  |Int target: mapping(i,target) & activeTarget(target)| <= 1;

factor atLeastOneTarget: for Int i if source(i,_) :
  |Int target: mapping(i,target) & activeTarget(target)| >= 1;

factor atLeastOneEnd: for Int begin if target(begin,_,_) & begin != 1 & begin >= 0:
  activeTarget(begin) => |Int end: target(end,_,_) & follows(begin,end) & followsScore(begin,end,_)| >= 1;

factor atMostOneEnd: for Int begin if target(begin,_,_):
  |Int end: follows(begin,end) & target(end,_,_) & followsScore(begin,end,_)| <= 1;

factor atLeastOneBegin: for Int end if target(end,_,_) & end > 0:
  activeTarget(end) => |Int begin: target(begin,_,_) & follows(begin,end)& followsScore(begin,end,_)| >= 1;

factor atMostOneBegin: for Int end if target(end,_,_):
  |Int begin: follows(begin,end) & target(begin,_,_) & followsScore(begin,end,_)| <= 1;
  

factor followsActiveBegin: for Int begin, Int end if followsScore(begin,end,_):
  follows(begin,end) => activeTarget(begin);

factor followsActiveEnd: for Int begin, Int end if followsScore(begin,end,_):
  follows(begin,end) => activeTarget(end);

factor acyclicity: follows acyclic;

factor: for Int begin, Int end, Double score if followsScore(begin,end,score)
  add [follows(begin,end)] * score;

factor: for Int target, Double score if target(target,_,score)
  add [activeTarget(target)] * score;


