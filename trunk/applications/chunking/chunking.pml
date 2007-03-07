//forbid any kind of overlaps
factor:
  for Int b1, Int e1, Int b2, Int e2, Chunk c1, Chunk c2
  if e2 >= e1 & b2 <= e1 & b1 != b2 & c1 != c2:!(chunk(b1,e1,c1) & chunk(b2,e2,c2));

//we need to test e1 != e2, too, because we don't support disjunctions yet. (or do we?)
factor:
  for Int b1, Int e1, Int b2, Int e2, Chunk c1, Chunk c2
  if e2 >= e1 & b2 <= e1 & e1 != e2 & c1 != c2:!(chunk(b1,e1,c1) & chunk(b2,e2,c2));
