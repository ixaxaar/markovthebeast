/*
 * Relation between isPredicate and is isArgument
/*

//weight w_is_ia_slemma: Slemma  x Slemma -> Double-;
//factor: for Int i, Int j, Slemma l1, Slemma l2 if slemma(i,l1) & slemma(j,l2) & i!=j add [isPredicate(i) & isArgument(j)] * w_is_ia_lemma(l1,l2);
//set collector.all.w_is_ia_slemma = true;

//weight w_is_ia_slemma: Slemma -> Double+;
//factor: for Int i, Int j, Slemma l1 if slemma(i,l1) & add [isPredicate(i) => isArgument(i)] * w_is_ia_lemma(l1);
//set collector.all.w_is_ia_slemma = true;


//weight w_is_ia_lemma: slemma  x slemma -> double-;
//factor: for int i, int j, lemma l1, lemma l2 if lemma(i,w) & lemma(i,w) & i!=j add [ispredicate(i) & isargument(j)] * w_is_ia_lemma(l1,l2);
//set collector.all.w_is_ia_lemma = true;

