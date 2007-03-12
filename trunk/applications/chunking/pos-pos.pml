/*
weight w_pos_1 : VeryCoarse x Pos -> Double-;

factor:
  for Int i, Pos p_1, Pos p_2, VeryCoarse v_1
  if verycoarse(p_1,v_1)
  add [pos(i-1,p_1) & pos(i,p_2)] * w_pos_1(v_1,p_2);

*/

weight w_pos_1 : VeryCoarse -> Double;
factor:
  for Int i, Pos p, VeryCoarse v
  if verycoarse(p,v) & word(i,_)
  add [pos(i,p)] * w_pos_1(v);

weight w_pos_2 : Pos x Pos -> Double-;
factor:
  for Int i, Pos p_1, Pos p_2
  add [pos(i-1,p_1) & pos(i,p_2)] * w_pos_2(p_1,p_2);

weight w_pos_3 : Pos x Pos x Pos -> Double-;
factor:
  for Int i, Pos p_1, Pos p_2, Pos p_3
  add [pos(i-2,p_1) & pos(i-1,p_2) & pos(i,p_3)] * w_pos_3(p_1,p_2,p_3);



/*
weight w_pos_2 : Pos x Pos -> Double-;
factor:
  for Int i, Pos p_i, Pos p_ip1
  if word(i+1,_)
  add [pos(i,p_i) & pos(i+1,p_ip1)] * w_pos_2(p_i,p_ip1);

weight w_pos_3 : Pos x Pos x Pos -> Double-;
factor:
  for Int i, Pos p_1, Pos p_2, Pos p_3
  add [pos(i-1,p_1) & pos(i,p_2) & pos(i+1,p_3)] * w_pos_3(p_1,p_2,p_3);

*/  