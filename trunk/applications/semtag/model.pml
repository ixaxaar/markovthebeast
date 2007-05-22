include "corpora/atis_2_3.dev.types.pml";
hidden: slot;
observed: word, pos;

weight w_bias: Slot -> Double-;
factor: for Int t,Slot s if word(t,_) add [slot(t,s)]*w_bias(s);

weight w_word: Word x Slot -> Double;
factor: for Int t, Word w, Slot s if word(t,w) add [slot(t,s)]*w_word(w,s);
set collector.all.w_word = true;

weight w_pos: POS x Slot -> Double;
factor: for Int t, Slot s, POS p if pos(t,p) add [slot(t,s)]*w_pos(p,s);
set collector.all.w_pos = true;

weight w_word_p1: Word x Slot -> Double;
factor: for Int t, Word w, Slot s if word(t-1,w) & word(t,_) add [slot(t,s)]*w_word_p1(w,s);
set collector.all.w_word_p1 = true;

weight w_pos_p1: POS x Slot -> Double;
factor: for Int t, Slot s, POS p if pos(t-1,p) & word(t,_) add [slot(t,s)]*w_pos_p1(p,s);
set collector.all.w_pos_p1 = true;

weight w_word_n1: Word x Slot -> Double;
factor: for Int t, Word w, Slot s if word(t+1,w)& word(t,_)  add [slot(t,s)]*w_word_n1(w,s);
set collector.all.w_word_n1 = true;


weight w_pos_n1: POS x Slot -> Double;
factor: for Int t, Slot s, POS p if pos(t+1,p) & word(t,_) add [slot(t,s)]*w_pos_n1(p,s);
set collector.all.w_pos_n1 = true;

weight w_word_p2: Word x Slot -> Double;
factor: for Int t, Word w, Slot s if word(t-2,w) & word(t,_) add [slot(t,s)]*w_word_p2(w,s);
set collector.all.w_word_p2 = true;


weight w_pos_p2: POS x Slot -> Double;
factor: for Int t, Slot s, POS p if pos(t-2,p) & word(t,_) add [slot(t,s)]*w_pos_p2(p,s);
set collector.all.w_pos_p2 = true;

weight w_word_n2: Word x Slot -> Double;
factor: for Int t, Word w, Slot s if word(t+2,w) & word(t,_) add [slot(t,s)]*w_word_n2(w,s);
set collector.all.w_word_n2 = true;


weight w_pos_n2: POS x Slot -> Double;
factor: for Int t, Slot s, POS p if pos(t+2,p) & word(t,_) add [slot(t,s)]*w_pos_n2(p,s);
set collector.all.w_pos_n2 = true;

/*
weight w_gword: Word x Goal -> Double;
factor: for Int t, Goal g, Word w if word(t,w) add [goal(1,g)]*w_gword(w,g);

weight w_gpos: POS x Goal -> Double;
factor: for Int t, Goal g, POS p if pos(t,p) add [goal(1,g)]*w_gpos(p,g);
*/

weight w_forbid1: Slot x Slot -> Double-;
factor: for Int t, Slot s1, Slot s2 if word(t,_) & s1 != s2 add[slot(t,s1)&slot(t,s2)] * w_forbid1(s1,s2);
set collector.all.w_forbid1 = true;

weight w_forbid2: Slot -> Double-;
factor:
  for Int i, Int j, Slot s1
  if word(i,_) & word(j,_) & j > i
  add[slot(i,s1)&slot(j,s1)] * w_forbid2(s1);
set collector.all.w_forbid2 = true;

weight w_forbid3: Slot x Slot -> Double-;
factor:
  for Int i, Slot s1, Slot s2
  if word(i,_) & word(i+1,_)
  add[slot(i,s1)&slot(i+1,s2)] * w_forbid3(s1,s2);

set collector.all.w_forbid3 = true;

weight w_imply1: Slot x Slot -> Double+;
factor: for Int t, Slot s1, Slot s2 if word(t,_) & s1 != s2 add[slot(t,s1) => slot(t,s2)] * w_imply1(s1,s2);

weight w_imply1_word: Slot x Slot x Word -> Double+;
factor:
  for Int t, Slot s1, Slot s2, Word w
  if word(t,w) & s1 != s2
  add[slot(t,s1) => slot(t,s2)] * w_imply1_word(s1,s2,w);

weight w_imply1_word_p1: Slot x Slot x Word -> Double+;
factor:
  for Int t, Slot s1, Slot s2, Word w
  if word(t,_) & word(t+1,w) & s1 != s2
  add[slot(t,s1) => slot(t,s2)] * w_imply1_word_p1(s1,s2,w);

weight w_imply1_word_n1: Slot x Slot x Word -> Double+;
factor:
  for Int t, Slot s1, Slot s2, Word w
  if word(t,_) & word(t-1,w) & s1 != s2
  add[slot(t,s1) => slot(t,s2)] * w_imply1_word_n1(s1,s2,w);


/*
weight w_implyFROMLOC: Slot -> Double+;
factor: for Int t, Slot s1 if word(t,_) & s1 != "FROMLOC" add[slot(t,"FROMLOC") => slot(t,s1)] * w_implyFROMLOC(s1);

weight w_implyTOLOC: Slot -> Double+;
factor: for Int t, Slot s1 if word(t,_) & s1 != "TOLOC" add[slot(t,"TOLOC") => slot(t,s1)] * w_implyTOLOC(s1);
*/

/*


weight w_slotpair2: Slot x Slot -> Double+;
factor: for Int t, Slot s1, Slot s2 if word(t,_) & s1 != s2 add[slot(t,s1) => slot(t,s2)] * w_slotpair2(s1,s2);

weight w_global1: Slot x Slot x Slot -> Double+;
factor:
  for Int i, Int j, Slot s1, Slot s2, Slot s3
  if word(i,_) & word(j,_) & j != i & s2 != s3
  add[slot(i,s1) & slot(j,s2) => slot(j,s3)] * w_global1(s1,s2,s3);
*/