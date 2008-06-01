/* This file contains features a la pos_h+1 pos_h pos_m pos_m-1 and all 3 gram subfeatures */

//hp1_pos, h_pos, m_pos, mm1_pos
weight pos_1234 : Pos x Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l+1, p2) & pos(r-1, p3) & pos(r,p4)
  add [link(h,m)] * pos_1234(p1,p2,p3,p4);

weight pos_1234_d : Pos x Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l+1, p2) & pos(r-1, p3) & pos(r,p4)
  add [link(h,m)] * pos_1234_d(p1,p2,p3,p4, bins(0,1,2,3,4,5,10,h-m));

weight pos_123 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p3
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l+1, p2) & pos(r-1, p3)
  add [link(h,m)] * pos_123(p1,p2,p3);

weight pos_123_d : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p3
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l+1, p2) & pos(r-1, p3)
  add [link(h,m)] * pos_123_d(p1,p2,p3,bins(0,1,2,3,4,5,10,h-m));

weight pos_234 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p2, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l+1, p2) & pos(r-1, p3) & pos(r,p4)
  add [link(h,m)] * pos_234(p2,p3,p4);

weight pos_234_d : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p2, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l+1, p2) & pos(r-1, p3) & pos(r,p4)
  add [link(h,m)] * pos_234_d(p2,p3,p4, bins(0,1,2,3,4,5,10,h-m));

weight pos_134 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(r-1, p3) & pos(r,p4)
  add [link(h,m)] * pos_134(p1,p3,p4);

weight pos_134_d : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(r-1, p3) & pos(r,p4)
  add [link(h,m)] * pos_134_d(p1,p3,p4, bins(0,1,2,3,4,5,10,h-m));

//hp1_pos, h_pos, m_pos, mm1_pos
weight pos_124 : Pos x Pos x Pos -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l+1, p2) & pos(r,p4)
  add [link(h,m)] * pos_124(p1,p2,p4);

weight pos_124_d : Pos x Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Int l, Int r, Pos p1, Pos p2, Pos p3, Pos p4
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) &
  pos(l, p1) & pos(l+1, p2) & pos(r,p4)
  add [link(h,m)] * pos_124_d(p1,p2,p4, bins(0,1,2,3,4,5,10,h-m));

