// Local rules

// Factors to determine if a current token is a predicate (predicate:
// isPredicate)

// WORD [i]
// is a predicate given the current word
weight w_ip_word: Word -> Double;
factor: for Int i, Word w if word(i,w) & possiblePredicate(i) add [isPredicate(i)] * w_ip_word(w);

// SLEMMA [-2.. i .. +2]
// is a predicate given the current lemma
weight w_ip_slemma: Slemma -> Double;
factor: for Int i, Slemma l if slemma(i,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_slemma(l);

// is a predicate given the previous slemma
weight w_ip_prev_slemma: Slemma -> Double;
factor: for Int i, Slemma l if slemma(i,_) & slemma(i-1,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_prev_slemma(l);

// is a predicate given the next slemma
weight w_ip_next_slemma: Slemma -> Double;
factor: for Int i, Slemma l if slemma(i,_) & slemma(i+1,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_next_slemma(l);

// is a predicate given the previous slemma
weight w_ip_prev2_slemma: Slemma -> Double;
factor: for Int i, Slemma l if slemma(i,_) & slemma(i-2,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_prev2_slemma(l);

// is a predicate given the next slemma
weight w_ip_next2_slemma: Slemma -> Double;
factor: for Int i, Slemma l if slemma(i,_) & slemma(i+2,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_next2_slemma(l);

// SPPOS [-2.. i .. +2]
// is a predicate given the current ppos
weight w_ip_sppos: Sppos -> Double;
factor: for Int i, Sppos l if sppos(i,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_sppos(l);

// is a predicate given the previous sppos
weight w_ip_prev_sppos: Sppos -> Double;
factor: for Int i, Sppos l if sppos(i,_) & sppos(i-1,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_prev_sppos(l);

// is a predicate given the next sppos
weight w_ip_next_sppos: Sppos -> Double;
factor: for Int i, Sppos l if sppos(i,_) & sppos(i+1,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_next_sppos(l);

// is a predicate given the previous sppos
weight w_ip_prev2_sppos: Sppos -> Double;
factor: for Int i, Sppos l if sppos(i,_) & sppos(i-2,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_prev2_sppos(l);

// is a predicate given the next sppos
weight w_ip_next2_sppos: Sppos -> Double;
factor: for Int i, Sppos l if sppos(i,_) & sppos(i+2,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_next2_sppos(l);

// CPOS
// Around a word
weight w_ip_prev_next_cpos: Cpos x Cpos-> Double;
factor: for Int i, Cpos c1, Cpos c2 if word(i,_) & cpos(i-1,c1) & cpos(i+1,c2) & possiblePredicate(i) add [isPredicate(i)] * w_ip_prev_next_cpos(c1,c2);

weight w_ip_prev_next2_cpos2: Cpos x Cpos x Cpos x Cpos-> Double;
factor: for Int i, Cpos c1, Cpos c2, Cpos c3, Cpos c4 if word(i,_) & cpos(i-2,c3) & cpos(i-1,c1) & cpos(i+1,c2) & cpos(i+2,c4) & possiblePredicate(i) add [isPredicate(i)] * w_ip_prev_next2_cpos2(c1,c2,c3,c4);

// LEMMA [i]
// is a predicate given the current slemma
weight w_ip_lemma: Lemma -> Double;
factor: for Int i, Lemma l if lemma(i,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_lemma(l);

// SFORM [i]
// is a predicate given the current sform
weight w_ip_sform: Sform -> Double;
factor: for Int i, Sform l if sform(i,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_sform(l);

// PPOS [i]
// is a predicate given the current sform
weight w_ip_ppos: Ppos -> Double;
factor: for Int i, Ppos l if ppos(i,l) & possiblePredicate(i) add [isPredicate(i)] * w_ip_ppos(l);


// USING OPEN information

// MALT dependencies
weight w_ip_mdep_child: MDependency -> Double;
factor: for Int i, MDependency m if word(i,_) & mst_dep(i,_,m) & possiblePredicate(i) add [isPredicate(i)] * w_ip_mdep_child(m);

weight w_ip_mdep_par: MDependency -> Double;
factor: for Int i, MDependency m if word(i,_) & mst_dep(_,i,m) & possiblePredicate(i) add [isPredicate(i)] * w_ip_mdep_par(m);

weight w_ip_mdep_child_ppos: Ppos -> Double;
factor: for Int i, Int j, Ppos p if word(i,_) & ppos(j,p) & mst_dep(i,j,_) & possiblePredicate(i) add [isPredicate(i)] * w_ip_mdep_child_ppos(p);

weight w_ip_mdep_child_sppos: Sppos -> Double;
factor: for Int i, Int j, Sppos p if word(i,_) & sppos(j,p) & mst_dep(i,j,_) & possiblePredicate(i) add [isPredicate(i)] * w_ip_mdep_child_sppos(p);

weight w_ip_mdep_pair_child_ppos: Ppos x Ppos -> Double;
factor: for Int i, Int j, Ppos p_c, Ppos p_p if ppos(i,p_p) & mst_dep(i,j,_) & ppos(j,p_c) & possiblePredicate(i) add [isPredicate(i)] * w_ip_mdep_pair_child_ppos(p_p,p_c);
set collector.all.w_ip_mdep_pair_child_ppos = true;

weight w_ip_mdep_pair_grandchild_ppos: Ppos x Ppos -> Double;
factor: for Int i, Int j, Int k,  Ppos p_c, Ppos p_p if ppos(i,p_p) & mst_dep(i,j,_) & mst_dep(j,k,_) & ppos(k,p_c) & possiblePredicate(i) add [isPredicate(i)] * w_ip_mdep_pair_grandchild_ppos(p_p,p_c);
set collector.all.w_ip_mdep_pair_grandchild_ppos = true;

weight w_ip_mframe: MFrame -> Double;
factor: for Int i, MFrame f if word(i,_) & mst_frame(i,f) & possiblePredicate(i) add [isPredicate(i)] * w_ip_mframe(f);

