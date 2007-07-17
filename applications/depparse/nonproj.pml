//These factors ensure consistency between unlabelled and labelled edges
factor label_geq1:
  for Int h, Int m if word(h,_) & word(m,_): link(h,m) => |Dep d: dep(h,m,d)| >= 1;

factor label_leq1:
  for Int h, Int m if word(h,_) & word(m,_): |Dep d: dep(h,m,d)| <= 1;

factor dep_link:
  for Int h, Int m, Dep d : dep(h,m,d) => link(h,m);

//These factors ensure we have exactly one head
factor head_geq1:
  for Int m if m > 0 & word(m,_): |Int h: word(h,_) & link(h,m)| >= 1;

factor head_leq1:
  for Int m if m > 0 & word(m,_): |Int h: word(h,_) & link(h,m)| <= 1;

//forbid cyles
factor nocycles:
  link acyclic;

