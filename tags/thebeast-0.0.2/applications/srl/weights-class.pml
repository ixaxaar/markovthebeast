weight w_class_bias: Class -> Double;
factor: for Class c add [class(c)] * w_class_bias(c);

weight w_class_pred: Class x Predicate -> Double;
factor: for Class c, Predicate p if pred(_,p,_) add [class(c)] * w_class_pred(c,p);

weight w_class_subcat: Class x Predicate x Subcat-> Double;
factor: for Class c, Predicate p, Subcat s if pred(_,p,_) & subcat(s)
  add [class(c)] * w_class_subcat(c,p,s);

//weight w_classarg: Argument x Predicate x Class -> Double+;
//factor[2]: for Int n, Argument a, Predicate p, Class c if candidate(n) & pred(_,p,_) 
//  add [arg(n,a) => class(c)] * w_classarg(a,p,c);

//weight w_argclass: Argument x Predicate x Class -> Double+;
//factor[2]: for Int n, Argument a, Predicate p, Class c if candidate(n) & pred(_,p,_)
//  add [isarg(n) & class(c) => arg(n,a)] * w_argclass(a,p,c);

//weight w_forbidargclass: Argument x Predicate x Class -> Double-;
//factor[2]: for Int n, Argument a, Predicate p, Class c if candidate(n) & pred(_,p,_)
//  add [class(c) & arg(n,a)] * w_forbidargclass(a,p,c);

//set collector.all.w_forbidargclass = true;

//weight w_forbidargclass_undef: Double-;
//factor[2]: for Int n, Argument a, Predicate p, Class c
//  if candidate(n) & pred(_,p,_) & undefined(w_forbidargclass(a,p,c))
//  add [class(c) & arg(n,a)] * w_forbidargclass_undef;

factor: |Class c: class(c)| >= 1;
factor: |Class c: class(c)| <= 1;

