
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