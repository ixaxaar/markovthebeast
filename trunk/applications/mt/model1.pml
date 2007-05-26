type SourceWord : "Ich","mag","Milch";
type TargetWord : "I", "me", "like", "love", "the", "milk";

//Source-Position x Source-Word
predicate source: Int x SourceWord;

//Source-Position x Group-ID
predicate group: Int x Int;

//Group-Id x Target-Id x TargetWord
predicate target: Int x Int x TargetWord;

//Group-Id x Translation-Prob
predicate groupScore: Int x Double;

//Target-Id x Target-Id x Follows-Prob
predicate followsScore: Int x Int x Double;

//Target-Id x Target-Id
predicate follows: Int x Int;

//Group-Id
predicate activeGroup: Int;

//Target-Id
predicate activeTarget: Int;



factor exactlyOneGroup: for Int i if source(i,_) :
  |Int group: group(i,group) & activeGroup(group)| <= 1;
set solver.ground.exactlyOneGroup = true;

factor groupImpliesTargetActive: for Int group, Int target if target(group,target,_):
  activeGroup(group) => activeTarget(target);
set solver.ground.groupImpliesTargetActive = true;


factor targetImpliesGroupActive: for Int group, Int target if target(group,target,_):
  activeTarget(target) => activeGroup(group);
set solver.ground.targetImpliesGroupActive = true;

//  (forall Int target: target(group,target) => activeTarget(target)) => activeGroup(group);

/*
factor: for Int begin if target(_,begin,_):
  activeTarget(begin) => |Int end: target(_,end,_) & follows(begin,end)| == 1;

factor: for Int end if target(_,end,_):
  activeTarget(end) => |Int begin: target(_,begin,_) & follows(begin,end)| == 1;
*/

factor followsActiveBegin: for Int begin, Int end if target(_,begin,_) & target(_,end,_) & followsScore(begin,end,_):
  follows(begin,end) => activeTarget(begin);
set solver.ground.followsActiveBegin = true;

factor followsActiveEnd: for Int begin, Int end if target(_,begin,_) & target(_,end,_) & followsScore(begin,end,_):
  follows(begin,end) => activeTarget(end);
set solver.ground.followsActiveEnd = true;


factor: follows acyclic;

factor: for Int begin, Int end, Double score if followsScore(begin,end,score)
  add [follows(begin,end)] * score;

factor: for Int group, Double score if groupScore(group,score)
  add [activeGroup(group)] * score;


