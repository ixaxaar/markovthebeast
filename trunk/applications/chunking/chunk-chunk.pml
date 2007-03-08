weight w_ch_ch : Chunk x Chunk -> Double-;
factor:
  for Int b, Int m, Int e, Chunk c_1, Chunk c_2
  add [chunk(b,m,c_1) & chunk(m+1,e, c_2)] * w_ch_ch(c_1,c_2);