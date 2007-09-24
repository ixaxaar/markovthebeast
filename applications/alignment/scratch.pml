type Word: ... ;
type BoolFeature: ... ;
type IntFeature: ... ; 

predicate align: Int x Int;
predicate source: Int x Word;
predicate target: Int x Word;

hidden: align;
observed: source, target;

