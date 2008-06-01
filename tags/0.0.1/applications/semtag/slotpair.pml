weight w_pair_bias: Slot x Slot -> Double-;
factor: for Int t,Slot s1, Slot s2 if word(t,_) & possiblePair(s1,s2) add [slotPair(t,s1,s2)]*w_pair_bias(s1,s2);
//set collector.all.w_pair_bias = true;

weight w_pair_word: Word x Slot x Slot -> Double;
factor: for Int t, Word w, Slot s1, Slot s2 if word(t,w) & possiblePair(s1,s2) add [slotPair(t,s1,s2)]*w_pair_word(w,s1,s2);
//set collector.all.w_pair_word = true;

weight w_pair_pos: POS x Slot x Slot -> Double;
factor: for Int t, Slot s1, Slot s2, POS p if pos(t,p) & possiblePair(s1,s2) add [slotPair(t,s1,s2)]*w_pair_pos(p,s1,s2);
//set collector.all.w_pair_pos = true;

weight w_pair_word_p1: Word x Slot x Slot -> Double;
factor: for Int t, Word w, Slot s1, Slot s2 if word(t-1,w) & word(t,_) & possiblePair(s1,s2) add [slotPair(t,s1,s2)]*w_pair_word_p1(w,s1,s2);
//set collector.all.w_pair_word_p1 = true;

weight w_pair_pos_p1: POS x Slot x Slot -> Double;
factor: for Int t, Slot s1, Slot s2, POS p if pos(t-1,p) & word(t,_) & possiblePair(s1,s2) add [slotPair(t,s1,s2)]*w_pair_pos_p1(p,s1,s2);
//set collector.all.w_pair_pos_p1 = true;

weight w_pair_word_n1: Word x Slot x Slot -> Double;
factor: for Int t, Word w, Slot s1, Slot s2 if word(t+1,w)& word(t,_) & possiblePair(s1,s2) add [slotPair(t,s1,s2)]*w_pair_word_n1(w,s1,s2);
//set collector.all.w_pair_word_n1 = true;

weight w_pair_pos_n1: POS x Slot x Slot -> Double;
factor: for Int t, Slot s1, Slot s2, POS p if pos(t+1,p) & word(t,_) & possiblePair(s1,s2) add [slotPair(t,s1,s2)]*w_pair_pos_n1(p,s1,s2);
set collector.all.w_pair_pos_n1 = true;

weight w_pair_word_p2: Word x Slot x Slot -> Double;
factor: for Int t, Word w, Slot s1, Slot s2 if word(t-2,w) & word(t,_) & possiblePair(s1,s2) add [slotPair(t,s1,s2)]*w_pair_word_p2(w,s1,s2);
//set collector.all.w_pair_word_p2 = true;

weight w_pair_pos_p2: POS x Slot x Slot -> Double;
factor: for Int t, Slot s1, Slot s2, POS p if pos(t-2,p) & word(t,_) & possiblePair(s1,s2) add [slotPair(t,s1,s2)]*w_pair_pos_p2(p,s1,s2);
//set collector.all.w_pair_pos_p2 = true;

weight w_pair_word_n2: Word x Slot x Slot -> Double;
factor: for Int t, Word w, Slot s1, Slot s2 if word(t+2,w) & word(t,_) & possiblePair(s1,s2) add [slotPair(t,s1,s2)]*w_pair_word_n2(w,s1,s2);
//set collector.all.w_pair_word_n2 = true;

weight w_pair_pos_n2: POS x Slot x Slot -> Double;
factor: for Int t, Slot s1, Slot s2, POS p if pos(t+2,p) & word(t,_) & possiblePair(s1,s2) add [slotPair(t,s1,s2)]*w_pair_pos_n2(p,s1,s2);
//set collector.all.w_pair_pos_n2 = true;