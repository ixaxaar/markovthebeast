/* This file contains features a la cpos_h+1 cpos_h cpos_m cpos_m-1 and all 3 gram subfeatures */

//hp1_cpos, h_cpos, m_cpos, mm1_cpos
weight cpos_1234 : Cpos x Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l+1, p2) & cpos(r-1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_1234(p1,p2,p3,p4);

weight cpos_1234_d : Cpos x Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l+1, p2) & cpos(r-1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_1234_d(p1,p2,p3,p4, bins(0,1,2,3,4,5,10,h-m));

weight cpos_123 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p3
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l+1, p2) & cpos(r-1, p3)
  add [link(h,m)] * cpos_123(p1,p2,p3);

weight cpos_123_d : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p3
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l+1, p2) & cpos(r-1, p3)
  add [link(h,m)] * cpos_123_d(p1,p2,p3,bins(0,1,2,3,4,5,10,h-m));

weight cpos_234 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p2, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l+1, p2) & cpos(r-1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_234(p2,p3,p4);

weight cpos_234_d : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p2, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l+1, p2) & cpos(r-1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_234_d(p2,p3,p4, bins(0,1,2,3,4,5,10,h-m));

weight cpos_134 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(r-1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_134(p1,p3,p4);

weight cpos_134_d : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(r-1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_134_d(p1,p3,p4, bins(0,1,2,3,4,5,10,h-m));

//hp1_cpos, h_cpos, m_cpos, mm1_cpos
weight cpos_124 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l+1, p2) & cpos(r,p4)
  add [link(h,m)] * cpos_124(p1,p2,p4);

weight cpos_124_d : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l+1, p2) & cpos(r,p4)
  add [link(h,m)] * cpos_124_d(p1,p2,p4, bins(0,1,2,3,4,5,10,h-m));

