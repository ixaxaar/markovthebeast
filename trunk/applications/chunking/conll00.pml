predicate word:        Int x Word;
predicate count:       Int x Int;
predicate hyphen:      Int x Hyphen;
predicate case:        Int x Case;
predicate cardinal:    Int x Cardinal;
predicate pos:         Int x Pos;
predicate chunk:       Int x Int x Chunk;
predicate highestfreq: Int x Int x Int;

predicate prefix1:   Int x Prefix1;
predicate postfix1:  Int x Postfix1;
predicate prefix2:   Int x Prefix2;
predicate postfix2:  Int x Postfix2;
predicate prefix3:   Int x Prefix3;
predicate postfix3:  Int x Postfix3;
predicate prefix4:   Int x Prefix4;
predicate postfix4:  Int x Postfix4;

type Inset : Yes,No;

predicate name: Int x Inset;
predicate firstname: Int x Inset;
predicate lastname: Int x Inset;
predicate orgname: Int x Inset;
predicate placename: Int x Inset;
predicate company: Int x Inset;
predicate stopword: Int x Inset;

index: chunk(*,*,_);
index: highestfreq(*,*,_);
