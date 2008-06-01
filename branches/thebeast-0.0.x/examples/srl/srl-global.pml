// this formula ensures that an argument cannot be have more than one role wrt to
// one predicate.
factor: for Int p, Int a if word(p,_) & word(a,_) : |Role r: role(p,a,r)| <=1;

// this formula ensures that an SRL predicate cannot have more than one argument with
// a unique role, such as "A0"
factor: for Int p, Role r if word(p,_) & unique(r) : |Int a: word(a,_) & role(p,a,r)| <=1;
