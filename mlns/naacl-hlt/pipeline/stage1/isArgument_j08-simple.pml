
// Johansson 2008 (Shared task)


weight w_ia_predParentWord: Word -> Double;
factor: for Int a, Int p, Int j, Word l if
  possiblePredicate(p) & possibleArgument(a) & mst_link(j,p) & word(j,l) add[isArgument(a)] * w_ia_predParentWord(l);

//weight w_ia_predParentLemma: Lemma -> Double;
//factor: for Int a, Int p, Int j, Lemma l if
//  possiblePredicate(p) & possibleArgument(a) & mst_link(j,p) & lemma(j,l) add[isArgument(a)] * w_ia_predParentLemma(l);

weight w_ia_predParentPos: Ppos -> Double;
factor: for Int a, Int p, Int j, Ppos l if
  possiblePredicate(p) & possibleArgument(a) & mst_link(j,p) & ppos(j,l) add[isArgument(a)] * w_ia_predParentPos(l);

weight w_ia_childDepSet: DepSet -> Double;
factor: for Int a, Int p, DepSet l if
  possiblePredicate(p) & possibleArgument(a) & childDepSet(p,l) add[isArgument(a)] * w_ia_childDepSet(l);

weight w_ia_predLemma2: Lemma -> Double;
factor: for Int a, Int p, Lemma l if
  lemma(p,l) & possiblePredicate(p) & possibleArgument(a) add[isArgument(a)] * w_ia_predLemma2(l);

weight w_ia_voice: Voice -> Double;
factor: for Int a, Int p, Voice v if
  possiblePredicate(p) & possibleArgument(a) & voice(p,v) add[isArgument(a)] * w_ia_voice(v);

weight w_ia_pos_before: Lemma -> Double;
factor: for Int a, Int p, Lemma l if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) & a<p add[isArgument(a)] * w_ia_pos_before(l);

weight w_ia_pos_after: Lemma -> Double;
factor: for Int a, Int p, Lemma l if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) & a>p add[isArgument(a)] * w_ia_pos_after(l);

weight w_ia_pos_equal: Lemma -> Double;
factor: for Int a, Int p, Lemma l if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) & a==p add[isArgument(a)] * w_ia_pos_equal(l);

weight w_ia_argWord: Word -> Double;
factor: for Int a, Int p, Word l if
  possiblePredicate(p) & possibleArgument(a) & word(a,l) add[isArgument(a)] * w_ia_argWord(l);

//weight w_ia_argLemma: Lemma -> Double;
//factor: for Int a, Int p, Lemma l if
//  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) add[isArgument(a)] * w_ia_argLemma(l);

weight w_ia_argPpos: Ppos -> Double;
factor: for Int a, Int p, Ppos l if
  possiblePredicate(p) & possibleArgument(a) & ppos(a,l) add[isArgument(a)] * w_ia_argPpos(l);

weight w_ia_rigthWord:  Word -> Double;
factor: for Int a, Int p, Word l, Int t if
  possiblePredicate(p) & possibleArgument(a) & rightToken(a,t) & word(t,l)  add[isArgument(a)] * w_ia_rigthWord(l);

//weight w_ia_rigthLemma:  Lemma -> Double;
//factor: for Int a, Int p, Lemma l, Int t if
//  possiblePredicate(p) & possibleArgument(a) & rightToken(a,t) & lemma(t,l)  add[isArgument(a)] * w_ia_rigthLemma(l);

weight w_ia_rigthPos: Ppos -> Double;
factor: for Int a, Int p, Ppos l, Int t if
  possiblePredicate(p) & possibleArgument(a) & rightToken(a,t) & ppos(t,l)  add[isArgument(a)] * w_ia_rigthPos(l);

weight w_ia_predPos: Ppos -> Double;
factor: for Int a, Int p, Ppos l if
  possiblePredicate(p) & possibleArgument(a) & ppos(p,l) add[isArgument(a)] * w_ia_predPos(l);

weight w_ia_relPath: RelPath -> Double;
factor: for Int a, Int p, RelPath l if
  possiblePredicate(p) & possibleArgument(a) & relPath(p,a,l) add[isArgument(a)] * w_ia_relPath(l);

weight w_ia_verbChainHasSubj: Lemma -> Double;
factor: for Int a, Int p, Lemma l  if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) & verbChainHasSubj(p) add[isArgument(a)] * w_ia_verbChainHasSubj(l);

weight w_ia_controllerHasObj: Lemma -> Double;
factor: for Int a, Int p, Lemma l if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) & controllerHasObj(p) add[isArgument(a)] * w_ia_controllerHasObj(l);

weight w_ia_predRelToParent: MDependency  -> Double;
factor: for Int a, Int p, MDependency l if
  possiblePredicate(p) & possibleArgument(a) & mst_dep(_,p,l) add[isArgument(a)] * w_ia_predRelToParent(l);

