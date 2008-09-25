// if two tokens are to be labelled according to hasLabel(t1,t2) then there exists a role that labels those
//factor [0]: for Int a, Int p if word(a,_) & word(p,_) & possiblePredicate(p) & possibleArgument(a): hasLabel(p,a) => |Role r: role(p,a,r)| >=1;

// if there is a role for two tokens it has a label
factor [0]: for Int a, Int p, Role r if word(a,_) & word(p,_) & possiblePredicate(p) & possibleArgument(a): role(p,a,r) => hasLabel(p,a);


// penalize roles for both the preposition and the modifying token
weight w_pp_forbid: Double-;
factor [0]: for Int p, Int a1, Int a2 if word(p,_) & mst_path(a1,a2,"vPMOD") & possiblePredicate(p) & possibleArgument(a1) & possibleArgument(a2)
  add [hasLabel(p,a1) & hasLabel(p,a2)] * w_pp_forbid;


// soft no overlap 1
// Ask Seb
weight w_overlap1: Double-;
factor[1]: for Int p1, Int a1, Int p2, Int a2
  if // word(p1,_) & word(a1,_) & word(p2,_) & word(a2,_) &
  p1 < p2 & a1 > p2 & a2 > a1 & p1 != p2 & a1 != a2
  add [hasLabel(p1,a1) & hasLabel(p2,a2)] * w_overlap1;



