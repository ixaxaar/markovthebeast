//explicit rules
weight w_explicit_1: Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int i, Pos p1, Pos p2, Pos p3, Chunk c
  add [pos(i-1,p1) & pos(i,p2) & pos(i+1,p3) => chunk(i,i,c)] * w_explicit_1(p1,p2,p3,c);


weight w_explicit_2: Pos x Pos x Pos x Pos x Chunk -> Double+;
factor:
  for Int i, Pos p1, Pos p2, Pos p3, Pos p4, Chunk c
  add [pos(i,p1) & pos(i+1,p2) & pos(i+2,p3) & pos(i+3,p4) => chunk(i+1,i+2,c)] * w_explicit_1(p1,p2,p3,p4,c);

weight w_explicit_2_c: Cpos x Cpos x Cpos x Cpos x Chunk -> Double+;
factor:
  for Int i, Pos p1, Pos p2, Pos p3, Pos p4, Cpos cp1, Cpos cp2, Cpos cp3, Cpos cp4, Chunk c
  if cpos(p1,cp1) & cpos(p2,cp2) & cpos(p3,cp3) & cpos(p4,cp4)
  add [pos(i,p1) & pos(i+1,p2) & pos(i+2,p3) & pos(i+3,p4) => chunk(i+1,i+2,c)] * w_explicit_2_c(cp1,cp2,cp3,cp4,c);  