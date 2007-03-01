/* This file contains features a la cpos_h-1 cpos_h cpos_m cpos_m+1 */

//hm1_cpos, h_cpos, m_cpos, mp1_cpos
weight cpos_m1p1 : Cpos x Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hm1_cpos, Cpos mp1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h-1,hm1_cpos) & cpos(m+1,mp1_cpos)
  add [link(h,m)] * cpos_m1p1(h_cpos,m_cpos, hm1_cpos, mp1_cpos);

weight cpos_m1p1_l : Cpos x Cpos x Cpos x Cpos x Dep -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hm1_cpos, Cpos mp1_cpos, Dep label
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h-1,hm1_cpos) & cpos(m+1,mp1_cpos)
  add [dep(h,m,label)] * cpos_m1p1_l(h_cpos,m_cpos, hm1_cpos, mp1_cpos, label);

weight cpos_m1p1_d : Cpos x Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hm1_cpos, Cpos mp1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h-1,hm1_cpos) & cpos(m+1,mp1_cpos)
  add [link(h,m)] * cpos_m1p1_d(h_cpos,m_cpos, hm1_cpos, mp1_cpos, bins(1,2,3,4,5,10,h-m));

weight cpos_m1p1_l_d : Cpos x Cpos x Cpos x Cpos x Dep x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hm1_cpos, Cpos mp1_cpos, Dep label
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h-1,hm1_cpos) & cpos(m+1,mp1_cpos)
  add [dep(h,m,label)] * cpos_m1p1_l_d(h_cpos,m_cpos, hm1_cpos, mp1_cpos, label, bins(1,2,3,4,5,10,h-m));

//h_cpos, m_cpos, mp1_cpos
weight cpos_m1p1_1 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos mp1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(m+1,mp1_cpos)
  add [link(h,m)] * cpos_m1p1_1(h_cpos,m_cpos, mp1_cpos);

weight cpos_m1p1_l_1 : Cpos x Cpos x Cpos x Dep -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos mp1_cpos, Dep label
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(m+1,mp1_cpos)
  add [dep(h,m,label)] * cpos_m1p1_l_1(h_cpos,m_cpos, mp1_cpos, label);

weight cpos_m1p1_d_1 : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos mp1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(m+1,mp1_cpos)
  add [link(h,m)] * cpos_m1p1_d_1(h_cpos,m_cpos, mp1_cpos, bins(1,2,3,4,5,10,h-m));

weight cpos_m1p1_l_d_1 : Cpos x Cpos x Cpos x Dep x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos mp1_cpos, Dep label
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(m+1,mp1_cpos)
  add [dep(h,m,label)] * cpos_m1p1_l_d_1(h_cpos,m_cpos, mp1_cpos, label, bins(1,2,3,4,5,10,h-m));

//h_cpos, m_cpos, hm1_cpos
weight cpos_m1p1_2 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hm1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h-1,hm1_cpos)
  add [link(h,m)] * cpos_m1p1_2(h_cpos,m_cpos, hm1_cpos);

weight cpos_m1p1_l_2 : Cpos x Cpos x Cpos x Dep -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hm1_cpos, Dep label
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h-1,hm1_cpos)
  add [dep(h,m,label)] * cpos_m1p1_l_2(h_cpos,m_cpos, hm1_cpos, label);

weight cpos_m1p1_d_2 : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hm1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h-1,hm1_cpos)
  add [link(h,m)] * cpos_m1p1_d_2(h_cpos,m_cpos, hm1_cpos, bins(1,2,3,4,5,10,h-m));

weight cpos_m1p1_l_d_2 : Cpos x Cpos x Cpos x Dep x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hm1_cpos, Dep label
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h-1,hm1_cpos)
  add [dep(h,m,label)] * cpos_m1p1_l_d_2(h_cpos,m_cpos, hm1_cpos, label, bins(1,2,3,4,5,10,h-m));

