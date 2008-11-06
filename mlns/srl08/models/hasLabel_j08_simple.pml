
// Johansson 2008 (Shared task)
// The following two would go to Global once (frameLabel and isPredicate argument are
// observable)

weight w_h_childDepSet: Role x DepSet -> Double;
factor: for Int a, Int p, DepSet l if
  possiblePredicate(p) & possibleArgument(a) & childDepSet(p,l) add[hasLabel(p,a)] * w_h_childDepSet(l);

weight w_h_predParentWord: Lemma -> Double;
factor: for Int a, Int p, Int j, Lemma l if
  possiblePredicate(p) & possibleArgument(a) & mst_link(j,p) & lemma(j,l) add[hasLabel(p,a)] * w_h_predParentWord(l);

weight w_h_predParentPos: Ppos -> Double;
factor: for Int a, Int p, Int j, Ppos l if
  possiblePredicate(p) & possibleArgument(a) & mst_link(j,p) & ppos(j,l) add[hasLabel(p,a)] * w_h_predParentPos(l);

weight w_h_childDepSet: MDependency -> Double;
factor: for Int a, Int p, MDependency l if
  possiblePredicate(p) & possibleArgument(a) & mst_dep(p,_,l) add[hasLabel(p,a)] * w_h_childDepSet(l);

weight w_h_predLemmaSense: Lemma x FrameLabel -> Double;
factor: for Int a, Int p, Lemma l, FrameLabel s if
  lemma(p,l) & possiblePredicate(p) & possibleArgument(a) & isPredicate(p) & frameLabel(p,s) add[hasLabel(p,a)] * w_h_predLemmaSense(l,s);

weight w_h_predLemma: Lemma -> Double;
factor: for Int a, Int p, Lemma l if
  lemma(p,l) & possiblePredicate(p) & possibleArgument(a) & isPredicate(p) add[hasLabel(p,a)] * w_h_predLemma(l);

weight w_h_predSense: FrameLabel -> Double;
factor: for Int a, Int p, FrameLabel l if
  possiblePredicate(p) & possibleArgument(a) & frameLabel(p,l) add[hasLabel(p,a)] * w_h_predSense(l);

weight w_h_voice: Voice -> Double;
factor: for Int a, Int p, Voice v if
  possiblePredicate(p) & possibleArgument(a) & voice(p,v) add[hasLabel(p,a,r)] * w_h_voice(v);

weight w_h_pos_before: Lemma -> Double;
factor: for Int a, Int p, Lemma l if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) & a<p add[hasLabel(p,a)] * w_h_pos_before(l);

weight w_h_pos_after: Lemma -> Double;
factor: for Int a, Int p, Lemma l if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) & a>p add[hasLabel(p,a)] * w_h_pos_after(l);

weight w_h_pos_equal: Lemma -> Double;
factor: for Int a, Int p, Lemma l if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) & a==p add[hasLabel(p,a)] * w_h_pos_equal(l);

weight w_h_argWord: Lemma -> Double;
factor: for Int a, Int p, Lemma l if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) add[hasLabel(p,a)] * w_h_argWord(l);

weight w_h_argPpos: Ppos -> Double;
factor: for Int a, Int p, Ppos l if
  possiblePredicate(p) & possibleArgument(a) & ppos(a,l) add[hasLabel(p,a)] * w_h_argPpos(l);

weight w_h_rigthWord:  Lemma -> Double;
factor: for Int a, Int p, Lemma l, Int t if
  possiblePredicate(p) & possibleArgument(a) & rightToken(a,t) & lemma(t,l)  add[hasLabel(p,a)] * w_h_rigthWord(l);

weight w_h_rigthPos: Ppos -> Double;
factor: for Int a, Int p, Ppos l, Int t if
  possiblePredicate(p) & possibleArgument(a) & rightToken(a,t) & ppos(t,l)  add[hasLabel(p,a)] * w_h_rigthPos(l);

weight w_h_predPos: Ppos -> Double;
factor: for Int a, Int p, Ppos l if
  possiblePredicate(p) & possibleArgument(a) & ppos(p,l) add[hasLabel(p,a)] * w_h_predPos(l);

weight w_h_relPath: RelPath -> Double;
factor: for Int a, Int p, RelPath l if
  possiblePredicate(p) & possibleArgument(a) & relPath(p,a,l) add[hasLabel(p,a)] * w_h_relPath(l);

weight w_h_verbChainHasSubj: Double;
factor: for Int a, Int p, RelPath l  if
  possiblePredicate(p) & possibleArgument(a) & verbChainHasSubj(p,a) add[hasLabel(p,a)] * w_h_verbChainHasSubj;

weight w_h_controllerHasObj: Double;
factor: for Int a, Int p, RelPath l  if
  possiblePredicate(p) & possibleArgument(a) & controllerHasObj(p,a) add[hasLabel(p,a)] * w_h_controllerHasObj;

weight w_h_predRelToParent: MDependency  -> Double;
factor: for Int a, Int p, MDependency l if
  possiblePredicate(p) & possibleArgument(a) & mst_dep(_,p,l) add[hasLabel(p,a)] * w_h_predRelToParent(l);

