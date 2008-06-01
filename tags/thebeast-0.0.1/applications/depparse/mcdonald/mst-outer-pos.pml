/* This file contains features a la pos_oh+1 pos_oh pos_om pos_om-1 and all 3 gram subfeatures */

//hp1_pos, h_pos, m_pos, mm1_pos
weight pos_o1234 : Pos x Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l-1, p2) & pos(r+1, p3) & pos(r,p4)
  add [link(h,m)] * pos_o1234(p1,p2,p3,p4);

weight pos_o1234_d : Pos x Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l-1, p2) & pos(r+1, p3) & pos(r,p4)
  add [link(h,m)] * pos_o1234_d(p1,p2,p3,p4, bins(0,1,2,3,4,5,10,h-m));

weight pos_o123 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p3
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l-1, p2) & pos(r+1, p3)
  add [link(h,m)] * pos_o123(p1,p2,p3);

weight pos_o123_d : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p3
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l-1, p2) & pos(r+1, p3)
  add [link(h,m)] * pos_o123_d(p1,p2,p3,bins(0,1,2,3,4,5,10,h-m));

weight pos_o234 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p2, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l-1, p2) & pos(r+1, p3) & pos(r,p4)
  add [link(h,m)] * pos_o234(p2,p3,p4);

weight pos_o234_d : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p2, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l-1, p2) & pos(r+1, p3) & pos(r,p4)
  add [link(h,m)] * pos_o234_d(p2,p3,p4, bins(0,1,2,3,4,5,10,h-m));

weight pos_o134 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(r+1, p3) & pos(r,p4)
  add [link(h,m)] * pos_o134(p1,p3,p4);

weight pos_o134_d : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(r+1, p3) & pos(r,p4)
  add [link(h,m)] * pos_o134_d(p1,p3,p4, bins(0,1,2,3,4,5,10,h-m));

//hp1_pos, h_pos, m_pos, mm1_pos
weight pos_o124 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l-1, p2) & pos(r,p4)
  add [link(h,m)] * pos_o124(p1,p2,p4);

weight pos_o124_d : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l-1, p2) & pos(r,p4)
  add [link(h,m)] * pos_o124_d(p1,p2,p4, bins(0,1,2,3,4,5,10,h-m));

