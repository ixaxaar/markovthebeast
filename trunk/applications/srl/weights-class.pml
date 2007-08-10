weight w_class_bias: Class -> Double;
factor: for Class c add [class(c)] * w_class_bias(c);

weight w_class_pred: Class x Predicate -> Double;
factor: for Class c, Predicate p if pred(_,p,_) add [class(c)] * w_class_pred(c,p);

weight w_class_subcat: Class x Predicate x Subcat-> Double;
factor: for Class c, Predicate p, Subcat s if pred(_,p,_) & subcat(s)
  add [class(c)] * w_class_subcat(c,p,s);


factor: |Class c: class(c)| >= 1;
factor: |Class c: class(c)| <= 1;

