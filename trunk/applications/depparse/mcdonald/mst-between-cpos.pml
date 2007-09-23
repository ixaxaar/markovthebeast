/* This file contains features a la cpos_h cpos_b cpos_m were h < b < m or h > b > m*/

weight cbetween_hm : Cpos x Cpos x Cpos -> Double;
factor:
  for Int h, Int b, Int m, Int l, Int r, Cpos pl, Cpos pb, Cpos pr
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) & l < b & b < r &
    cpos(l,pl) & cpos(b,pb) & cpos(r,pr)
  add [link(h,m)] * cbetween_hm(pl,pb,pr);

weight cbetween_hm_d : Cpos x Cpos x Cpos x Int -> Double;
factor:
  for Int h, Int b, Int m, Int l, Int r, Cpos pl, Cpos pb, Cpos pr
  if word(h,_) & word(m,_) & l == min(h,m) & r == max(h,m) & l < b & b < r &
    cpos(l,pl) & cpos(b,pb) & cpos(r,pr)
  add [link(h,m)] * cbetween_hm_d(pl,pb,pr,bins(1,2,3,4,5,10,h-m));

