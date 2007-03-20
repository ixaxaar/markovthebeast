/*
//left or right bounds
//POS|POS|*
weight w_left_1: Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Chunk c
  add [pos(b-1,p1) & pos(b,p2)  => chunk(b,b,c)] * w_left_1(p1,p2,c);

//*|POS|POS
weight w_right_1: Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Chunk c
  add [pos(b,p1) & pos(b+1,p2)  => chunk(b,b,c)] * w_right_1(p1,p2,c);

*/
/*
//boundary features
//POS|POS|POS
weight w_boundary_1: Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Chunk c
  add [pos(b-1,p1) & pos(b,p2) & pos(b+1,p3)  => chunk(b,b,c)] * w_boundary_1(p1,p2,p3,c);

//POS|POS POS|POS
weight w_boundary_2: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(b-1,p1) & pos(b,p2) & pos(b+1,p3) & pos(b+2,p4) => chunk(b,b+1,c)] * w_boundary_2(p1,p2,p3,p4,c);

//POS|POS _ POS|POS
weight w_boundary_3: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(b-1,p1) & pos(b,p2) & pos(b+2,p3) & pos(b+3,p4) => chunk(b,b+2,c)] * w_boundary_3(p1,p2,p3,p4,c);


//POS|POS _ _ POS|POS
weight w_boundary_4: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(b-1,p1) & pos(b,p2) & pos(b+3,p3) & pos(b+4,p4) => chunk(b,b+3,c)] * w_boundary_4(p1,p2,p3,p4,c);

//POS|POS _ _ _ POS|POS
weight w_boundary_5: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(b-1,p1) & pos(b,p2) & pos(b+4,p3) & pos(b+5,p4) => chunk(b,b+4,c)] * w_boundary_5(p1,p2,p3,p4,c);

//POS|POS _ _ _ _ POS|POS
weight w_boundary_6: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(b-1,p1) & pos(b,p2) & pos(b+5,p3) & pos(b+6,p4) => chunk(b,b+5,c)] * w_boundary_6(p1,p2,p3,p4,c);

//POS|POS _ _ _ _ _ POS|POS
weight w_boundary_7: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(b-1,p1) & pos(b,p2) & pos(b+6,p3) & pos(b+7,p4) => chunk(b,b+6,c)] * w_boundary_7(p1,p2,p3,p4,c);
*/
//coarse pos

//CPOS|POS|CPOS
weight w_boundary_1_cpos: Cpos x Pos x Cpos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Cpos cp1, Cpos cp3, Chunk c
  if cpos(p1,cp1) & cpos(p3,cp3)
  add [pos(b-1,p1) & pos(b,p2) & pos(b+1,p3) => chunk(b,b,c)] *
    w_boundary_1_cpos(cp1,p2,cp3,c);

//CPOS|POS POS|CPOS
weight w_boundary_2_cpos: Cpos x Pos x Pos x Cpos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Cpos cp1, Cpos cp4, Chunk c
  if cpos(p1,cp1) & cpos(p4,cp4)
  add [pos(b-1,p1) & pos(b,p2) & pos(b+1,p3) & pos(b+2,p4) => chunk(b,b+1,c)] *
    w_boundary_2_cpos(cp1,p2,p3,cp4,c);


//CPOS|POS _ POS|CPOS
weight w_boundary_3_cpos: Cpos x Pos x Pos x Cpos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Cpos cp1, Cpos cp4, Chunk c
  if cpos(p1,cp1) & cpos(p4,cp4)
  add [pos(b-1,p1) & pos(b,p2) & pos(b+2,p3) & pos(b+3,p4) => chunk(b,b+2,c)] *
    w_boundary_3_cpos(cp1,p2,p3,cp4,c);

//CPOS|POS _ _ POS|CPOS
weight w_boundary_4_cpos: Cpos x Pos x Pos x Cpos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Cpos cp1, Cpos cp4, Chunk c
  if cpos(p1,cp1) & cpos(p4,cp4)
  add [pos(b-1,p1) & pos(b,p2) & pos(b+3,p3) & pos(b+4,p4) => chunk(b,b+3,c)] *
    w_boundary_4_cpos(cp1,p2,p3,cp4,c);

