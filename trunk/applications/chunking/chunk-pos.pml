
/*
weight ch_pos_d: Pos x Pos x Chunk x Int -> Double+;
factor:
  for Int b, Int e, Pos p_b, Pos p_e, Chunk c
  if e > b
  add [pos(b,p_b) & pos(e,p_e) => chunk(b,e,c)] * ch_pos_d(p_b,p_e,c, bins(1,2,10,e-b));

*/

//look at inner tags
weight ch_pos: Pos x Chunk -> Double+;
factor:
  for Int t, Pos p, Chunk c
  add [pos(t,p) => chunk(t,t,c)] * ch_pos(p,c);

weight ch_pos_2: Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p_1, Pos p_2, Chunk c
  add [pos(t,p_1) & pos(t+1,p_2) => chunk(t,t+1,c)] * ch_pos_2(p_1,p_2,c);

weight ch_pos_2_d2: Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p_1, Pos p_2, Chunk c
  add [pos(t,p_1) & pos(t+2,p_2) => chunk(t,t+2,c)] * ch_pos_2_d2(p_1,p_2,c);

weight ch_pos_2_d3: Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p_1, Pos p_2, Chunk c
  add [pos(t,p_1) & pos(t+3,p_2) => chunk(t,t+3,c)] * ch_pos_2_d3(p_1,p_2,c);

weight ch_pos_3: Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p_1, Pos p_2, Pos p_3, Chunk c
  add [pos(t,p_1) & pos(t+1,p_2) & pos(t+2, p_3) => chunk(t,t+2,c)] * ch_pos_3(p_1,p_2,p_3,c);


//look at outer tags
/*
weight ch_pos_2_outer: Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p_1, Pos p_2, Chunk c
  add [pos(t-1,p_1) & pos(t+2,p_2) => chunk(t,t+1,c)] * ch_pos_2_outer(p_1,p_2,c);

*/