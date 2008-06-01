/* This file contains features a la cpos_oh+1 cpos_oh cpos_om cpos_om-1 and all 3 gram subfeatures */

//hp1_cpos, h_cpos, m_cpos, mm1_cpos
weight cpos_o1234 : Cpos x Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l-1, p2) & cpos(r+1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_o1234(p1,p2,p3,p4);

weight cpos_o1234_d : Cpos x Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l-1, p2) & cpos(r+1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_o1234_d(p1,p2,p3,p4, bins(0,1,2,3,4,5,10,h-m));

weight cpos_o123 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p3
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l-1, p2) & cpos(r+1, p3)
  add [link(h,m)] * cpos_o123(p1,p2,p3);

weight cpos_o123_d : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p3
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l-1, p2) & cpos(r+1, p3)
  add [link(h,m)] * cpos_o123_d(p1,p2,p3,bins(0,1,2,3,4,5,10,h-m));

weight cpos_o234 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p2, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l-1, p2) & cpos(r+1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_o234(p2,p3,p4);

weight cpos_o234_d : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p2, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l-1, p2) & cpos(r+1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_o234_d(p2,p3,p4, bins(0,1,2,3,4,5,10,h-m));

weight cpos_o134 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(r+1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_o134(p1,p3,p4);

weight cpos_o134_d : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(r+1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_o134_d(p1,p3,p4, bins(0,1,2,3,4,5,10,h-m));

//hp1_cpos, h_cpos, m_cpos, mm1_cpos
weight cpos_o124 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l-1, p2) & cpos(r,p4)
  add [link(h,m)] * cpos_o124(p1,p2,p4);

weight cpos_o124_d : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l-1, p2) & cpos(r,p4)
  add [link(h,m)] * cpos_o124_d(p1,p2,p4, bins(0,1,2,3,4,5,10,h-m));

