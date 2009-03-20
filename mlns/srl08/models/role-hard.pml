// a token can only have one role wrt to one predicate
factor [0] : for Int a, Int p if word(a,_) & word(p,_) & possibleArgument(a) & possiblePredicate(p) : |Role r: role(p,a,r)| <= 1;


//a token can have only one argument for each proper argument type
factor [1]: for Int p, Role r if word(p,_) & properarg(r) & possiblePredicate(p) : |Int a: possibleArgument(a) &  role(p,a,r)| <= 1;

//factor [2]: for Int p, Int ca, Role rca, Role ra
//  if rarg(rca) & rargpair(rca,ra) :
//  role(p,ca,rca) => |Int a: possibleArgument(a) & palmer(p,a) & role(p,a,ra) | >= 1; 


//factor [2]: for Int p, Int ca, Role rca, Role ra
//  if carg(rca) & cargpair(rca,ra) :
//  role(p,ca,rca) => |Int a: possibleArgument(a) & palmer(p,a) & role(p,a,ra) | >= 1;



