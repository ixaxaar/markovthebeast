// Global rules involving role/predicate/framePredicate

weight w_rg_predLemmaSense: Role x Lemma x Lemma x FrameLabel -> Double-;
factor [4]: for Int a, Int p, Role r, Lemma la, Lemma l, FrameLabel s if
  lemma(p,l) & possiblePredicate(p) & possibleArgument(a) & lemma(a,la) add[frameLabel(p,s) & role(p,a,r)] * w_rg_predLemmaSense(r,l,la,s);

//lemma(p,l) & possiblePredicate(p) & possibleArgument(a) & lemma(a,la) add[isPredicate(p)  &  frameLabel(p,s) & role(p,a,r)] * w_rg_predLemmaSense(r,l,la,s);

//set collector.all.w_rg_predLemmaSense=true;

weight w_rg_predLemma: Role x Lemma x Lemma -> Double-;
//factor [5]: for Int a, Int p, Role r, Lemma l, Lemma la if
factor [5]: for Int a, Int p, Role r, Lemma l, Lemma la if
lemma(p,l) & possiblePredicate(p) & possibleArgument(a) & lemma(a,la) add[role(p,a,r)] * w_rg_predLemma(r,l,la);
//  lemma(p,l) & possiblePredicate(p) & possibleArgument(a) & lemma(a,la) add[isPredicate(p) & role(p,a,r)] * w_rg_predLemma(r,l,la);
//set collector.all.w_rg_predLemma=true;

//weight w_rg_predSense: Role x FrameLabel -> Double-;
//factor [3]: for Int a, Int p, FrameLabel l, Role r if
//  possiblePredicate(p) & possibleArgument(a) add[frameLabel(p,l) & role(p,a,r)] * w_rg_predSense(r,l);
//set collector.all.w_rg_predSense=true;



