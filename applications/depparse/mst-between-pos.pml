/* This file contains features a la pos_h pos_b pos_m were h < b < m or h > b > m*/

weight between_hm : Pos x Pos x Pos -> Double;
factor:
  for Int h, Int b, Int m, Pos h_pos, Pos b_pos, Pos m_pos
  if pos(h, h_pos), pos(m, m_pos), pos(b, b_bos) & h < b & b < m
  add [link(h,m)] * between_hm(h_pos, b_pos, m_pos);

weight between_hm_d : Pos x Pos x Pos x Int -> Double;
factor:
  for Int h, Int b, Int m, Pos h_pos, Pos b_pos, Pos m_pos
  if pos(h, h_pos), pos(m, m_pos), pos(b, b_bos) & h < b & b < m
  add [link(h,m)] * between_hm_d(h_pos, b_pos, m_pos, bins(1,2,3,4,5,10,h-m));

weight between_hm_l : Pos x Pos x Pos x Dep -> Double;
factor:
  for Int h, Int b, Int m, Pos h_pos, Pos b_pos, Pos m_pos, Dep l
  if pos(h, h_pos), pos(m, m_pos), pos(b, b_bos) & h < b & b < m
  add [dep(h,m,l)] * between_hm_l(h_pos, b_pos, m_pos,l);

weight between_hm_l_d : Pos x Pos x Pos x Dep x Int -> Double;
factor:
  for Int h, Int b, Int m, Pos h_pos, Pos b_pos, Pos m_pos
  if pos(h, h_pos), pos(m, m_pos), pos(b, b_bos) & h < b & b < m
  add [dep(h,m,l)] * between_hm_l_d(h_pos, b_pos, m_pos, l, bins(1,2,3,4,5,10,h-m));
