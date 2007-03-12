weight w_propose_1: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Int e, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  if e < b + 6 & e > b
  add [pos(b-1,p1) & pos(b,p2) & pos(e,p3) & pos(e+1,p4) => chunk(b,e,c)] * w_propose_1(p1,p2,p3,p4,c);

weight w_propose_2: Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Int e, Pos p1, Pos p2, Chunk c
  if e < b + 6 & e > b
  add [pos(b-1,p1) & pos(e+1,p2) => chunk(b,e,c)] * w_propose_2(p1,p2,c);

/*
weight w_propose_3: Pos x Pos x Int xChunk -> Double+;
factor:
  for Int b, Int e, Pos p1, Pos p2, Chunk c
  if e < b + 6 & e > b
  add [pos(b-1,p1) & pos(e+1,p2) => chunk(b,e,c)] * w_propose_3(p1,p2,e-b);
*/
  

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

/*
weight w_chunkpos_1: Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p1, Pos p2, Pos p3, Chunk c
  add [pos(t-1,p1) & pos(t,p2) & pos(t+1,p3) => chunk(t,t,c)] * w_chunkpos_1(p1,p2,p3,c);

weight w_chunkpos_2: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(t-1,p1) & pos(t,p2) & pos(t+1,p3) & pos(t+2,p4) => chunk(t,t+1,c)] * w_chunkpos_2(p1,p2,p3,p4,c);

weight w_chunkpos_3: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(t-1,p1) & pos(t,p2) & pos(t+2,p3) & pos(t+3,p4) => chunk(t,t+2,c)] * w_chunkpos_3(p1,p2,p3,p4,c);

weight w_chunkpos_4: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(t-1,p1) & pos(t,p2) & pos(t+3,p3) & pos(t+4,p4) => chunk(t,t+3,c)] * w_chunkpos_4(p1,p2,p3,p4,c);

weight w_chunkpos_5: Pos x Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p1, Pos p2, Pos p3, Pos p4, Pos p5, Chunk c
  add [pos(t-1,p1) & pos(t,p2) & pos(t+1,p3) & pos(t+2,p4) & pos(t+3,p5) => chunk(t,t+2,c)] 
    * w_chunkpos_5(p1,p2,p3,p4,p5,c);

weight w_chunkpos_6: Pos x Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p1, Pos p2, Pos p3, Pos p4, Pos p5, Chunk c
  add [pos(t-1,p1) & pos(t,p2) & pos(t+1,p3) & pos(t+3,p4) & pos(t+4,p5) => chunk(t,t+3,c)] 
    * w_chunkpos_6(p1,p2,p3,p4,p5,c);

weight w_chunkpos_7: Pos x Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int t, Pos p1, Pos p2, Pos p3, Pos p4, Pos p5, Chunk c
  add [pos(t-1,p1) & pos(t,p2) & pos(t+2,p3) & pos(t+3,p4) & pos(t+4,p5) => chunk(t,t+3,c)]
    * w_chunkpos_7(p1,p2,p3,p4,p5,c);


//for the beginning of the sentence
weight w_chunkpos_b_1: Pos x Pos x Chunk -> Double+;
factor:
  for Pos p1, Pos p2, Chunk c
  add [pos(0,p1) & pos(1,p2) => chunk(0,0,c)] * w_chunkpos_b_1(p1,p2,c);

weight w_chunkpos_b_2: Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Pos p1, Pos p2, Pos p3, Chunk c
  add [pos(0,p1) & pos(1,p2) & pos(2,p3) => chunk(0,1,c)] * w_chunkpos_b_2(p1,p2,p3, c);

weight w_chunkpos_b_3: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(0,p1) & pos(1,p2) & pos(2,p3) & pos(3,p4) => chunk(0,2,c)] * w_chunkpos_b_3(p1,p2,p3,p4,c);
*/
