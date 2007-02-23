/* This file contains features a la pos_h+1 pos_h pos_m pos_m-1 */

//hp1_pos, h_pos, m_pos, mm1_pos
weight pos_p1m1 : Pos x Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hp1_pos, Pos mm1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(h+1,hp1_pos) & pos(m-1,mm1_pos)
  add [link(h,m)] * pos_p1m1(h_pos,m_pos, hp1_pos, mm1_pos);

weight pos_p1m1_l : Pos x Pos x Pos x Pos x Dep -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hp1_pos, Pos mm1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(h+1,hp1_pos) & pos(m-1,mm1_pos)
  add [dep(h,m,label)] * pos_p1m1_l(h_pos,m_pos, hp1_pos, mm1_pos, label);

weight pos_p1m1_d : Pos x Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hp1_pos, Pos mm1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(h+1,hp1_pos) & pos(m-1,mm1_pos)
  add [link(h,m)] * pos_p1m1_d(h_pos,m_pos, hp1_pos, mm1_pos, bins(1,2,3,4,5,10,h-m));

weight pos_p1m1_l_d : Pos x Pos x Pos x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hp1_pos, Pos mm1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(h+1,hp1_pos) & pos(m-1,mm1_pos)
  add [dep(h,m,label)] * pos_p1m1_l_d(h_pos,m_pos, hp1_pos, mm1_pos, label, bins(1,2,3,4,5,10,h-m));

//h_pos, m_pos, mm1_pos
weight pos_p1m1_1 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos mm1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(m-1,mm1_pos)
  add [link(h,m)] * pos_p1m1_1(h_pos,m_pos, mm1_pos);

weight pos_p1m1_l_1 : Pos x Pos x Pos x Dep -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos mm1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(m-1,mm1_pos)
  add [dep(h,m,label)] * pos_p1m1_l_1(h_pos,m_pos, mm1_pos, label);

weight pos_p1m1_d_1 : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos mm1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(m-1,mm1_pos)
  add [link(h,m)] * pos_p1m1_d_1(h_pos,m_pos, mm1_pos, bins(1,2,3,4,5,10,h-m));

weight pos_p1m1_l_d_1 : Pos x Pos x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos mm1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(m-1,mm1_pos)
  add [dep(h,m,label)] * pos_p1m1_l_d_1(h_pos,m_pos, mm1_pos, label, bins(1,2,3,4,5,10,h-m));

//h_pos, m_pos, hp1_pos
weight pos_p1m1_2 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hp1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(h+1,hp1_pos)
  add [link(h,m)] * pos_p1m1_2(h_pos,m_pos, hp1_pos);

weight pos_p1m1_l_2 : Pos x Pos x Pos x Dep -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hp1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(h+1,hp1_pos)
  add [dep(h,m,label)] * pos_p1m1_l_2(h_pos,m_pos, hp1_pos, label);

weight pos_p1m1_d_2 : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hp1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(h+1,hp1_pos)
  add [link(h,m)] * pos_p1m1_d_2(h_pos,m_pos, hp1_pos, bins(1,2,3,4,5,10,h-m));

weight pos_p1m1_l_d_2 : Pos x Pos x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hp1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(h+1,hp1_pos)
  add [dep(h,m,label)] * pos_p1m1_l_d_2(h_pos,m_pos, hp1_pos, label, bins(1,2,3,4,5,10,h-m));

//hp1_pos, m_pos, mm1_pos
weight pos_p1m1_3 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos mm1_pos, Pos m_pos, Pos hp1_pos
  if pos(m-1,mm1_pos) & pos(m,m_pos) & pos(h+1,hp1_pos)
  add [link(h,m)] * pos_p1m1_3(mm1_pos,m_pos, hp1_pos);

weight pos_p1m1_l_3 : Pos x Pos x Pos x Dep -> Double;
factor :
  for Int h, Int m, Pos mm1_pos, Pos m_pos, Pos hp1_pos, Dep label
  if pos(m-1,mm1_pos) & pos(m,m_pos) & pos(h+1,hp1_pos)
  add [dep(h,m,label)] * pos_p1m1_l_3(mm1_pos,m_pos, hp1_pos, label);

weight pos_p1m1_d_3 : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos mm1_pos, Pos m_pos, Pos hp1_pos
  if pos(m-1,mm1_pos) & pos(m,m_pos) & pos(h+1,hp1_pos)
  add [link(h,m)] * pos_p1m1_d_3(mm1_pos,m_pos, hp1_pos, bins(1,2,3,4,5,10,h-m));

weight pos_p1m1_l_d_3 : Pos x Pos x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos mm1_pos, Pos m_pos, Pos hp1_pos, Dep label
  if pos(m-1,mm1_pos) & pos(m,m_pos) & pos(h+1,hp1_pos)
  add [dep(h,m,label)] * pos_p1m1_l_d_3(mm1_pos,m_pos, hp1_pos, label, bins(1,2,3,4,5,10,h-m));

//hp1_pos, h_pos, mm1_pos
weight pos_p1m1_4 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos mm1_pos, Pos h_pos, Pos hp1_pos
  if pos(m-1,mm1_pos) & pos(h,h_pos) & pos(h+1,hp1_pos)
  add [link(h,m)] * pos_p1m1_4(mm1_pos,h_pos, hp1_pos);

weight pos_p1m1_l_4 : Pos x Pos x Pos x Dep -> Double;
factor :
  for Int h, Int m, Pos mm1_pos, Pos h_pos, Pos hp1_pos, Dep label
  if pos(m-1,mm1_pos) & pos(h,h_pos) & pos(h+1,hp1_pos)
  add [dep(h,m,label)] * pos_p1m1_l_4(mm1_pos,h_pos, hp1_pos, label);

weight pos_p1m1_d_4 : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos mm1_pos, Pos h_pos, Pos hp1_pos
  if pos(m-1,mm1_pos) & pos(h,h_pos) & pos(h+1,hp1_pos)
  add [link(h,m)] * pos_p1m1_d_4(mm1_pos,h_pos, hp1_pos, bins(1,2,3,4,5,10,h-m));

weight pos_p1m1_l_d_4 : Pos x Pos x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos mm1_pos, Pos h_pos, Pos hp1_pos, Dep label
  if pos(m-1,mm1_pos) & pos(h,h_pos) & pos(h+1,hp1_pos)
  add [dep(h,m,label)] * pos_p1m1_l_d_4(mm1_pos,h_pos, hp1_pos, label, bins(1,2,3,4,5,10,h-m));
