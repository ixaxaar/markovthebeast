
//weight w_argpair: Argument x Argument -> Double-;
//factor argpair:
//  for Int c1, Int c2, Argument a1, Argument a2, Int b1, Int b2
//  if candidate(c1) & candidate(c2) & span(c1,b1,_) & span(c2,b2,_) & b2 > b1
//  add [arg(c1,a1) & arg(c2,a2)] * w_argpair(a1,a2);
//set collector.all.w_argpair = true;



/*
weight w_duplicatemod: Argument -> Double-;
factor duplicatemod:
  for Int c1, Int c2, Argument a1, Int b1, Int b2
  if candidate(c1) & candidate(c2) & modifier(a1) & span(c1,b1,_) & span(c2,b2,_) & b2 > b1
  add [arg(c1,a1) & arg(c2,a1)] * w_duplicatemod(a1);
set collector.all.w_duplicatemod = true;
*/
/*
weight w_duplicatearg: Argument -> Double-;
factor duplicatearg:
  for Int c1, Int c2, Argument a1, Int b1, Int b2
  if candidate(c1) & candidate(c2) & span(c1,b1,_) & span(c2,b2,_) & b2 > b1
  add [arg(c1,a1) & arg(c2,a1)] * w_duplicatearg(a1);
set collector.all.w_duplicatearg = true;


weight w_argpairvoice: Argument x Argument x Voice -> Double-;
factor argpairvoice:
  for Int c1, Int c2, Argument a1, Argument a2, Int b1, Int b2, Voice v
  if candidate(c1) & candidate(c2) & span(c1,b1,_) & span(c2,b2,_) & b2 > b1 & pred(_,_,v)
  add [arg(c1,a1) & arg(c2,a2)] * w_argpairvoice(a1,a2,v);
set collector.all.w_argpairvoice = true;

weight w_argpairvoicepred: Argument x Argument x Predicate x Voice -> Double-;
factor argpairvoicepred:
  for Int c1, Int c2, Argument a1, Argument a2, Int b1, Int b2, Voice v, Predicate p
  if candidate(c1) & candidate(c2) & span(c1,b1,_) & span(c2,b2,_) & b2 > b1 & pred(_,p,v)
  add [arg(c1,a1) & arg(c2,a2)] * w_argpairvoicepred(a1,a2,p,v);
set collector.all.w_argpairvoicepred = true;
*/