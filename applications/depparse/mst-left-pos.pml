/* This file contains features a la pos_h-1 pos_h pos_m pos_m-1 */

//hm1_pos, h_pos, m_pos, mm1_pos
weight pos_m1m1 : Pos x Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hm1_pos, Pos mm1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(h-1,hm1_pos) & pos(m-1,mm1_pos)
  add [link(h,m)] * pos_m1m1(h_pos,m_pos, hm1_pos, mm1_pos);

weight pos_m1m1_l : Pos x Pos x Pos x Pos x Dep -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hm1_pos, Pos mm1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(h-1,hm1_pos) & pos(m-1,mm1_pos)
  add [dep(h,m,label)] * pos_m1m1_l(h_pos,m_pos, hm1_pos, mm1_pos, label);

weight pos_m1m1_d : Pos x Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hm1_pos, Pos mm1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(h-1,hm1_pos) & pos(m-1,mm1_pos)
  add [link(h,m)] * pos_m1m1_d(h_pos,m_pos, hm1_pos, mm1_pos, bins(1,2,3,4,5,10,h-m));

weight pos_m1m1_l_d : Pos x Pos x Pos x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hm1_pos, Pos mm1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(h-1,hm1_pos) & pos(m-1,mm1_pos)
  add [dep(h,m,label)] * pos_m1m1_l_d(h_pos,m_pos, hm1_pos, mm1_pos, label, bins(1,2,3,4,5,10,h-m));

