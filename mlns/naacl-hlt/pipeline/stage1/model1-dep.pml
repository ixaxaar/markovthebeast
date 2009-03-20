hidden: dep,link;

/* add index for dep predicate for faster processing */
index: dep(*,*,_);

/* hard constraints */
//exactly one head
factor: for Int m if word(m,_) & m > 0: |Int h: link(h,m) & word(h,_)| <= 1;
factor: for Int m if word(m,_) & m > 0: |Int h: link(h,m) & word(h,_)| >= 1;

/* No overlaps */
factor [1]:
  for Int h1, Int m1, Int h2, Int m2
  if h1 < h2 & m1 > h2 & m2 > m1 :!(link(h1,m1) & link(h2,m2));

factor [1]:
  for Int h1, Int m1, Int h2, Int m2
  if h1 > h2 & m1 < h2 & m2 > h1 :!(link(h1,m1) & link(h2,m2));

/* Exactly one label */
factor: for Int h, Int m if word(h,_) & word(m,_): |Dependency d: dep(h,m,d)| <= 1;
factor [1]: for Int h, Int m if word(h,_) & word(m,_): link(h,m) => |Dependency d: dep(h,m,d)| >= 1;

/* No cycles */
//factor nocycles: link acyclic;

/* local features */
include "dep-bigram.pml";
include "dep-malt.pml";