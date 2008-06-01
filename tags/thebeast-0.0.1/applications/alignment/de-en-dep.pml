weight w_monotonic: SourceDep x TargetDep -> Double+;
factor [1]: for Int s1, Int s2, Int t1, Int t2, SourceDep sd, TargetDep td
  if sourcehead(s1,s2) & sourcedep(s1,sd) & targethead(t1,t2) & targetdep(t1,td) & s1 > 0 & s2 > 0 & t2 > 0
  add [align(s1,t1) => align(s2,t2)] * w_monotonic(sd,td);

weight w_cross: SourceDep x TargetDep -> Double+;
factor [1]: for Int s1, Int s2, Int t1, Int t2, SourceDep sd, TargetDep td
  if sourcehead(s1,s2) & sourcedep(s1,sd) & targethead(t1,t2) & targetdep(t1,td) & s2 > 0 & t2 > 0
  add [align(s1,t2) => align(s2,t1)] * w_cross(sd,td);

weight w_twoToOne: SourceDep -> Double+;
factor [1]: for Int s1, Int s2, Int t1, SourceDep sd
  if sourcehead(s1,s2) & sourcedep(s1,sd) & target(t1,_) & s2 > 0
  add [align(s1,t1) => align(s2,t1)] * w_twoToOne(sd);

weight w_oneToTwo: TargetDep -> Double+;
factor [1]: for Int s1, Int t1, Int t2, TargetDep td
  if source(s1,_) & targethead(t1,t2) & targetdep(t1,td) & t2 > 0
  add [align(s1,t1) => align(s1,t2)] * w_oneToTwo(td);

weight w_srchead: SourceDep -> Double-;
factor [1]: for Int s1, Int s2, Int t1, Int t2, SourceDep sd
  if sourcehead(s1,s2) & sourcedep(s1,sd) & target(t1,_) & target(t2,_) & !targetdom(t2,t1)
  add [align(s1,t1) & align(s2,t2)] * w_srchead(sd);

set collector.all.w_srchead = true;

weight w_tgthead: TargetDep -> Double-;
factor [1]: for Int s1, Int s2, Int t1, Int t2, TargetDep td
  if source(s1,_) & source(s2,_) & targetdep(t1,td)  & targethead(t1,t2) & !sourcedom(s2,s1)
  add [align(s1,t1) & align(s2,t2)] * w_tgthead(td);

set collector.all.w_tgthead = true;  

/*
weight w_srchead: SourceDep -> Double-;
factor [1]: for Int s1, Int s2, Int t1, Int t2, SourceDep sd
  if sourcehead(s1,s2) & sourcedep(s1,sd) & target(t1,_) & target(t2,_) & !targethead(t1,t2)
  add [align(s1,t1) & align(s2,t2)] * w_srchead(sd);

weight w_tgthead: TargetDep -> Double-;
factor [1]: for Int s1, Int s2, Int t1, Int t2, TargetDep td
  if source(s1,_) & source(s2,_) & targetdep(t1,td)  & targethead(t1,t2) & !sourcehead(s1,s2)
  add [align(s1,t1) & align(s2,t2)] * w_tgthead(td);
*/

//factor [1]: for Int s1, Int s2, Int t1, Int t2
//  if source(s1,_) & source(s2,_)  & targethead(t1,t2) & !sourcehead(s1,s2):
//  !(align(s1,t1) & align(s2,t2));

//factor [1]: for Int s1, Int s2, Int t1, Int t2
//  if sourcehead(s1,s2) & target(t1,_) & target(t2,_) & !targethead(t1,t2):
//  !(align(s1,t1) & align(s2,t2));


/*
weight w_parent: TargetDep -> Double+;
factor [1]: for Int th, Int tm, Int s, TargetDep dep
   if targethead(tm,th) & source(s,_) & targetdep(tm,dep)
   add [align(tm,s) => align(th,s)] * w_parent(dep);
*/
