// Global rules involving role/predicate/framePredicate

weight w_hg_predLemmaSense: Lemma x FrameLabel -> Double;
factor [3]: for Int a, Int p, Role r, Lemma l, FrameLabel s if
  lemma(p,l) & possiblePredicate(p) & possibleArgument(a) &  frameLabel(p,s) add[hasLabel(p,a)] * w_hg_predLemmaSense(l,s);
  //lemma(p,l) & possiblePredicate(p) & possibleArgument(a) &  frameLabel(p,s) add[isPredicate(p) & hasLabel(p,a)] * w_hg_predLemmaSense(l,s);

weight w_hg_predLemma: Lemma -> Double;
factor [4]: for Int a, Int p, Role r, Lemma l if
  lemma(p,l) & possiblePredicate(p) & possibleArgument(a) add[hasLabel(p,a)] * w_hg_predLemma(l);
  //lemma(p,l) & possiblePredicate(p) & possibleArgument(a) add[isPredicate(p) & hasLabel(p,a)] * w_hg_predLemma(l);


