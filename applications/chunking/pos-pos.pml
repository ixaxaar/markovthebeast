weight w_pos_pos : Pos x Pos -> Double-;
factor:
  for Int i, Pos p_i, Pos p_ip1
  if word(i+1,_)
  add [pos(i,p_i) & pos(i+1,p_ip1)] * w_pos_pos(p_i,p_ip1);