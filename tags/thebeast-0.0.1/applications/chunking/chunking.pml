//forbid any kind of overlaps
factor overlap1:
  for Int b1, Int e1, Int b2, Int e2, Chunk c1, Chunk c2
  if e2 > e1 & b2 <= e1: !(chunk(b1,e1,c1) & chunk(b2,e2,c2));

factor overlap2:
  for Int b1, Int e1, Int b2, Int e2, Chunk c1, Chunk c2
  if b1 < b2 & e1 >= b2: !(chunk(b1,e1,c1) & chunk(b2,e2,c2));
