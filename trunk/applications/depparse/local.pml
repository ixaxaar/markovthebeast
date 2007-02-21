weight w_hpmp : Pos x Pos x Int -> Double;

factor :
  for Int h, Int m, Pos hp, Pos mp
  if pos(h,hp) & pos(m,mp)
  add [link(h,m)] * w_hpmp(hp,mp, bins(0,1,2,5,h-m));

