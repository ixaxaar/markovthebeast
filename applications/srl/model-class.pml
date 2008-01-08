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
predicate class: Class;
predicate nooverlap: Int x Int;

//global predicates
predicate properarg: Argument;
predicate modifier: Argument;
predicate carg: Argument;
predicate rarg: Argument;
predicate cargpair: Argument x Argument;
predicate rargpair: Argument x Argument;

predicate arg: Int x Argument;
predicate isarg: Int;
predicate allargs: Argument;

//index: span(*,*,_);

hidden: arg,isarg,class;
observed: word,pos,span,label,head,candidate,pred,path,subcat,position,pathlength,shortframe,
  frame,chunkdistance,framepattern,parentlabel,parenthead,sister,pprightmosthead,distance;
global: properarg, modifier, carg, rarg, cargpair, rargpair,allargs;


include "weights-arg.pml";
include "weights-isarg.pml";
include "weights-class.pml";
//include "weights-isarg-compact.pml";

load global from "global.atoms";

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
factor duplicatearg: for Argument a if properarg(a) : |Int c: arg(c,a)| <= 1;

//if there is a c-arg there has to be an arg
factor cargimpliesarg: for Argument start, Argument ca, Int c, Int bc
  if carg(ca) & cargpair(ca,start) & candidate(c) & span(c,bc,_) :
  arg(c,ca) => |Int a, Int ba: candidate(a) & span(a,ba,_) & ba < bc & arg(a,start)| >= 1;

factor rargimpliesarg: for Argument start, Argument ra, Int c
  if carg(ra) & rargpair(ra,start) & candidate(c) :
  arg(c,ra) => |Int a: candidate(a) & arg(a,start)| >= 1;


//at least one argument
//factor atLeastOne: |Int c, Argument a: candidate(c) & arg(c,a)| >= 1;


  
