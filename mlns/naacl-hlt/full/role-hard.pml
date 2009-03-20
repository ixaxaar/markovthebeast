// a token can only have one role wrt to one predicate
factor [0] : for Int a, Int p if word(a,_) & word(p,_) & possibleArgument(a) & possiblePredicate(p) : |Role r: role(p,a,r)| <= 1;


//a token can have only one argument for each proper argument type
factor [1]: for Int p, Role r if word(p,_) & properarg(r) & possiblePredicate(p) : |Int a: possibleArgument(a) &  role(p,a,r)| <= 1;





