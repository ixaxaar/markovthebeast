//bias and pos tag of predicate
weight w_role_bias_ppos: Role x Ppos -> Double;
factor: for Int a, Int p, Role r, Ppos cp if ppos(p,cp) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_bias_ppos(r,cp);

set collector.all.w_role_bias_ppos = true;