//CPOS|POS _ _ _ POS|CPOS
weight w_boundary_5_cpos: Cpos x Pos x Pos x Cpos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Cpos cp1, Cpos cp4, Chunk c
  if cpos(p1,cp1) & cpos(p4,cp4)
  add [pos(b-1,p1) & pos(b,p2) & pos(b+4,p3) & pos(b+5,p4) => chunk(b,b+4,c)] *
    w_boundary_5_cpos(cp1,p2,p3,cp4,c);

//CPOS|POS _ _ _ _ POS|CPOS
weight w_boundary_6_cpos: Cpos x Pos x Pos x Cpos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Cpos cp1, Cpos cp4, Chunk c
  if cpos(p1,cp1) & cpos(p4,cp4)
  add [pos(b-1,p1) & pos(b,p2) & pos(b+5,p3) & pos(b+6,p4) => chunk(b,b+5,c)] *
    w_boundary_6_cpos(cp1,p2,p3,cp4,c);



/*
//|POS|
weight w_inside: Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Chunk c
  add [pos(b,p1) => chunk(b,b,c)] * w_inside(p1,c);



//|POS POS|
weight w_inside_0: Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Chunk c
  add [pos(b,p1) & pos(b+1,p2)  => chunk(b,b+1,c)] * w_inside_0(p1,p2,c);

*/
/*
//|POS POS POS|
weight w_inside_1: Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Chunk c
  add [pos(b,p1) & pos(b+1,p2) & pos(b+2,p3)  => chunk(b,b+2,c)] * w_inside_1(p1,p2,p3,c);

//|POS POS POS POS|
weight w_inside_2: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(b,p1) & pos(b+1,p2) & pos(b+2,p3) & pos(b+3,p4)  => chunk(b,b+3,c)] * w_inside_2(p1,p2,p3,p4,c);

//|POS POS POS POS POS|
weight w_inside_3: Pos x Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Pos p5, Chunk c
  add [pos(b,p1) & pos(b+1,p2) & pos(b+2,p3) & pos(b+3,p4) & pos(b+4,p5)  => chunk(b,b+4,c)]
   * w_inside_3(p1,p2,p3,p4,p5,c);

//|POS _ POS|
weight w_inside_bounds_1: Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Chunk c
  add [pos(b,p1) & pos(b+2,p2)  => chunk(b,b+2,c)] * w_inside_bounds_1(p1,p2,c);

//|POS _ _ POS|
weight w_inside_bounds_2: Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Chunk c
  add [pos(b,p1) & pos(b+3,p2)  => chunk(b,b+3,c)] * w_inside_bounds_2(p1,p2,c);

//|POS _ _ _ POS|
weight w_inside_bounds_3: Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Chunk c
  add [pos(b,p1) & pos(b+4,p2)  => chunk(b,b+4,c)] * w_inside_bounds_3(p1,p2,c);

//|POS _ _ _ _ POS|
weight w_inside_bounds_4: Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Chunk c
  add [pos(b,p1) & pos(b+5,p2)  => chunk(b,b+5,c)] * w_inside_bounds_4(p1,p2,c);

//|POS _ _ _ _ _ POS|
weight w_inside_bounds_5: Pos x Pos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Chunk c
  add [pos(b,p1) & pos(b+6,p2)  => chunk(b,b+6,c)] * w_inside_bounds_5(p1,p2,c);

*/
//Features that forbid certain pos tags in chunks.

//some pos tags can't appear inside chunks
//|+ POS +|
weight w_forbid_1: Pos x Chunk -> Double-;
factor:
  for Int b, Int e, Int m, Pos p, Chunk c
  if b < m & m < e
  add [pos(m,p) & chunk(b,e,c)] * w_forbid_1(p,c);

//some pos tag binary subsequences can't appear in chunks
//|+ POS POS +|
weight w_forbid_2: Pos x Pos x Chunk -> Double-;
factor:
  for Int b, Int e, Int m, Pos p1, Pos p2, Chunk c
  if b < m & m < e -1
  add [pos(m,p1) & pos(m+1,p2) & chunk(b,e,c)] * w_forbid_2(p1,p2,c);

//some pos tags can't be chunks themselves
//|POS|
weight w_forbid_3: Pos x Chunk -> Double-;
factor:
  for Int b, Pos p, Chunk c
  add [pos(b,p) & chunk(b,b,c)] * w_forbid_3(p,c);

/*
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

