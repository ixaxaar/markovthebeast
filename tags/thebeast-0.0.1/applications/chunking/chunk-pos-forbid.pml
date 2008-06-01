//|+ POS +|
weight w_forbid_1: Pos x Chunk -> Double-;
factor:
  for Int b, Int e, Int m, Pos p, Chunk c
  if b < m & m < e
  add [pos(m,p) & chunk(b,e,c)] * w_forbid_1(p,c);

//some pos tag binary subsequences can't appear in chunks
//|POS POS|
weight w_forbid_2: Pos x Pos x Chunk -> Double-;
factor:
  for Int b, Int e, Int m, Pos p1, Pos p2, Chunk c
  if b <= m & m <= e - 1 
  add [pos(m,p1) & pos(m+1,p2) & chunk(b,e,c)] * w_forbid_2(p1,p2,c);

/*
//some pos tags can't be chunks themselves
//|POS|
weight w_forbid_3: Pos x Chunk -> Double-;
factor:
  for Int b, Pos p, Chunk c
  add [pos(b,p) & chunk(b,b,c)] * w_forbid_3(p,c);

//some tags can't be around a chunk
//POS | * | *
weight w_forbid_4: Pos x Chunk -> Double-;
factor:
  for Int b, Int e, Pos p, Chunk c
  add [pos(b-1,p) & chunk(b,e,c)] * w_forbid_4(p,c);

// * | * | POS
weight w_forbid_5: Pos x Chunk -> Double-;
factor:
  for Int b, Int e, Pos p, Chunk c
  add [pos(e+1,p) & chunk(b,e,c)] * w_forbid_5(p,c);

//POS | * | POS
weight w_forbid_6: Pos x Pos x Chunk -> Double-;
factor:
  for Int b, Int e, Pos p1, Pos p2, Chunk c
  add [pos(b-1,p1) & pos(e+1,p2) & chunk(b,e,c)] * w_forbid_6(p1,p2,c);

*/
