//bias
//weight l_hasLabel_bias: Double;
//factor: for Int p, Int a if lemma(p,_) & lemma(a,_) add [hasLabel(p,a)] * l_hasLabel_bias;

//distance
weight l_hasLabel_dist: Int -> Double-;
factor: for Int p, Int a if lemma(p,_) & lemma(a,_) & possiblePredicate(p) & possibleArgument(a) add [hasLabel(p,a)] * l_hasLabel_dist(bins(0,1,2,3,4,5,10,a-p));

weight w_hasLabel_impossiblePredicate: Double;
factor: for Int p, Int a if lemma(p,_) & lemma(a,_) & !possiblePredicate(p)
  add [hasLabel(p,a)] * w_hasLabel_impossiblePredicate;



//distance & voice
weight l_hasLabel_dist_voice: Int x Voice -> Double-;
factor: for Int p, Int a, Voice v if lemma(p,_) & lemma(a,_) & voice(p,v) 
  add [hasLabel(p,a)] * l_hasLabel_dist_voice(bins(0,1,2,3,4,5,10,a-p),v);

//predicate lemma and argument lemma
weight l_hasLabel_lp_la: Lemma x Lemma -> Double;
factor: for Int p, Int a, Lemma l_p, Lemma l_a if lemma(p,l_p) & lemma(a,l_a) & possiblePredicate(p) & possibleArgument(a) 
  add [hasLabel(p,a)] * l_hasLabel_lp_la(l_p,l_a);

//predicate wnet and argument wnet
//weight l_hasLabel_wnp_wna: WNet x WNet -> Double;
//factor: for Int p, Int a, WNet l_p, WNet l_a if wnet(p,l_p) & wnet(a,l_a)
//  add [hasLabel(p,a)] * l_hasLabel_wnp_wna(l_p,l_a);

//predicate wnet and argument wnet + distance
//weight l_hasLabel_wnp_wna_dist: WNet x WNet x Int -> Double;
//factor: for Int p, Int a, WNet l_p, WNet l_a if wnet(p,l_p) & wnet(a,l_a)
//  add [hasLabel(p,a)] * l_hasLabel_wnp_wna_dist(l_p,l_a,bins(0,1,2,3,4,5,10,a-p));

//predicate pos and argument pos
weight l_hasLabel_pp_pa: Ppos x Ppos -> Double;
factor: for Int p, Int a, Ppos p_p, Ppos p_a if ppos(p,p_p) & ppos(a,p_a) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_pp_pa(p_p,p_a);

//predicate pos and argument pos + distance
weight l_hasLabel_pp_pa_dist: Ppos x Ppos x Int-> Double;
factor: for Int p, Int a, Ppos p_p, Ppos p_a if ppos(p,p_p) & ppos(a,p_a) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_pp_pa_dist(p_p,p_a,bins(0,1,2,3,4,5,10,a-p));

//predicate pos and pos at arg + 1
weight l_hasLabel_pp_paN1: Ppos x Ppos -> Double;
factor: for Int p, Int a, Ppos p_p, Ppos p_a if ppos(p,p_p) & ppos(a+1,p_a) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_pp_paN1(p_p,p_a);

//pred pos and pos at arg - 1
weight l_hasLabel_pp_paP1: Ppos x Ppos -> Double;
factor: for Int p, Int a, Ppos p_p, Ppos p_a if ppos(p,p_p) & ppos(a-1,p_a) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_pp_paP1(p_p,p_a);

//predicate lemma and arg pos
weight l_hasLabel_lp_pa: Lemma x Ppos -> Double;
factor: for Int p, Int a, Lemma l_p, Ppos p_a if lemma(p,l_p) & ppos(a,p_a) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_lp_pa(l_p,p_a);

//predicate wnet and arg pos
//weight l_hasLabel_wnp_pa: WNet x Ppos -> Double;
//factor: for Int p, Int a, WNet l_p, Ppos p_a if wnet(p,l_p) & ppos(a,p_a)
//  add [hasLabel(p,a)] * l_hasLabel_wnp_pa(l_p,p_a);

//predicate wnet and arg pos + distance
//weight l_hasLabel_wnp_pa_dist: WNet x Ppos x Int -> Double;
//factor: for Int p, Int a, WNet l_p, Ppos p_a if wnet(p,l_p) & ppos(a,p_a)
//  add [hasLabel(p,a)] * l_hasLabel_wnp_pa_dist(l_p,p_a,bins(0,1,2,3,4,5,10,a-p));

