weight w_link_malt: Double;
factor:
  for Int h, Int m
  if m_link(h,m)
  add [link(h,m)] * w_link_malt;

weight w_dep_malt: Double;
factor:
  for Int h, Int m, MDependency m_d, Dependency d
  if m_dep(h,m,m_d)
  add [dep(h,m,d)] * w_dep_malt;

weight w_dep_malt_d: MDependency x Dependency -> Double;
factor:
  for Int h, Int m, MDependency m_d, Dependency d
  if m_dep(h,m,m_d)
  add [dep(h,m,d)] * w_dep_malt_d(m_d,d);


