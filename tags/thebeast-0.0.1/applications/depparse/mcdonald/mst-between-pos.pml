/* This file contains features a la pos_h pos_b pos_m were h < b < m or h > b > m*/

weight between_hm : Pos x Pos x Pos -> Double;
factor:
  for Int h, Int b, Int m, Int l, Int r, Pos pl, Pos pb, Pos pr
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) & l < b & b < r &
    pos(l,pl) & pos(b,pb) & pos(r,pr)
  add [link(h,m)] * between_hm(pl,pb,pr);

weight between_hm_d : Pos x Pos x Pos x Int -> Double;
factor:
  for Int h, Int b, Int m, Int l, Int r, Pos pl, Pos pb, Pos pr
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) & l < b & b < r &
    pos(l,pl) & pos(b,pb) & pos(r,pr)
  add [link(h,m)] * between_hm_d(pl,pb,pr,bins(1,2,3,4,5,10,h-m));

