// Local rules

// Factors to determin the frameLabel of an argument:
//      FrameLabel <- ['01','02','03'...]

weight w_frameLabel: FrameLabel -> Double;
factor: for Int p, FrameLabel l if word(p,_) & possiblePredicate(p) add [frameLabel(p,l)] * w_frameLabel(l);

weight w_frameLabel_slemma: Slemma x FrameLabel -> Double;
factor: for Int p, FrameLabel l, Slemma s if slemma(p,s) & possiblePredicate(p) add [frameLabel(p,l)] * w_frameLabel_slemma(s,l);


// New additions
// WORD [i]
// is a predicate given the current word
weight w_fl_word:FrameLabel x  Word  -> Double;
factor: for FrameLabel f, Int i, Word w if word(i,w) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_word(f,w);

// SLEMMA [-2.. i .. +2]
// is a predicate given the current lemma
weight w_fl_slemma:FrameLabel x Slemma -> Double;
factor: for FrameLabel f, Int i, Slemma l if slemma(i,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_slemma(f,l);

// is a predicate given the previous slemma
weight w_fl_prev_slemma:FrameLabel x Slemma -> Double;
factor: for FrameLabel f, Int i, Slemma l if slemma(i,_) & slemma(i-1,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_prev_slemma(f,l);

// is a predicate given the next slemma
weight w_fl_next_slemma:FrameLabel x Slemma -> Double;
factor: for FrameLabel f, Int i, Slemma l if slemma(i,_) & slemma(i+1,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_next_slemma(f,l);

// is a predicate given the previous slemma
weight w_fl_prev2_slemma:FrameLabel x Slemma -> Double;
factor: for FrameLabel f, Int i, Slemma l if slemma(i,_) & slemma(i-2,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_prev2_slemma(f,l);

// is a predicate given the next slemma
weight w_fl_next2_slemma:FrameLabel x Slemma -> Double;
factor: for FrameLabel f, Int i, Slemma l if slemma(i,_) & slemma(i+2,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_next2_slemma(f,l);

// SPPOS [-2.. i .. +2]
// is a predicate given the current ppos
weight w_fl_ppos:FrameLabel x Ppos -> Double;
factor: for FrameLabel f, Int i, Ppos l if ppos(i,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_ppos(f,l);

// is a predicate given the previous ppos
weight w_fl_prev_ppos:FrameLabel x Ppos -> Double;
factor: for FrameLabel f, Int i, Ppos l if ppos(i,_) & ppos(i-1,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_prev_ppos(f,l);

// is a predicate given the next ppos
weight w_fl_next_ppos:FrameLabel x Ppos -> Double;
factor: for FrameLabel f, Int i, Ppos l if ppos(i,_) & ppos(i+1,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_next_ppos(f,l);

// is a predicate given the previous ppos
weight w_fl_prev2_ppos:FrameLabel x Ppos -> Double;
factor: for FrameLabel f, Int i, Ppos l if ppos(i,_) & ppos(i-2,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_prev2_ppos(f,l);

// is a predicate given the next ppos
weight w_fl_next2_ppos:FrameLabel x Ppos -> Double;
factor: for FrameLabel f, Int i, Ppos l if ppos(i,_) & ppos(i+2,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_next2_ppos(f,l);

// CPOS
// Around a word
weight w_fl_prev_next_cpos:FrameLabel x Cpos x Cpos-> Double;
factor: for FrameLabel f, Int i, Cpos c1, Cpos c2 if word(i,_) & cpos(i-1,c1) & cpos(i+1,c2) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_prev_next_cpos(f,c1,c2);

weight w_fl_prev_next2_cpos2:FrameLabel x Cpos x Cpos x Cpos x Cpos-> Double;
factor: for FrameLabel f, Int i, Cpos c1, Cpos c2, Cpos c3, Cpos c4 if word(i,_) & cpos(i-2,c3) & cpos(i-1,c1) & cpos(i+1,c2) & cpos(i+2,c4) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_prev_next2_cpos2(f,c1,c2,c3,c4);

// LEMMA [i]
// is a predicate given the current slemma
weight w_fl_lemma:FrameLabel x Lemma -> Double;
factor: for FrameLabel f, Int i, Lemma l if lemma(i,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_lemma(f,l);

// SFORM [i]
// is a predicate given the current sform
weight w_fl_sform:FrameLabel x Sform -> Double;
factor: for FrameLabel f, Int i, Sform l if sform(i,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_sform(f,l);

// PPOS [i]
// is a predicate given the current sform
weight w_fl_ppos:FrameLabel x Ppos -> Double;
factor: for FrameLabel f, Int i, Ppos l if ppos(i,l) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_ppos(f,l);


// USING OPEN information

// MALT dependencies
weight w_fl_mdep_child:FrameLabel x MDependency -> Double;
factor: for FrameLabel f, Int i, MDependency m if word(i,_) & mst_dep(i,_,m) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_mdep_child(f,m);

weight w_fl_mdep_par:FrameLabel x MDependency -> Double;
factor: for FrameLabel f, Int i, MDependency m if word(i,_) & mst_dep(_,i,m) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_mdep_par(f,m);

weight w_fl_mdep_child_ppos:FrameLabel x Ppos -> Double;
factor: for FrameLabel f, Int i, Int j, Ppos p if word(i,_) & ppos(j,p) & mst_dep(i,j,_) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_mdep_child_ppos(f,p);

weight w_fl_mdep_child_ppos:FrameLabel x Ppos -> Double;
factor: for FrameLabel f, Int i, Int j, Ppos p if word(i,_) & ppos(j,p) & mst_dep(i,j,_) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_mdep_child_ppos(f,p);

weight w_fl_mdep_pair_child_ppos:FrameLabel x Ppos x Ppos -> Double;
factor: for FrameLabel f, Int i, Int j, Ppos p_c, Ppos p_p if ppos(i,p_p) & mst_dep(i,j,_) & ppos(j,p_c) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_mdep_pair_child_ppos(f,p_p,p_c);
set collector.all.w_fl_mdep_pair_child_ppos = true;

weight w_fl_mdep_pair_grandchild_ppos:FrameLabel x Ppos x Ppos -> Double;
factor: for FrameLabel f, Int i, Int j, Int k,  Ppos p_c, Ppos p_p if ppos(i,p_p) & mst_dep(i,j,_) & mst_dep(j,k,_) & ppos(k,p_c) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_mdep_pair_grandchild_ppos(f,p_p,p_c);
set collector.all.w_fl_mdep_pair_grandchild_ppos = true;

weight w_fl_mframe:FrameLabel x MFrame -> Double;
factor: for FrameLabel f, Int i, MFrame f2 if word(i,_) & mst_frame(i,f2) & possiblePredicate(i) add [frameLabel(i,f)] * w_fl_mframe(f,f2);







