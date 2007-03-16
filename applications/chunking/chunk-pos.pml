weight w_propose_1: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Int e, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  if e < b + 6 & e > b
  add [pos(b-1,p1) & pos(b,p2) & pos(e,p3) & pos(e+1,p4) => chunk(b,e,c)] * w_propose_1(p1,p2,p3,p4,c);

weight w_propose_1_d: Pos x Pos x Pos x Pos x Chunk x Int -> Double+;
factor:
  for Int b, Int e, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  if e < b + 6 & e > b
  add [pos(b-1,p1) & pos(b,p2) & pos(e,p3) & pos(e+1,p4) => chunk(b,e,c)] * w_propose_1_d(p1,p2,p3,p4,c, e-b);

weight w_propose_2: Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Int e, Pos p1, Pos p2, Chunk c
  if e < b + 6 & e >= b
  add [pos(b-1,p1) & pos(e+1,p2) => chunk(b,e,c)] * w_propose_2(p1,p2,c);

weight w_propose_2_d: Pos x Pos x Chunk x Int  -> Double+;
factor:
  for Int b, Int e, Pos p1, Pos p2, Chunk c
  if e < b + 6 & e >= b
  add [pos(b-1,p1) & pos(e+1,p2) => chunk(b,e,c)] * w_propose_2_d(p1,p2,c, e-b);

//pos_0, pos_e, pos_e+1 for chunk(0,e)
weight w_propose_3: Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int e, Pos p1, Pos p2, Pos p3, Chunk c
  if e < 6 & e > 0
  add [pos(0,p1) & pos(e,p2) & pos(e+1,p3)  => chunk(0,e,c)] * w_propose_3(p1,p2,p3,c);

weight w_propose_3_d: Pos x Pos x Pos x Chunk x Int -> Double+;
factor:
  for  Int e, Pos p1, Pos p2, Pos p3, Chunk c
  if e < 6 & e > 0
  add [pos(0,p1) & pos(e,p2) & pos(e+1,p3)  => chunk(0,e,c)] * w_propose_3_d(p1,p2,p3,c,e);

weight w_propose_4_d: Pos x Chunk x Int -> Double+;
factor:
  for Int e, Pos p1, Chunk c
  if e < 6 & e >= 0
  add [pos(e+1,p1) => chunk(0,e,c)] * w_propose_4_d(p1,c, e);

weight w_propose_4: Pos x Chunk -> Double+;
factor:
  for Int e, Pos p1, Chunk c
  if e < 6 & e >= 0
  add [pos(e+1,p1) => chunk(0,e,c)] * w_propose_4(p1,c);


//explicit rules
weight w_propose_5: Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int i, Pos p1, Pos p2, Pos p3, Chunk c
  add [pos(i-1,p1) & pos(i,p2) & pos(i+1,p3) => chunk(i,i,c)] * w_propose_5(p1,p2,p3,c);

weight w_propose_6: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int i, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(i,p1) & pos(i+1,p2) & pos(i+2,p3) & pos(i+3,p4) => chunk(i+1,i+2,c)] * w_propose_6(p1,p2,p3,p4,c);

weight w_propose_7: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int i, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(i,p1) & pos(i+1,p2) & pos(i+2,p3) & pos(i+3,p4) => chunk(i+1,i+2,c)] * w_propose_6(p1,p2,p3,p4,c);

//some pos tags can't appear in chunks
weight w_forbid_1: Pos x Chunk -> Double-;
factor:
  for Int b, Int e, Int m, Pos p, Chunk c
  if b <= m & m <= e
  add [pos(m,p) & chunk(b,e,c)] * w_forbid_1(p,c);

//some pos tag binary subsequences can't appear in chunks
weight w_forbid_2: Pos x Pos x Chunk -> Double-;
factor:
  for Int b, Int e, Int m1, Int m2, Pos p1, Pos p2, Chunk c
  if b <= m1 & m1 < m2 & m2 <= e
  add [pos(m1,p1) & pos(m2,p2) & chunk(b,e,c)] * w_forbid_2(p1,p2,c);

//some pos tags can't be chunks themselves
weight w_forbid_3: Pos x Chunk -> Double-;
factor:
  for Int b, Pos p, Chunk c
  add [pos(b,p) & chunk(b,b,c)] * w_forbid_3(p,c);

