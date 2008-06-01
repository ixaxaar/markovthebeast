/* This file contains features a la cpos_h+1 cpos_h cpos_m cpos_m-1 */

//hp1_cpos, h_cpos, m_cpos, mm1_cpos
weight cpos_p1m1 : Cpos x Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hp1_cpos, Cpos mm1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos) & cpos(m-1,mm1_cpos)
  add [link(h,m)] * cpos_p1m1(h_cpos,m_cpos, hp1_cpos, mm1_cpos);

//weight cpos_p1m1_l : Cpos x Cpos x Cpos x Cpos x Dep -> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hp1_cpos, Cpos mm1_cpos, Dep label
//  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos) & cpos(m-1,mm1_cpos)
//  add [dep(h,m,label)] * cpos_p1m1_l(h_cpos,m_cpos, hp1_cpos, mm1_cpos, label);

weight cpos_p1m1_d : Cpos x Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hp1_cpos, Cpos mm1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos) & cpos(m-1,mm1_cpos)
  add [link(h,m)] * cpos_p1m1_d(h_cpos,m_cpos, hp1_cpos, mm1_cpos, bins(1,2,3,4,5,10,h-m));

//weight cpos_p1m1_l_d : Cpos x Cpos x Cpos x Cpos x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hp1_cpos, Cpos mm1_cpos, Dep label
//  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos) & cpos(m-1,mm1_cpos)
//  add [dep(h,m,label)] * cpos_p1m1_l_d(h_cpos,m_cpos, hp1_cpos, mm1_cpos, label, bins(1,2,3,4,5,10,h-m));

//h_cpos, m_cpos, mm1_cpos
weight cpos_p1m1_1 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos mm1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(m-1,mm1_cpos)
  add [link(h,m)] * cpos_p1m1_1(h_cpos,m_cpos, mm1_cpos);

//weight cpos_p1m1_l_1 : Cpos x Cpos x Cpos x Dep -> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos mm1_cpos, Dep label
//  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(m-1,mm1_cpos)
//  add [dep(h,m,label)] * cpos_p1m1_l_1(h_cpos,m_cpos, mm1_cpos, label);

weight cpos_p1m1_d_1 : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos mm1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(m-1,mm1_cpos)
  add [link(h,m)] * cpos_p1m1_d_1(h_cpos,m_cpos, mm1_cpos, bins(1,2,3,4,5,10,h-m));

//weight cpos_p1m1_l_d_1 : Cpos x Cpos x Cpos x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos mm1_cpos, Dep label
//  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(m-1,mm1_cpos)
//  add [dep(h,m,label)] * cpos_p1m1_l_d_1(h_cpos,m_cpos, mm1_cpos, label, bins(1,2,3,4,5,10,h-m));

//h_cpos, m_cpos, hp1_cpos
weight cpos_p1m1_2 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hp1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos)
  add [link(h,m)] * cpos_p1m1_2(h_cpos,m_cpos, hp1_cpos);

//weight cpos_p1m1_l_2 : Cpos x Cpos x Cpos x Dep -> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hp1_cpos, Dep label
//  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos)
//  add [dep(h,m,label)] * cpos_p1m1_l_2(h_cpos,m_cpos, hp1_cpos, label);

weight cpos_p1m1_d_2 : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hp1_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos)
  add [link(h,m)] * cpos_p1m1_d_2(h_cpos,m_cpos, hp1_cpos, bins(1,2,3,4,5,10,h-m));

//weight cpos_p1m1_l_d_2 : Cpos x Cpos x Cpos x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Cpos hp1_cpos, Dep label
//  if cpos(h,h_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos)
//  add [dep(h,m,label)] * cpos_p1m1_l_d_2(h_cpos,m_cpos, hp1_cpos, label, bins(1,2,3,4,5,10,h-m));

//hp1_cpos, m_cpos, mm1_cpos
weight cpos_p1m1_3 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Cpos mm1_cpos, Cpos m_cpos, Cpos hp1_cpos
  if cpos(m-1,mm1_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos) & word(h,_)
  add [link(h,m)] * cpos_p1m1_3(mm1_cpos,m_cpos, hp1_cpos);

//weight cpos_p1m1_l_3 : Cpos x Cpos x Cpos x Dep -> Double;
//factor :
//  for Int h, Int m, Cpos mm1_cpos, Cpos m_cpos, Cpos hp1_cpos, Dep label
//  if cpos(m-1,mm1_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos) & word(h,_)
//  add [dep(h,m,label)] * cpos_p1m1_l_3(mm1_cpos,m_cpos, hp1_cpos, label);

weight cpos_p1m1_d_3 : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Cpos mm1_cpos, Cpos m_cpos, Cpos hp1_cpos
  if cpos(m-1,mm1_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos) & word(h,_)
  add [link(h,m)] * cpos_p1m1_d_3(mm1_cpos,m_cpos, hp1_cpos, bins(1,2,3,4,5,10,h-m));

//weight cpos_p1m1_l_d_3 : Cpos x Cpos x Cpos x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Cpos mm1_cpos, Cpos m_cpos, Cpos hp1_cpos, Dep label
//  if cpos(m-1,mm1_cpos) & cpos(m,m_cpos) & cpos(h+1,hp1_cpos) & word(h,_)
//  add [dep(h,m,label)] * cpos_p1m1_l_d_3(mm1_cpos,m_cpos, hp1_cpos, label, bins(1,2,3,4,5,10,h-m));

//hp1_cpos, h_cpos, mm1_cpos
weight cpos_p1m1_4 : Cpos x Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Cpos mm1_cpos, Cpos h_cpos, Cpos hp1_cpos
  if cpos(m-1,mm1_cpos) & cpos(h,h_cpos) & cpos(h+1,hp1_cpos) & word(m,_)
  add [link(h,m)] * cpos_p1m1_4(mm1_cpos,h_cpos, hp1_cpos);

//weight cpos_p1m1_l_4 : Cpos x Cpos x Cpos x Dep -> Double;
//factor :
//  for Int h, Int m, Cpos mm1_cpos, Cpos h_cpos, Cpos hp1_cpos, Dep label
//  if cpos(m-1,mm1_cpos) & cpos(h,h_cpos) & cpos(h+1,hp1_cpos) & word(m,_)
//  add [dep(h,m,label)] * cpos_p1m1_l_4(mm1_cpos,h_cpos, hp1_cpos, label);

weight cpos_p1m1_d_4 : Cpos x Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Cpos mm1_cpos, Cpos h_cpos, Cpos hp1_cpos
  if cpos(m-1,mm1_cpos) & cpos(h,h_cpos) & cpos(h+1,hp1_cpos) & word(m,_)
  add [link(h,m)] * cpos_p1m1_d_4(mm1_cpos,h_cpos, hp1_cpos, bins(1,2,3,4,5,10,h-m));

//weight cpos_p1m1_l_d_4 : Cpos x Cpos x Cpos x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Cpos mm1_cpos, Cpos h_cpos, Cpos hp1_cpos, Dep label
//  if cpos(m-1,mm1_cpos) & cpos(h,h_cpos) & cpos(h+1,hp1_cpos) & word(m,_)
//  add [dep(h,m,label)] * cpos_p1m1_l_d_4(mm1_cpos,h_cpos, hp1_cpos, label, bins(1,2,3,4,5,10,h-m));
