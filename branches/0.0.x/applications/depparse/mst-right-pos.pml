/* This file contains features a la pos_h+1 pos_h pos_m pos_m+1 */

//hp1_pos, h_pos, m_pos, mp1_pos
weight pos_p1p1 : Pos x Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hp1_pos, Pos mp1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(h+1,hp1_pos) & pos(m+1,mp1_pos)
  add [link(h,m)] * pos_p1p1(h_pos,m_pos, hp1_pos, mp1_pos);

//weight pos_p1p1_l : Pos x Pos x Pos x Pos x Dep -> Double;
//factor :
//  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hp1_pos, Pos mp1_pos, Dep label
//  if pos(h,h_pos) & pos(m,m_pos) & pos(h+1,hp1_pos) & pos(m+1,mp1_pos)
//  add [dep(h,m,label)] * pos_p1p1_l(h_pos,m_pos, hp1_pos, mp1_pos, label);

weight pos_p1p1_d : Pos x Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hp1_pos, Pos mp1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(h+1,hp1_pos) & pos(m+1,mp1_pos)
  add [link(h,m)] * pos_p1p1_d(h_pos,m_pos, hp1_pos, mp1_pos, bins(1,2,3,4,5,10,h-m));

//weight pos_p1p1_l_d : Pos x Pos x Pos x Pos x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hp1_pos, Pos mp1_pos, Dep label
//  if pos(h,h_pos) & pos(m,m_pos) & pos(h+1,hp1_pos) & pos(m+1,mp1_pos)
//  add [dep(h,m,label)] * pos_p1p1_l_d(h_pos,m_pos, hp1_pos, mp1_pos, label, bins(1,2,3,4,5,10,h-m));