//predicate pos and argument lemma
weight l_hasLabel_pp_la: Ppos x Lemma -> Double;
factor: for Int p, Int a, Ppos p_p, Lemma l_a if ppos(p,p_p) & lemma(a,l_a) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_pp_la(p_p,l_a);


//same token features: lemma
weight w_same_lemma: Lemma -> Double;
factor: for Int p, Lemma l if lemma(p,l) & possiblePredicate(p) & possibleArgument(p) add [hasLabel(p,p)] * w_same_lemma(l);

//same token features: ppos
weight w_same_ppos: Ppos -> Double;
factor: for Int p, Ppos l if ppos(p,l) & possiblePredicate(p) & possibleArgument(p) add [hasLabel(p,p)] * w_same_ppos(l);


//CPOS window feature
weight w_cpos1: Cpos x Cpos x Cpos x Cpos x Int -> Double;
factor: for Int p, Int a, Cpos cp1, Cpos cp2, Cpos cp3, Cpos cp4
  if cpos(p,cp1) & cpos(p-1,cp2) & cpos(a,cp3) & cpos(a+1,cp4) & possiblePredicate(p) & possibleArgument(a) 
  add [hasLabel(p,a)] * w_cpos1(cp1,cp2,cp3,cp4,bins(0,1,2,3,4,5,10,a-p));

weight w_cpos2: Cpos x Cpos x Cpos x Cpos x Int -> Double;
factor: for Int p, Int a, Cpos cp1, Cpos cp2, Cpos cp3, Cpos cp4
  if cpos(p,cp1) & cpos(p+1,cp2) & cpos(a,cp3) & cpos(a-1,cp4) & possiblePredicate(p) & possibleArgument(a) 
  add [hasLabel(p,a)] * w_cpos2(cp1,cp2,cp3,cp4,bins(0,1,2,3,4,5,10,a-p));

weight w_cpos3: Cpos x Cpos x Cpos x Cpos x Int -> Double;
factor: for Int p, Int a, Cpos cp1, Cpos cp2, Cpos cp3, Cpos cp4
  if cpos(p,cp1) & cpos(p-1,cp2) & cpos(a,cp3) & cpos(a-1,cp4) & possiblePredicate(p) & possibleArgument(a) 
  add [hasLabel(p,a)] * w_cpos3(cp1,cp2,cp3,cp4,bins(0,1,2,3,4,5,10,a-p));

weight w_cpos4: Cpos x Cpos x Cpos x Cpos x Int -> Double;
factor: for Int p, Int a, Cpos cp1, Cpos cp2, Cpos cp3, Cpos cp4
  if cpos(p,cp1) & cpos(p+1,cp2) & cpos(a,cp3) & cpos(a+1,cp4) & possiblePredicate(p) & possibleArgument(a) 
  add [hasLabel(p,a)] * w_cpos4(cp1,cp2,cp3,cp4,bins(0,1,2,3,4,5,10,a-p));

/*
weight w_cpos5: Cpos x Cpos x Cpos x Int -> Double;
factor: for Int p, Int a, Cpos cp1, Cpos cp2, Cpos cp3
  if cpos(p,cp1) & cpos(p-1,cp2) & cpos(a,cp3)
  add [hasLabel(p,a)] * w_cpos5(cp1,cp2,cp3,bins(0,1,2,3,4,5,10,a-p));

weight w_cpos6: Cpos x Cpos x Cpos x Int -> Double;
factor: for Int p, Int a, Cpos cp1, Cpos cp2, Cpos cp3
  if cpos(p,cp1) & cpos(p+1,cp2) & cpos(a,cp3)
  add [hasLabel(p,a)] * w_cpos6(cp1,cp2,cp3,bins(0,1,2,3,4,5,10,a-p));

weight w_cpos7: Cpos x Cpos x Cpos x Int -> Double;
factor: for Int p, Int a, Cpos cp1, Cpos cp2, Cpos cp3
  if cpos(p,cp1) & cpos(a+1,cp2) & cpos(a,cp3)
  add [hasLabel(p,a)] * w_cpos7(cp1,cp2,cp3,bins(0,1,2,3,4,5,10,a-p));

weight w_cpos8: Cpos x Cpos x Cpos x Int -> Double;
factor: for Int p, Int a, Cpos cp1, Cpos cp2, Cpos cp3
  if cpos(p,cp1) & cpos(a-1,cp2) & cpos(a,cp3)
  add [hasLabel(p,a)] * w_cpos8(cp1,cp2,cp3,bins(0,1,2,3,4,5,10,a-p));
*/
