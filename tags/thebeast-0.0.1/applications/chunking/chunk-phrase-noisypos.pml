//noisypos features
weight w_noisypos_i: Pos x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Pos w_i
  if noisypos(b,_) & noisypos(e,_) & noisypos(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_noisypos_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_noisypos: Pos x Pos x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Pos w1, Pos w2
  if noisypos(b,_) & noisypos(e,_) & m >= b & m <= e - 1 & noisypos(m,w1) & noisypos(m+1,w2)
  add [chunk(b,e,c)] * w_bi_noisypos(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_noisypos: Pos x Pos x Pos x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Pos w1, Pos w2, Pos w3
  if noisypos(b,_) & noisypos(e,_) & m >= b & m <= e - 2 & noisypos(m,w1) & noisypos(m+1,w2) & noisypos(m+2,w3)
  add [chunk(b,e,c)] * w_tri_noisypos(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_noisypos: Pos x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Pos w
  if noisypos(b,_) & noisypos(e,_) & e >= b & noisypos(b-1,w)
  add [chunk(b,e,c)] * w_before_noisypos(w,bins(0,5,10,e-b),c);

weight w_after_noisypos: Pos x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Pos w
  if noisypos(b,_) & noisypos(e,_) & e >= b & noisypos(e+1,w)
  add [chunk(b,e,c)] * w_after_noisypos(w,bins(0,5,10,e-b),c);

//noisycpos features
weight w_noisycpos_i: Noisycpos x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Noisycpos w_i
  if noisycpos(b,_) & noisycpos(e,_) & noisycpos(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_noisycpos_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_noisycpos: Noisycpos x Noisycpos x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Noisycpos w1, Noisycpos w2
  if noisycpos(b,_) & noisycpos(e,_) & m >= b & m <= e - 1 & noisycpos(m,w1) & noisycpos(m+1,w2)
  add [chunk(b,e,c)] * w_bi_noisycpos(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_noisycpos: Noisycpos x Noisycpos x Noisycpos x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Noisycpos w1, Noisycpos w2, Noisycpos w3
  if noisycpos(b,_) & noisycpos(e,_) & m >= b & m <= e - 2 & noisycpos(m,w1) & noisycpos(m+1,w2) & noisycpos(m+2,w3)
  add [chunk(b,e,c)] * w_tri_noisycpos(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_noisycpos: Noisycpos x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Noisycpos w
  if noisycpos(b,_) & noisycpos(e,_) & e >= b & noisycpos(b-1,w)
  add [chunk(b,e,c)] * w_before_noisycpos(w,bins(0,5,10,e-b),c);

weight w_after_noisycpos: Noisycpos x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Noisycpos w
  if noisycpos(b,_) & noisycpos(e,_) & e >= b & noisycpos(e+1,w)
  add [chunk(b,e,c)] * w_after_noisycpos(w,bins(0,5,10,e-b),c);
