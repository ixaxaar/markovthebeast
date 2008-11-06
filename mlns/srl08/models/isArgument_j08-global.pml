
weight w_iag_predLemmaSense: Lemma x Lemma x FrameLabel -> Double-;
factor[3]: for Int a, Int p, Lemma l, FrameLabel s, Lemma la if
  lemma(p,l) & lemma(a,la) &  possiblePredicate(p) & possibleArgument(a) add [ isPredicate(p) & frameLabel(p,s) & isArgument(a)] * w_iag_predLemmaSense(l,la,s);
//set collector.all.w_iag_predLemmaSense=true;

weight w_iag_predLemma: Lemma x Lemma -> Double-;
factor[2]: for Int a, Int p, Lemma l, Lemma la if
  lemma(p,l) & lemma(a,la) & possiblePredicate(p) & possibleArgument(a) add [ isPredicate(p) & isArgument(a)] * w_iag_predLemma(l,la);
//set collector.all.w_iag_predLemma=true;

//weight w_iag_predSense: Lemma x FrameLabel -> Double-;
//factor[3]: for Int a, Int p, FrameLabel l, Lemma la if
//  possiblePredicate(p) & possibleArgument(a) & lemma(a,la) add [ frameLabel(p,l) & isArgument(a)] * w_iag_predSense(la,l);
//set collector.all.w_iag_predSense=true;

