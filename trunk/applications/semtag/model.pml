include "corpora/atis_2_3.types.pml";
hidden: slot, goal;
observed: word, pos;


weight w_word: Word x Slot -> Double;
factor: for Int t, Word w, Slot s if word(t,w) add [slot(t,s)]*w_word(w,s);

weight w_pos: POS x Slot -> Double;
factor: for Int t, Slot s, POS p if pos(t,p) add [slot(t,s)]*w_pos(p,s);

weight w_word_p1: Word x Slot -> Double;
factor: for Int t, Word w, Slot s if word(t-1,w) & word(t,_) add [slot(t,s)]*w_word_p1(w,s);

weight w_pos_p1: POS x Slot -> Double;
factor: for Int t, Slot s, POS p if pos(t-1,p) & word(t,_) add [slot(t,s)]*w_pos_p1(p,s);

weight w_word_n1: Word x Slot -> Double;
factor: for Int t, Word w, Slot s if word(t+1,w)& word(t,_)  add [slot(t,s)]*w_word_n1(w,s);

weight w_pos_n1: POS x Slot  -> Double;
factor: for Int t, Slot s, POS p if pos(t+1,p) & word(t,_) add [slot(t,s)]*w_pos_n1(p,s);

weight w_word_p2: Word x Slot -> Double;
factor: for Int t, Word w, Slot s if word(t-2,w) & word(t,_) add [slot(t,s)]*w_word_p2(w,s);

weight w_pos_p2: POS x Slot -> Double;
factor: for Int t, Slot s, POS p if pos(t-2,p) & word(t,_) add [slot(t,s)]*w_pos_p2(p,s);

weight w_word_n2: Word x Slot -> Double;
factor: for Int t, Word w, Slot s if word(t+2,w) & word(t,_) add [slot(t,s)]*w_word_n2(w,s);

weight w_pos_n2: POS x Slot  -> Double;
factor: for Int t, Slot s, POS p if pos(t+2,p) & word(t,_) add [slot(t,s)]*w_pos_n2(p,s);

weight w_gword: Word x Goal  -> Double;
factor: for Int t, Goal g, Word w if word(t,w) add [goal(1,g)]*w_gword(w,g);

weight w_gpos: POS x Goal  -> Double;
factor: for Int t, Goal g, POS p if pos(t,p) add [goal(1,g)]*w_gpos(p,g);

weight w_slotpair: Slot x Slot -> Double-;
factor: for Int t, Slot s1, Slot s2 if word(t,_) & s1 != s2 add[slot(t,s1)&slot(t,s2)] * w_slotpair(s1,s2);
set collector.all.w_slotpair = true;

