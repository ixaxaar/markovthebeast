/*
 * The following formulas ensure consistency if an isPredicate predicate is used
 */


// if a token p is a predicate there must be a token pair (p,a) that has a label
//factor[0]: for Int p if word(p,_) & possiblePredicate(p): 
//  isPredicate(p) => |Int a: possibleArgument(a) & palmer(p,a) & hasLabel(p,a)| >= 1;

// if a tokenpair is labelled the first token must be a predicate
factor: for Int p, Int a if word(a,_) & word(p,_) & possiblePredicate(p) : hasLabel(p,a) => isPredicate(p);
