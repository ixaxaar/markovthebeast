/*
weight w_markov_down: Double+;
factor [1]: for Int t, Int s1
   if target(t,_) & targetpos(t+1,_) & source(s1,_)
   & s1 > 0 & t > 0
   add [align(s1,t) => align(s1,t+1)] * w_markov_down;
*/

weight w_markov_pos_down: TargetPos x TargetPos -> Double+;
factor [2]: for Int t, Int s1, TargetPos p1, TargetPos p2
   if targetpos(t,p1) & targetpos(t+1,p2) & source(s1,_)
   & s1 > 0 & t > 0 
   add [align(s1,t) => align(s1,t+1)] * w_markov_pos_down(p1,p2);

weight w_markov_pos_diag: TargetPos x TargetPos -> Double+;
factor [2]: for Int t, Int s1, TargetPos p1, TargetPos p2
   if targetpos(t,p1) & targetpos(t+1,p2) & source(s1,_) & source(s1+1,_)
   & s1 > 0 & t > 0
   add [align(s1,t) => align(s1+1,t+1)] * w_markov_pos_diag(p1,p2);

/*
weight w_markov_pos: TargetPos x TargetPos -> Double-;
factor [1]: for Int t, Int s1, Int s2, TargetPos p1, TargetPos p2
   if targetpos(t,p1) & targetpos(t+1,p2) & source(s1,_) & source(s2,_)
   & s1 > 0 & s2 > 0 & t > 0 & s2 != s1 + 1
   add [align(s1,t) & align(s2,t+1)] * abs(double(s2 - s1 - 1)) * w_markov_pos(p1,p2);

weight w_markov_null_next: TargetPos x TargetPos -> Double-;
factor [1]: for Int t, Int s1, TargetPos p1, TargetPos p2
   if targetpos(t,p1) & targetpos(t+1,p2) & source(s1,_) 
   & s1 > 0 & t > 0
   add [align(s1,t) & align(0,t+1)] * w_markov_null_next(p1,p2);

weight w_markov_null_prev: TargetPos x TargetPos -> Double-;
factor [1]: for Int t, Int s1, TargetPos p1, TargetPos p2
   if targetpos(t,p1) & targetpos(t+1,p2) & source(s1,_)
   & s1 > 0 & t > 0
   add [align(0,t) & align(s1,t+1)] * w_markov_null_prev(p1,p2);
*/



/*
weight w_markov: Double-;
factor [1]: for Int t, Int s1, Int s2
   if target(t,_) & target(t+1,_) & source(s1,_) & source(s2,_)
   & s1 > 0 & s2 > 0 & t > 0
   add [align(s1,t) & align(s2,t+1)] * abs(double(s2 - s1 - 1)) * w_markov;

weight w_markov_right: Int -> Double-;
factor [1]: for Int t, Int s1, Int s2
   if target(t,_) & target(t+1,_) & source(s1,_) & source(s2,_) & s2 > s1 + 1
   & s1 > 0 & s2 > 0 & t > 0
   add [align(s1,t) & align(s2,t+1)] * w_markov_right(bins(0,1,2,3,4,5,10,s2 - s1 - 1));

weight w_markov_left: Int -> Double-;
factor [1]: for Int t, Int s1, Int s2
   if target(t,_) & target(t+1,_) & source(s1,_) & source(s2,_) & s2 < s1 + 1
   & s1 > 0 & s2 > 0 & t > 0
   add [align(s1,t) & align(s2,t+1)] * w_markov_left(bins(0,1,2,3,4,5,10,s2 - s1 - 1));

*/
