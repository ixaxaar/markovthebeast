type SourceWord : "not",...;
type TargetWord : "nicht", ...;

predicate source: Int x SourceWord;
predicate inBag: Int x TargetWord;
predicate target: Int x TargetWord;
predicate follows: Int x TargetWord x Int x Target

factor: for Int i if source(i,_) : |TargetWord w: inBag(i,w) & target(i,w)| <= 1;
factor: for Int i, TargetWord t_i : target(i,t_i) => |Int j, TargetWord t_j: inBag(j,t_j) & follows(i,t_i,j,t_j)| == 1;
factor: for Int i, TargetWord t_i : target(i,t_i) => |Int j, TargetWord t_j: inBag(j,t_j) & follows(j,t_j,i,t_i)| == 1;

factor: for Int i, TargetWord t_i, Int j, TargetWord t_j if inBag(i,t_i) & inBag(j,t_j): follows(i,t_i,j,t_j) => target(i,t_i);
factor: for Int i, TargetWord t_i, Int j, TargetWord t_j if inBag(i,t_i) & inBag(j,t_j): follows(i,t_i,j,t_j) => target(j,t_j);

factor: follows is acyclic;

factor:
  for Int i, TargetWord t_i, Int j, TargetWord t_j, Double s
  if inBag(i,t_i) & inBag(j,t_j) & score_follows(i,t_i,j,t_j,s)
  add [follows(i,t_i,j,t_j)] * s;

factor:
  for Int i, TargetWord t_i, Double s
  if inBag(i,t_i) & score_target(i,t_i,s)
  add [target(i,t_i)] * s;