//hm1_cpos, m_cpos, mp1_cpos
weight cpos_m1p1_3 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Cpos mp1_cpos, Cpos m_cpos, Cpos hm1_cpos
  if cpos(m+1,mp1_cpos) & cpos(m,m_cpos) & cpos(h-1,hm1_cpos) & word(h,_)
  add [link(h,m)] * cpos_m1p1_3(mp1_cpos,m_cpos, hm1_cpos);

weight cpos_m1p1_l_3 : Cpos x Cpos x Cpos x Dep -> Double;
factor :
  for Int h, Int m, Cpos mp1_cpos, Cpos m_cpos, Cpos hm1_cpos, Dep label
  if cpos(m+1,mp1_cpos) & cpos(m,m_cpos) & cpos(h-1,hm1_cpos) & word(h,_)
  add [dep(h,m,label)] * cpos_m1p1_l_3(mp1_cpos,m_cpos, hm1_cpos, label);

weight cpos_m1p1_d_3 : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Cpos mp1_cpos, Cpos m_cpos, Cpos hm1_cpos
  if cpos(m+1,mp1_cpos) & cpos(m,m_cpos) & cpos(h-1,hm1_cpos) & word(h,_)
  add [link(h,m)] * cpos_m1p1_d_3(mp1_cpos,m_cpos, hm1_cpos, bins(1,2,3,4,5,10,h-m));

weight cpos_m1p1_l_d_3 : Cpos x Cpos x Cpos x Dep x Int -> Double;
factor :
  for Int h, Int m, Cpos mp1_cpos, Cpos m_cpos, Cpos hm1_cpos, Dep label
  if cpos(m+1,mp1_cpos) & cpos(m,m_cpos) & cpos(h-1,hm1_cpos) & word(h,_)
  add [dep(h,m,label)] * cpos_m1p1_l_d_3(mp1_cpos,m_cpos, hm1_cpos, label, bins(1,2,3,4,5,10,h-m));

//hm1_cpos, h_cpos, mp1_cpos
weight cpos_m1p1_4 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Cpos mp1_cpos, Cpos h_cpos, Cpos hm1_cpos
  if cpos(m+1,mp1_cpos) & cpos(h,h_cpos) & cpos(h-1,hm1_cpos) & word(m,_)
  add [link(h,m)] * cpos_m1p1_4(mp1_cpos,h_cpos, hm1_cpos);

weight cpos_m1p1_l_4 : Cpos x Cpos x Cpos x Dep -> Double;
factor :
  for Int h, Int m, Cpos mp1_cpos, Cpos h_cpos, Cpos hm1_cpos, Dep label
  if cpos(m+1,mp1_cpos) & cpos(h,h_cpos) & cpos(h-1,hm1_cpos) & word(m,_)
  add [dep(h,m,label)] * cpos_m1p1_l_4(mp1_cpos,h_cpos, hm1_cpos, label);

weight cpos_m1p1_d_4 : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Cpos mp1_cpos, Cpos h_cpos, Cpos hm1_cpos
  if cpos(m+1,mp1_cpos) & cpos(h,h_cpos) & cpos(h-1,hm1_cpos) & word(m,_)
  add [link(h,m)] * cpos_m1p1_d_4(mp1_cpos,h_cpos, hm1_cpos, bins(1,2,3,4,5,10,h-m));

weight cpos_m1p1_l_d_4 : Cpos x Cpos x Cpos x Dep x Int -> Double;
factor :
  for Int h, Int m, Cpos mp1_cpos, Cpos h_cpos, Cpos hm1_cpos, Dep label
  if cpos(m+1,mp1_cpos) & cpos(h,h_cpos) & cpos(h-1,hm1_cpos) & word(m,_)
  add [dep(h,m,label)] * cpos_m1p1_l_d_4(mp1_cpos,h_cpos, hm1_cpos, label, bins(1,2,3,4,5,10,h-m));
