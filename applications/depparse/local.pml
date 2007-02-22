
//word tag of head and modifier
weight w_hw_mw: Word x Word -> Double;

factor :
  for Int h, Int m, Word hw, Word mw
  if word(h,hw) & word(m,mw)
  add [link(h,m)] * w_hw_mw(hw,mw);


//pos tag of head and modifier together with a (binned and signed) distance between the two.
weight w_hp_mp_dist : Pos x Pos x Int -> Double;

factor :
  for Int h, Int m, Pos hp, Pos mp
  if pos(h,hp) & pos(m,mp)
  add [link(h,m)] * w_hp_mp_dist(hp,mp, bins(0,1,2,5,10,30,h-m));

/*
//label, pos tag of head and modifier together with a (binned and signed) distance between the two.
weight w_dep_hp_mp_dist : Pos x Pos x Dep x Int -> Double;

factor :
  for Int h, Int m, Pos hp, Pos mp, Dep d
  if pos(h,hp) & pos(m,mp)
  add [dep(h,m,d)] * w_dep_hp_mp_dist(hp,mp, d, bins(0,1,2,5,h-m));
*/

