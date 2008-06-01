predicate word:   Int x Word;
predicate prefix: Int x Prefix;
predicate pos:    Int x Pos;
predicate cpos:   Int x Cpos;
predicate link:   Int x Int;
predicate dep:    Int x Int x Dep;

index: dep(*,*,_);
index: link(*,*);

