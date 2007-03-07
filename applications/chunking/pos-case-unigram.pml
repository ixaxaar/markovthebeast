//local case unigram based factors
weight w_case: Case x Pos -> Double;
factor:
  for Int i, Pos p, Case w_i
  if case(i,w_i)
  add [pos(i,p)] * w_case(w_i,p);
/*
weight w_case_m1: Case x Pos -> Double;
factor:
  for Int i, Pos p, Case w_i_m1
  if case(i,_) & case(i-1,w_i_m1)
  add [pos(i,p)] * w_case_m1(w_i_m1,p);

weight w_case_p1: Case x Pos -> Double;
factor:
  for Int i, Pos p, Case w_i_p1
  if case(i,_) & case(i+1,w_i_p1)
  add [pos(i,p)] * w_case_p1(w_i_p1,p);

weight w_case_m2: Case x Pos -> Double;
factor:
  for Int i, Pos p, Case w_i_m1
  if case(i,_) & case(i-2,w_i_m1)
  add [pos(i,p)] * w_case_m2(w_i_m1,p);

weight w_case_p2: Case x Pos -> Double;
factor:
  for Int i, Pos p, Case w_i_p1
  if case(i,_) & case(i+2,w_i_p1)
  add [pos(i,p)] * w_case_p2(w_i_p1,p);

*/