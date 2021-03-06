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
/*
//CPOS|CPOS|CPOS
weight w_boundary_1_cpos: Cpos x Cpos x Cpos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Cpos cp1, Cpos cp2, Cpos cp3, Chunk c
  if cpos(p1,cp1) & cpos(p2, cp2) & cpos(p3,cp3)
  add [pos(b-1,p1) & pos(b,p2) & pos(b+1,p3) => chunk(b,b,c)] *
    w_boundary_1_cpos(cp1,cp2,cp3,c);


//CPOS|CPOS CPOS|CPOS
weight w_boundary_2_cpos: Cpos x Cpos x Cpos x Cpos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Cpos cp1, Cpos cp4, Chunk c, Cpos cp2, Cpos cp3
  if cpos(p1,cp1) & cpos(p4,cp4) & cpos(p2,cp2) & cpos(p3,cp3)
  add [pos(b-1,p1) & pos(b,p2) & pos(b+1,p3) & pos(b+2,p4) => chunk(b,b+1,c)] *
    w_boundary_2_cpos(cp1,cp2,cp3,cp4,c);


//CPOS|CPOS _ CPOS|CPOS
weight w_boundary_3_cpos: Cpos x Cpos x Cpos x Cpos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Cpos cp1, Cpos cp4, Chunk c, Cpos cp2, Cpos cp3
  if cpos(p1,cp1) & cpos(p4,cp4) & cpos(p2,cp2) & cpos(p3,cp3)
  add [pos(b-1,p1) & pos(b,p2) & pos(b+2,p3) & pos(b+3,p4) => chunk(b,b+2,c)] *
    w_boundary_3_cpos(cp1,cp2,cp3,cp4,c);

//CPOS|CPOS _ _ CPOS|CPOS
weight w_boundary_4_cpos: Cpos x Cpos x Cpos x Cpos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Cpos cp1, Cpos cp4, Chunk c, Cpos cp2, Cpos cp3
  if cpos(p1,cp1) & cpos(p4,cp4) & cpos(p2,cp2) & cpos(p3,cp3)
  add [pos(b-1,p1) & pos(b,p2) & pos(b+3,p3) & pos(b+4,p4) => chunk(b,b+3,c)] *
    w_boundary_4_cpos(cp1,cp2,cp3,cp4,c);

//CPOS|CPOS _ _ _ CPOS|CPOS
weight w_boundary_5_cpos: Cpos x Cpos x Cpos x Cpos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Cpos cp1, Cpos cp4, Chunk c, Cpos cp2, Cpos cp3
  if cpos(p1,cp1) & cpos(p4,cp4) & cpos(p2,cp2) & cpos(p3,cp3)
  add [pos(b-1,p1) & pos(b,p2) & pos(b+4,p3) & pos(b+5,p4) => chunk(b,b+4,c)] *
    w_boundary_5_cpos(cp1,cp2,cp3,cp4,c);

//CPOS|CPOS _ _ _ _ CPOS|CPOS
weight w_boundary_6_cpos: Cpos x Cpos x Cpos x Cpos x Chunk -> Double+;
factor:
  for Int b, Pos p1, Pos p2, Pos p3, Pos p4, Cpos cp1, Cpos cp4, Chunk c, Cpos cp2, Cpos cp3
  if cpos(p1,cp1) & cpos(p4,cp4) & cpos(p2,cp2) & cpos(p3,cp3)
  add [pos(b-1,p1) & pos(b,p2) & pos(b+5,p3) & pos(b+6,p4) => chunk(b,b+5,c)] *
    w_boundary_6_cpos(cp1,cp2,cp3,cp4,c);

*/
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


