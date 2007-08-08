predicate word: Int x Word;
predicate pos: Int x Pos;
predicate span: Int x Int x Int;
predicate label: Int x Label;
predicate parentlabel: Int x Label;
predicate head: Int x Int;
predicate parenthead: Int x Int;
predicate candidate: Int;
predicate path: Int x Path;
predicate pathlength: Int x Int;
predicate pred: Int x Predicate x Voice;
predicate subcat: Subcat;
predicate position: Int x Position;
predicate frame: Int x Frame;
predicate framepattern: Int x FramePattern;
predicate shortframe: Int x ShortFrame;
predicate chunkdistance: Int x Int;
predicate sister: Int x Int x Int;
predicate pprightmosthead: Int x Int;
predicate distance: Int x Int;


predicate arg: Int x Argument;
predicate isarg: Int;

//index: span(*,*,_);

hidden: arg,isarg;
observed: word,pos,span,label,head,candidate,pred,path,subcat,position,pathlength,shortframe,
  frame,chunkdistance,framepattern,parentlabel,parenthead,sister,pprightmosthead,distance;

include "weights-arg.pml";
include "weights-isarg.pml";
//include "weights-isarg-compact.pml";


//weight w_argpair: Argument x Argument -> Double-;
//factor argpair:
//  for Int c1, Int c2, Argument a1, Argument a2, Int b1, Int b2
//  if candidate(c1) & candidate(c2) & span(c1,b1,_) & span(c2,b2,_) & b2 > b1
//  add [arg(c1,a1) & arg(c2,a2)] * w_argpair(a1,a2);
//set collector.all.w_argpair = true;

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

//no overlaps
factor overlap1: for Int c1, Int c2, Int b1, Int e1, Int b2, Int e2, Argument a1, Argument a2
  if span(c1,b1,e1) & span(c2,b2,e2) & b1 < b2 & e1 >= b2 : !(arg(c1,a1) & arg(c2,a2));

factor overlap2: for Int c1, Int c2, Int b1, Int e1, Int b2, Int e2
  if span(c1,b1,e1) & span(c2,b2,e2) & b1 < b2 & e1 >= b2 : !(isarg(c1) & isarg(c2));


//not more than one argument for a candidate
factor atMostOneArg: for Int c if candidate(c): |Argument a: arg(c,a)| <= 1;

//if there is an isarg there has to be an arg
factor implyArg: for Int c if candidate(c): isarg(c) => |Argument a: arg(c,a)| >= 1;

//if there is an arg there has to be a isarg
//factor implyIsarg: for Int c, Argument a if candidate(c): arg(c,a) => isarg(c);

//factor implyIsarg: for Int c if candidate(c): |Argument a: arg(c,a)| >= 1 => isarg(c);

//not duplicate arguments
//factor: |Int c: arg(c,"V")| <= 1;
factor: |Int c: arg(c,"A0")| <= 1;
factor: |Int c: arg(c,"A1")| <= 1;
factor: |Int c: arg(c,"A2")| <= 1;
factor: |Int c: arg(c,"A3")| <= 1;
factor: |Int c: arg(c,"A4")| <= 1;

//factor: |Int c: candidate(c) & arg(c,"V")| >= 1;



  
