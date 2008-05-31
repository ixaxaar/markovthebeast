/* This file contains features a la pos_h-1 pos_h pos_m pos_m-1 */

//hp1_pos, h_pos, m_pos, mm1_pos
weight pos_l1234 : Pos x Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l-1, p2) & pos(r-1, p3) & pos(r,p4)
  add [link(h,m)] * pos_l1234(p1,p2,p3,p4);

weight pos_l1234_d : Pos x Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l-1, p2) & pos(r-1, p3) & pos(r,p4)
  add [link(h,m)] * pos_l1234_d(p1,p2,p3,p4, bins(0,1,2,3,4,5,10,h-m));


