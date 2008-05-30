// a token can only have one role wrt to one predicate
factor [0] : for Int a, Int p if word(a,_) & word(p,_): |Role r: role(p,a,r)| <= 1;

//if NONE labels are used each token pair must have a label
//factor: for Int a, Int p if word(a,_) & word(p,_): |Role r: role(p,a,r)| >= 1;

//a token can have only one argument for each proper argument type
factor [1]: for Int p, Role r if word(p,_) & properarg(r): |Int a: role(p,a,r)| <= 1;

/*
factor [1]: for Int p, Int ca, Role rca, Role ra
  if rarg(rca) & rargpair(rca,ra) :
  role(p,ca,rca) => |Int a: possibleArgument(a) & palmer(p,a) & role(p,a,ra) | >= 1; 


factor [1]: for Int p, Int ca, Role rca, Role ra
  if carg(rca) & cargpair(rca,ra) :
  role(p,ca,rca) => |Int a: possibleArgument(a) & palmer(p,a) & role(p,a,ra) | >= 1;

*/
//an argument can not be the modifier of another argument of the same predicate
//joint version
//factor [0]: for Int p, Int a1, Int a2, Role r1, Role2
//  if word(p,_) & word(a1,_) & word(a2,_): !(role(p,a1,r1) & link(a1,a2) & role(p,a2,r2));

//malt version
//factor [1]: for Int p, Int a1, Int a2, Role r1, Role r2
//  if word(p,_) & word(a1,_) & word(a2,_) & m_path_directed(a1,a2,_): !(role(p,a1,r1) & role(p,a2,r2));


//soft malt version
/*
weight w_parentrule: Double-;
factor [1]: for Int p, Int a1, Int a2, Role r1, Role r2
  if word(p,_) & word(a1,_) & word(a2,_) & m_path_directed(a1,a2,_)
  add [role(p,a1,r1) & role(p,a2,r2)] * w_parentrule;
*/




