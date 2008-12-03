/* Hidden predicates */
predicate coref: Phrase x Phrase;
predicate label: Phrase x Class;
predicate anaphoric: Phrase;

/* Observed predicates */
predicate isNounPhrase: Phrase;
predicate isUpperCase: Phrase;
predicate distance: Phrase x Phrase x Int;
predicate isFirstPhrase: Phrase;
predicate asWord: Phrase x Word;

hidden: coref, label;

weight w_distance: Integer -> Double;
factor: for Phrase p1, Phrase p2, Int dist if distance(p1,p2,dist) add [coref(p1,p2)] * w_distance(dist); 

weight w_fp_distance: Integer -> Double;
factor: for Phrase p1, Phrase p2, Int dist if distance(p1,p2,dist) & firstPhrase(p1) add [coref(p1,p2)] * w_fp_distance(dist); 

weight w_uppercase: Class -> Double;
factor: for Phrase p, Class c if upperCase(p) add [label(p,c)] * w_uppercase(c);

factor: for Phrase p: |Class c: label(p,c)| >= 1;
factor: for Phrase p: |Class c: label(p,c)| <= 1;

weight w_global1: Class -> Double+;
factor: for Phrase p1, Phrase p2, Class c add [label(p1,c) & label(p2,c) => coref(p1,p2)] * w_global1(c);

factor: for Phrase p1, Phrase p2, Phrase p3: coref(p1,p2) & coref(p2,p3) => coref(p1,p3);

factor: for Phrase p1, Phrase p2, Class c1: label(p1,c1) & coref(p1,p2) => label(p2,c1);

factor: for Phrase p: anaphoric(p) => |Phrase antecedent: coref(antecedent, p)| => 1;