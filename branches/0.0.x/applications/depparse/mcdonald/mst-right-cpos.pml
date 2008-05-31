/* This file contains features a la cpos_h-1 cpos_h cpos_m cpos_m-1 */

//hp1_cpos, h_cpos, m_cpos, mm1_cpos
weight cpos_r1234 : Cpos x Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l+1, p2) & cpos(r+1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_r1234(p1,p2,p3,p4);

weight cpos_r1234_d : Cpos x Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Cpos p1, Cpos p2, Cpos p3, Cpos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  cpos(l, p1) & cpos(l+1, p2) & cpos(r+1, p3) & cpos(r,p4)
  add [link(h,m)] * cpos_r1234_d(p1,p2,p3,p4, bins(0,1,2,3,4,5,10,h-m));
