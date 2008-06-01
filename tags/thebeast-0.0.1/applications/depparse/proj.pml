include "nonproj.pml";

//projectivity
factor overlap1:
  for Int h1, Int m1, Int h2, Int m2
  if h1 < h2 & m1 > h2 & m2 > m1 :!(link(h1,m1) & link(h2,m2));

factor overlap2:
  for Int h1, Int m1, Int h2, Int m2
  if h1 > h2 & m1 < h2 & m2 > h1 :!(link(h1,m1) & link(h2,m2));
