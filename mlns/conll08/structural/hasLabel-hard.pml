// if two tokens are to be labelled according to hasLabel(t1,t2) then there exists a role that labels those
factor [0]: for Int a, Int p if word(a,_) & word(p,_): hasLabel(p,a) => |Role r: role(p,a,r)| >=1;

// if there is a role for two tokens it has a label
factor [0]: for Int a, Int p, Role r if word(a,_) & word(p,_): role(p,a,r) => hasLabel(p,a);

/*
// forbid roles of both parent and child
factor [0]: for Int p, Int a1, Int a2
  if word(p,_) & word(a1,_) & word(a2,_) & m_path_directed(a1,a2,_):
  !(hasLabel(p,a1) & hasLabel(p,a2));
*/


// penalize roles for both the preposition and the modifying token
//weight w_pp_forbid: Double-;
//factor [0]: for Int p, Int a1, Int a2 if word(p,_) & m_path(a1,a2,"vPMOD")
//  add [hasLabel(p,a1) & hasLabel(p,a2)] * w_pp_forbid;

 

/*
weight w_forbid_attach: MDependency x MDependency -> Double-;
factor: for Int p, Int a, Role r, Int m, MDependency dpa, MDependency dam if m_dep(p,a,dpa) & m_dep(a,m,dam) 
  add [role(p,a,r) & role(p,m,r)] * w_forbid_attach(dpa,dam);

set collector.all.w_forbid_attach = true;
*/



// soft no overlap 1
//weight w_overlap1: Double-;
//factor[1]: for Int p1, Int a1, Int p2, Int a2
//  if // word(p1,_) & word(a1,_) & word(p2,_) & word(a2,_) &
//  p1 < p2 & a1 > p2 & a2 > a1 & p1 != p2 & a1 != a2
//  add [hasLabel(p1,a1) & hasLabel(p2,a2)] * w_overlap1;

/*

// penalize roles for two cordinated tokens
weight w_cc_forbid: Double-;
factor [0]: for Int p, Int a1, Int a2 if word(p,_) & m_path(a1,a2,"vCOORDvCONJ")
  add [hasLabel(p,a1) & hasLabel(p,a2)] * w_cc_forbid;
*/

/*

// penalize roles of both parent and child
weight w_parentrule: Double-;
factor [0]: for Int p, Int a1, Int a2
  if word(p,_) & word(a1,_) & word(a2,_) & m_path_directed(a1,a2,_)
  add [hasLabel(p,a1) & hasLabel(p,a2)] * w_parentrule;

*/


/*
weight w_parentrule_path: MPath -> Double-;
factor [0]: for Int p, Int a1, Int a2, MPath path
  if word(p,_) & word(a1,_) & word(a2,_) & m_path_directed(a1,a2,path)
  add [hasLabel(p,a1) & hasLabel(p,a2)] * w_parentrule_path(path);

set collector.all.w_parentrule_path = true;
*/


/*
weight w_overlap2: Double-;
factor[1]: for Int p1, Int a1, Int p2, Int a2
  if // word(p1,_) & word(a1,_) & word(p2,_) & word(a2,_) &
  p1 < p2 & a1 > p2 & a2 < p1 & p1 != p2 & a1 != a2
  add [hasLabel(p1,a1) & hasLabel(p2,a2)] * w_overlap2;
*/

/*

  for Int h1, Int m1, Int h2, Int m2
  if h1 > h2 & m1 < h2 & m2 > h1 :!(link(h1,m1) & link(h2,m2));



 for Int h1, Int m1, Int h2, Int m2
  if h1 < h2 & m1 > h2 & m2 > m1 :!(link(h1,m1) & link(h2,m2));

*/
