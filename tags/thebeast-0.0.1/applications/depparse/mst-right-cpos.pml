/* This file contains features a la cpos_h+1 cpos_h cpos_m cpos_m+1 */

//hp1_cpos, h_cpos, m_cpos, mp1_cpos
weight cpos_p1p1 : Cpos x Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hp1_cpos, Cpos mp1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos) & cpos(m+1,mp1_cpos)
  add [link(h,m)] * cpos_p1p1(h_cpos,m_cpos, hp1_cpos, mp1_cpos);

//weight cpos_p1p1_l : Cpos x Cpos x Cpos x Cpos x Dep -> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hp1_cpos, Cpos mp1_cpos, Dep label
//  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos) & cpos(m+1,mp1_cpos)
//  add [dep(h,m,label)] * cpos_p1p1_l(h_cpos,m_cpos, hp1_cpos, mp1_cpos, label);

weight cpos_p1p1_d : Cpos x Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hp1_cpos, Cpos mp1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos) & cpos(m+1,mp1_cpos)
  add [link(h,m)] * cpos_p1p1_d(h_cpos,m_cpos, hp1_cpos, mp1_cpos, bins(1,2,3,4,5,10,h-m));

//weight cpos_p1p1_l_d : Cpos x Cpos x Cpos x Cpos x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hp1_cpos, Cpos mp1_cpos, Dep label
//  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos) & cpos(m+1,mp1_cpos)
//  add [dep(h,m,label)] * cpos_p1p1_l_d(h_cpos,m_cpos, hp1_cpos, mp1_cpos, label, bins(1,2,3,4,5,10,h-m));