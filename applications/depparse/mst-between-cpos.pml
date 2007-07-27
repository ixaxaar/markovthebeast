/* This file contains features a la cpos_h cpos_b cpos_m were h < b < m or h > b > m*/

weight between_hm : Cpos x Cpos x Cpos -> Double;
factor:
  for Int h, Int b, Int m, Int l, Int r, Cpos pl, Cpos pb, Cpos pr
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) & l < b & b < r &
    cpos(l,pl) & cpos(b,pb) & cpos(r,pr)
  add [link(h,m)] * between_hm(pl,pb,pr);

weight between_hm_d : Cpos x Cpos x Cpos x Int -> Double;
factor:
  for Int h, Int b, Int m, Int l, Int r, Cpos pl, Cpos pb, Cpos pr
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) & l < b & b < r &
    cpos(l,pl) & cpos(b,pb) & cpos(r,pr)
  add [link(h,m)] * between_hm_d(pl,pb,pr,bins(1,2,3,4,5,10,h-m));

/*
weight between_hm : Cpos x Cpos x Cpos -> Double;
factor:
  for Int h, Int b, Int m, Cpos h_cpos, Cpos b_cpos, Cpos m_cpos
  if cpos(h, h_cpos) & cpos(m, m_cpos) & cpos(b, b_cpos) & h < b & b < m
  add [link(h,m)] * between_hm(h_cpos, b_cpos, m_cpos);

weight between_hm_d : Cpos x Cpos x Cpos x Int -> Double;
factor:
  for Int h, Int b, Int m, Cpos h_cpos, Cpos b_cpos, Cpos m_cpos
  if cpos(h, h_cpos) & cpos(m, m_cpos) & cpos(b, b_cpos) & h < b & b < m
  add [link(h,m)] * between_hm_d(h_cpos, b_cpos, m_cpos, bins(1,2,3,4,5,10,h-m));

weight between_hm_l : Cpos x Cpos x Cpos x Dep -> Double;
factor:
  for Int h, Int b, Int m, Cpos h_cpos, Cpos b_cpos, Cpos m_cpos, Dep l
  if cpos(h, h_cpos) & cpos(m, m_cpos) & cpos(b, b_cpos) & h < b & b < m
  add [dep(h,m,l)] * between_hm_l(h_cpos, b_cpos, m_cpos,l);

weight between_hm_l_d : Cpos x Cpos x Cpos x Dep x Int -> Double;
factor:
  for Int h, Int b, Int m, Cpos h_cpos, Cpos b_cpos, Cpos m_cpos, Dep l
  if cpos(h, h_cpos) & cpos(m, m_cpos) & cpos(b, b_cpos) & h < b & b < m
  add [dep(h,m,l)] * between_hm_l_d(h_cpos, b_cpos, m_cpos, l, bins(1,2,3,4,5,10,h-m));
*/