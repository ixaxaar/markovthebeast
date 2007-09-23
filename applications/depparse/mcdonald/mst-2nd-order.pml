weight w_2nd: Pos x Pos x Pos -> Double-;
factor f_2nd:
  for Int h, Int m1, Int m2, Pos p_m1, Pos p_m2, Pos p_h
  if pos(m1,p_m1) & pos(m2, p_m2) & pos(h,p_h)
  add [link(h,m1) & link(h,m2)] * w_2nd(p_m1,p_m2, p_h);

set collector.all.w_2nd = true;  
