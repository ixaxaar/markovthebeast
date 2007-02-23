/* This file contains features a la pos_h-1 pos_h pos_m pos_m+1 */

//hm1_pos, h_pos, m_pos, mp1_pos
weight pos_m1p1 : Pos x Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hm1_pos, Pos mp1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(h-1,hm1_pos) & pos(m+1,mp1_pos)
  add [link(h,m)] * pos_m1p1(h_pos,m_pos, hm1_pos, mp1_pos);

weight pos_m1p1_l : Pos x Pos x Pos x Pos x Dep -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hm1_pos, Pos mp1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(h-1,hm1_pos) & pos(m+1,mp1_pos)
  add [dep(h,m,label)] * pos_m1p1_l(h_pos,m_pos, hm1_pos, mp1_pos, label);

weight pos_m1p1_d : Pos x Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hm1_pos, Pos mp1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(h-1,hm1_pos) & pos(m+1,mp1_pos)
  add [link(h,m)] * pos_m1p1_d(h_pos,m_pos, hm1_pos, mp1_pos, bins(1,2,3,4,5,10,h-m));

weight pos_m1p1_l_d : Pos x Pos x Pos x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hm1_pos, Pos mp1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(h-1,hm1_pos) & pos(m+1,mp1_pos)
  add [dep(h,m,label)] * pos_m1p1_l_d(h_pos,m_pos, hm1_pos, mp1_pos, label, bins(1,2,3,4,5,10,h-m));

//h_pos, m_pos, mp1_pos
weight pos_m1p1_1 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos mp1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(m+1,mp1_pos)
  add [link(h,m)] * pos_m1p1_1(h_pos,m_pos, mp1_pos);

weight pos_m1p1_l_1 : Pos x Pos x Pos x Dep -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos mp1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(m+1,mp1_pos)
  add [dep(h,m,label)] * pos_m1p1_l_1(h_pos,m_pos, mp1_pos, label);

weight pos_m1p1_d_1 : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos mp1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(m+1,mp1_pos)
  add [link(h,m)] * pos_m1p1_d_1(h_pos,m_pos, mp1_pos, bins(1,2,3,4,5,10,h-m));

weight pos_m1p1_l_d_1 : Pos x Pos x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos mp1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(m+1,mp1_pos)
  add [dep(h,m,label)] * pos_m1p1_l_d_1(h_pos,m_pos, mp1_pos, label, bins(1,2,3,4,5,10,h-m));

//h_pos, m_pos, hm1_pos
weight pos_m1p1_2 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hm1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(h-1,hm1_pos)
  add [link(h,m)] * pos_m1p1_2(h_pos,m_pos, hm1_pos);

weight pos_m1p1_l_2 : Pos x Pos x Pos x Dep -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hm1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(h-1,hm1_pos)
  add [dep(h,m,label)] * pos_m1p1_l_2(h_pos,m_pos, hm1_pos, label);

weight pos_m1p1_d_2 : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hm1_pos
  if pos(h,h_pos) & pos(m,m_pos) & pos(h-1,hm1_pos)
  add [link(h,m)] * pos_m1p1_d_2(h_pos,m_pos, hm1_pos, bins(1,2,3,4,5,10,h-m));

weight pos_m1p1_l_d_2 : Pos x Pos x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Pos hm1_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos) & pos(h-1,hm1_pos)
  add [dep(h,m,label)] * pos_m1p1_l_d_2(h_pos,m_pos, hm1_pos, label, bins(1,2,3,4,5,10,h-m));

//hm1_pos, m_pos, mp1_pos
weight pos_m1p1_3 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos mp1_pos, Pos m_pos, Pos hm1_pos
  if pos(m+1,mp1_pos) & pos(m,m_pos) & pos(h-1,hm1_pos)
  add [link(h,m)] * pos_m1p1_3(mp1_pos,m_pos, hm1_pos);

weight pos_m1p1_l_3 : Pos x Pos x Pos x Dep -> Double;
factor :
  for Int h, Int m, Pos mp1_pos, Pos m_pos, Pos hm1_pos, Dep label
  if pos(m+1,mp1_pos) & pos(m,m_pos) & pos(h-1,hm1_pos)
  add [dep(h,m,label)] * pos_m1p1_l_3(mp1_pos,m_pos, hm1_pos, label);

weight pos_m1p1_d_3 : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos mp1_pos, Pos m_pos, Pos hm1_pos
  if pos(m+1,mp1_pos) & pos(m,m_pos) & pos(h-1,hm1_pos)
  add [link(h,m)] * pos_m1p1_d_3(mp1_pos,m_pos, hm1_pos, bins(1,2,3,4,5,10,h-m));

weight pos_m1p1_l_d_3 : Pos x Pos x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos mp1_pos, Pos m_pos, Pos hm1_pos, Dep label
  if pos(m+1,mp1_pos) & pos(m,m_pos) & pos(h-1,hm1_pos)
  add [dep(h,m,label)] * pos_m1p1_l_d_3(mp1_pos,m_pos, hm1_pos, label, bins(1,2,3,4,5,10,h-m));

//hm1_pos, h_pos, mp1_pos
weight pos_m1p1_4 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos mp1_pos, Pos h_pos, Pos hm1_pos
  if pos(m+1,mp1_pos) & pos(h,h_pos) & pos(h-1,hm1_pos)
  add [link(h,m)] * pos_m1p1_4(mp1_pos,h_pos, hm1_pos);

weight pos_m1p1_l_4 : Pos x Pos x Pos x Dep -> Double;
factor :
  for Int h, Int m, Pos mp1_pos, Pos h_pos, Pos hm1_pos, Dep label
  if pos(m+1,mp1_pos) & pos(h,h_pos) & pos(h-1,hm1_pos)
  add [dep(h,m,label)] * pos_m1p1_l_4(mp1_pos,h_pos, hm1_pos, label);

weight pos_m1p1_d_4 : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos mp1_pos, Pos h_pos, Pos hm1_pos
  if pos(m+1,mp1_pos) & pos(h,h_pos) & pos(h-1,hm1_pos)
  add [link(h,m)] * pos_m1p1_d_4(mp1_pos,h_pos, hm1_pos, bins(1,2,3,4,5,10,h-m));

weight pos_m1p1_l_d_4 : Pos x Pos x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos mp1_pos, Pos h_pos, Pos hm1_pos, Dep label
  if pos(m+1,mp1_pos) & pos(h,h_pos) & pos(h-1,hm1_pos)
  add [dep(h,m,label)] * pos_m1p1_l_d_4(mp1_pos,h_pos, hm1_pos, label, bins(1,2,3,4,5,10,h-m));
