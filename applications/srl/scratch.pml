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

hidden: arg;
observed: word,pos,span,label,head,candidate,pred;

load corpus from "one-sentence.crp";

save corpus to ram;

//next;

types to "types.pml";