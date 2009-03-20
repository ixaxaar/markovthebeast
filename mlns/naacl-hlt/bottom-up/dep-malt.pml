weight w_link_malt: Double;
factor:
  for Int h, Int m
  if mst_link(h,m)
  add [link(h,m)] * w_link_malt;

weight w_dep_malt: Double;
factor:
  for Int h, Int m, MDependency mst_d, Dependency d
  if mst_dep(h,m,mst_d)
  add [dep(h,m,d)] * w_dep_malt;

weight w_dep_malt_d: MDependency x Dependency -> Double;
factor:
  for Int h, Int m, MDependency mst_d, Dependency d
  if mst_dep(h,m,mst_d)
  add [dep(h,m,d)] * w_dep_malt_d(mst_d,d);


