// Local rules

// Factors to determin the frameLabel of an argument:
//      FrameLabel <- ['01','02','03'...]

weight w_frameLabel: FrameLabel -> Double;
factor: for Int p, FrameLabel l if word(p,_) & possiblePredicate(p) add [frameLabel(p,l)] * w_frameLabel(l);

weight w_frameLabel_slemma: Slemma x FrameLabel -> Double;
factor: for Int p, FrameLabel l, Slemma s if slemma(p,s) & possiblePredicate(p) add [frameLabel(p,l)] * w_frameLabel_slemma(s,l);







// WORD [i]
// is a predicate given the current word
//weight w_fl_word: Word  x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Word w if word(i,w) add [frameLabel(i,f)] * w_fl_word(w,f);

// SLEMMA [-2.. i .. +2]
// is a predicate given the current lemma
//weight w_fl_slemma: Slemma x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Slemma l if slemma(i,l) add [frameLabel(i,f)] * w_fl_slemma(l,f);

// is a predicate given the previous slemma
//weight w_fl_prev_slemma: Slemma  x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Slemma l if slemma(i,_) & slemma(i-1,l) add [frameLabel(i,f)] * w_fl_prev_slemma(l,f);

// is a predicate given the next slemma
//weight w_fl_next_slemma: Slemma x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Slemma l if slemma(i,_) & slemma(i+1,l) add [frameLabel(i,f)] * w_fl_next_slemma(l,f);

// is a predicate given the previous slemma
//weight w_fl_prev2_slemma: Slemma x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Slemma l if slemma(i,_) & slemma(i-2,l) add [frameLabel(i,f)] * w_fl_prev2_slemma(l,f);

// is a predicate given the next slemma
//weight w_fl_next2_slemma: Slemma x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Slemma l if slemma(i,_) & slemma(i+2,l) add [frameLabel(i,f)] * w_fl_next2_slemma(l,f);

// SPPOS [-2.. i .. +2]
// is a predicate given the current ppos
//weight w_fl_sppos: Sppos x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Sppos l if sppos(i,l) add [frameLabel(i,f)] * w_fl_sppos(l,f);

// is a predicate given the previous sppos
//weight w_fl_prev_sppos: Sppos x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Sppos l if sppos(i,_) & sppos(i-1,l) add [frameLabel(i,f)] * w_fl_prev_sppos(l,f);

// is a predicate given the next sppos
//weight w_fl_next_sppos: Sppos x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Sppos l if sppos(i,_) & sppos(i+1,l) add [frameLabel(i,f)] * w_fl_next_sppos(l,f);

// is a predicate given the previous sppos
//weight w_fl_prev2_sppos: Sppos x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Sppos l if sppos(i,_) & sppos(i-2,l) add [frameLabel(i,f)] * w_fl_prev2_sppos(l,f);

// is a predicate given the next sppos
//weight w_fl_next2_sppos: Sppos x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Sppos l if sppos(i,_) & sppos(i+2,l) add [frameLabel(i,f)] * w_fl_next2_sppos(l,f);

// LEMMA
// is a predicate given the current slemma
//weight w_fl_lemma: Lemma x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Lemma l if lemma(i,l) add [frameLabel(i,f)] * w_fl_lemma(l,f);

// SFORM
// is a predicate given the current sform
//weight w_fl_sform: Sform x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Sform l if sform(i,l) add [frameLabel(i,f)] * w_fl_sform(l,f);

// PPOS
// is a predicate given the current sform
//weight w_fl_ppos: Ppos x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, Ppos l if ppos(i,l) add [frameLabel(i,f)] * w_fl_ppos(l,f);

// USING OPEN information

// MALT dependencies
//weight w_fl_mdep: MDependency x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, MDependency m if word(i,_) & mst_dep(_,i,m) add [frameLabel(i,f)] * w_fl_mdep(m,f);

//weight w_fl_mframe: MFrame x FrameLabel -> Double;
//factor: for Int i, FrameLabel fl, MFrame f if word(i,_) & mst_frame(i,f) add [frameLabel(i,fl)] * w_fl_mframe(f,fl);

// MST dependencies
//weight w_fl_mdep: MDependency x FrameLabel -> Double;
//factor: for Int i, FrameLabel f, MDependency m if word(i,_) & mst_dep(_,i,m) add [frameLabel(i,f)] * w_fl_mdep(m,f);

//weight w_fl_mframe: MFrame x FrameLabel -> Double;
//factor: for Int i, FrameLabel fl, MFrame f if word(i,_) & mst_frame(i,f) add [frameLabel(i,fl)] * w_fl_mframe(f,fl);



