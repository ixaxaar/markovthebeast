/*
 * The following formulas ensure consistency if an isArgument predicate is used
 */


// if a token a is an argument there must be a token pair (p,a) that has a label

//factor[0]: for Int a if word(a,_) & possibleArgument(a):
//  isArgument(a) => |Int p: possiblePredicate(p) & hasLabel(p,a)| >= 1;

// if a tokenpair is labelled the second token must be an argument
factor: for Int p, Int a if word(a,_) & word(p,_) & possiblePredicate(p) & possibleArgument(a) : hasLabel(p,a) => isArgument(a);
