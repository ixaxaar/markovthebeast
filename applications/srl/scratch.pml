type Path : ... ;
type Label : ... ;
type Pos : ... ;
type Word : ... ;
type Labeller : ... ;
type Voice : ... ;
type Argument : ... ;
type Predicate : ... ;

predicate word: Int x Word;
predicate pos: Int x Pos;
predicate span: Int x Int x Int;
predicate label: Int x Labeller x Label;
predicate head: Int x Labeller x Int;
predicate candidate: Int;
predicate arg: Int x Argument;
predicate path: Int x Labeller x Path;
predicate pred: Int x Predicate x Voice;

//index: span(*,*,_);

hidden: arg;
observed: word,pos,span,label,head,candidate,pred,path;

weight w_path: Path x Labeller x Argument -> Double;
factor: for Int c, Path p, Labeller labeller, Argument a
  if candidate(c) & path(c,labeller,p) add [arg(c,a)] * w_path(p,labeller,a); 

weight w_label: Labeller x Label x Argument -> Double;
factor: for Int c, Label label, Labeller labeller, Argument a
  if candidate(c) & label(c,labeller,label) add [arg(c,a)] * w_label(labeller, label, a); 

weight w_voice: Voice x Argument -> Double;
factor: for Int c, Voice v, Argument a
  if candidate(c) & pred(_,_,v) add [arg(c,a)] * w_voice(v, a);

weight w_pred: Predicate x Argument -> Double;
factor: for Int c, Predicate p, Argument a
  if candidate(c) & pred(_,p,_) add [arg(c,a)] * w_pred(p,a);

weight w_right: Argument -> Double;
factor: for Int c, Argument a, Int t, Int b if candidate(c) & pred(t,_,_) & span(c,b,_) & b > t add [arg(c,a)] * w_right(a);

weight w_left: Argument -> Double;
factor: for Int c, Argument a, Int t, Int b if candidate(c) & pred(t,_,_) & span(c,b,_) & b < t add [arg(c,a)] * w_left(a);  

weight w_head: Word x Labeller x Argument -> Double;
factor: for Int c, Argument a, Int t, Word h, Labeller l if candidate(c) & head(c,l,t) & word(t,h)
  add [arg(c,a)] * w_head(h,l,a);

//no overlaps
factor: for Int c1, Int c2, Int b1, Int e1, Int b2, Int e2, Argument a1, Argument a2
  if span(c1,b1,e1) & span(c2,b2,e2) & b1 < b2 & e1 >= b2 : !(arg(c1,a1) & arg(c2,a2));

//not more than one argument for a candidate
factor: for Int c if candidate(c): |Argument a: arg(c,a)| <= 1;

//not duplicate arguments
factor: |Int c: arg(c,A0)| <= 1;  
factor: |Int c: arg(c,A1)| <= 1;
factor: |Int c: arg(c,A2)| <= 1;
factor: |Int c: arg(c,A3)| <= 1;
factor: |Int c: arg(c,A4)| <= 1;  

load corpus from "one-sentence.crp";

save corpus to ram;

collect;

save corpus to instances "/tmp/srl.instances.dmp";

set learner.solver.integer = true;
set learner.loss = "avgF1";
//set learner.solver.model.initIntegers = true;

learn for 10 epochs;

//next;

print weights;

types to "types.pml";