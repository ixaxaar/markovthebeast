/*
weight w_2nd: Pos x Pos x Pos -> Double-;
factor f_2nd:
  for Int h, Int m1, Int m2, Pos p_m1, Pos p_m2, Pos p_h
  if pos(m1,p_m1) & pos(m2, p_m2) & pos(h,p_h) & m1 < m2
  add [link(h,m1) & link(h,m2)] * w_2nd(p_m1,p_m2, p_h);

set collector.all.w_2nd = true;  

weight w_2nd_2: Pos x Pos -> Double-;
factor f_2nd_2:
  for Int h, Int m1, Int m2, Pos p_m1, Pos p_m2
  if pos(m1,p_m1) & pos(m2, p_m2) & m1 < m2
  add [link(h,m1) & link(h,m2)] * w_2nd_2(p_m1,p_m2);

set collector.all.w_2nd_2 = true;  
*/

weight w_2nd: Cpos x Cpos x Cpos -> Double-;
factor f_2nd:
  for Int h, Int m1, Int m2, Cpos p_m1, Cpos p_m2, Cpos p_h
  if cpos(m1,p_m1) & cpos(m2, p_m2) & cpos(h,p_h) & m1 < m2
  add [link(h,m1) & link(h,m2)] * w_2nd(p_m1,p_m2, p_h);

set collector.all.w_2nd = true;

weight w_2nd_2: Cpos x Cpos -> Double-;
factor f_2nd_2:
  for Int h, Int m1, Int m2, Cpos p_m1, Cpos p_m2
  if cpos(m1,p_m1) & cpos(m2, p_m2) & m1 < m2
  add [link(h,m1) & link(h,m2)] * w_2nd_2(p_m1,p_m2);

set collector.all.w_2nd_2 = true;
