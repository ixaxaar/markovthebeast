//token features
predicate word:   Int x Word;
predicate pos:    Int x Pos;

//role label for target # and constituent
predicate arg:    Int x Phrase x Arg;

//path between target # and phrase
predicate path:   Int x Phrase x Path;

//type of constituents
predicate type:   Phrase x Label;

//beginning and end of constituent
predicate begin:  Phrase x Int;
predicate end:    Phrase x Int;

//token indices of target verbs
predicate target: Int;
