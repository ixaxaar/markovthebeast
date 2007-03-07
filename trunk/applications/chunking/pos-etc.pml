weight w_cardinal: Cardinal x Pos -> Double;
factor:
  for Int i, Pos p, Cardinal w_i
  if cardinal(i,w_i)
  add [pos(i,p)] * w_cardinal(w_i,p);
