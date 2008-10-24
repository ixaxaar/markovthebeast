
// Johansson 2008 (Shared task)
// The following two would go to Global once (frameLabel and isPredicate argument are
// observable)

weight w_r_predLemmaSense: Role x Lemma x FrameLabel -> Double;
factor: for Int a, Int p, Role r, Lemma l, FrameLabel s if
  lemma(p,l) & possiblePredicate(p) & possibleArgument(a) & isPredicate(p) & frameLabel(p,s) add[role(p,a,r)] * w_r_predLemmaSense(r,l,s);

weight w_r_predLemma: Role x Lemma -> Double;
factor: for Int a, Int p, Role r, Lemma l if
  lemma(p,l) & possiblePredicate(p) & possibleArgument(a) & isPredicate(p) add[role(p,a,r)] * w_r_predLemma(r,l);

weight w_r_predSense: Role x FrameLabel -> Double;
factor: for Int a, Int p, FrameLabel l, Role r if
  possiblePredicate(p) & possibleArgument(a) & frameLabel(p,l) add[role(p,a,r)] * w_r_predSense(r,l);

weight w_r_voice: Role x Voice -> Double;
factor: for Int a, Int p, Voice v, Role r if
  possiblePredicate(p) & possibleArgument(a) & voice(p,v) add[role(p,a,r)] * w_r_voice(r,v);

weight w_r_pos_before: Role x Lemma -> Double;
factor: for Int a, Int p, Role r, Lemma l if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) & a<p add[role(p,a,r)] * w_r_pos_before(r,l);

weight w_r_pos_after: Role x Lemma -> Double;
factor: for Int a, Int p, Role r, Lemma l if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) & a>p add[role(p,a,r)] * w_r_pos_after(r,l);

weight w_r_pos_equal: Role x Lemma -> Double;
factor: for Int a, Int p, Role r, Lemma l if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) & a==p add[role(p,a,r)] * w_r_pos_equal(r,l);

weight w_r_argWord: Role x Lemma -> Double;
factor: for Int a, Int p, Lemma l, Role r if
  possiblePredicate(p) & possibleArgument(a) & lemma(a,l) add[role(p,a,r)] * w_r_argWord(r,l);

weight w_r_argPpos: Role x Ppos -> Double;
factor: for Int a, Int p, Ppos l, Role r if
  possiblePredicate(p) & possibleArgument(a) & ppos(a,l) add[role(p,a,r)] * w_r_argPpos(r,l);

weight w_r_leftWord: Role x Lemma -> Double;
factor: for Int a, Int p, Lemma l, Int t, Role r if
  possiblePredicate(p) & possibleArgument(a) & leftToken(a,t) & lemma(t,l) add[role(p,a,r)] * w_r_leftWord(r,l);

weight w_r_rigthWord: Role x Lemma -> Double;
factor: for Int a, Int p, Lemma l, Int t, Role r if
  possiblePredicate(p) & possibleArgument(a) & rightToken(a,t) & lemma(t,l)  add[role(p,a,r)] * w_r_rigthWord(r,l);

weight w_r_leftPos: Role x Ppos -> Double;
factor: for Int a, Int p, Ppos l, Int t, Role r if
  possiblePredicate(p) & possibleArgument(a) & leftToken(a,t) & ppos(t,l) add[role(p,a,r)] * w_r_leftPos(r,l);

weight w_r_rigthPos: Role x Ppos -> Double;
factor: for Int a, Int p, Ppos l, Int t,  Role r if
  possiblePredicate(p) & possibleArgument(a) & rightToken(a,t) & ppos(t,l)  add[role(p,a,r)] * w_r_rigthPos(r,l);

// Note, we don't distinguish between most left/right sibblings
weight w_r_leftSiblingWord: Role x Lemma -> Double;
factor: for Int a, Int p, Lemma l, Int j, Int s, Role r if
  possiblePredicate(p) & possibleArgument(a) & mst_link(j,a) & mst_link(j,s) & lemma(s,l) & s < a add[role(p,a,r)] * w_r_leftSiblingWord(r,l);

weight w_r_rigthSiblingWord: Role x Lemma -> Double;
factor: for Int a, Int p, Lemma l, Int j, Int s, Role r if
  possiblePredicate(p) & possibleArgument(a) & mst_link(j,a) & mst_link(j,s) & lemma(s,l) & s > a add[role(p,a,r)] * w_r_rigthSiblingWord(r,l);

weight w_r_leftSiblingPos: Role x Ppos -> Double;
factor: for Int a, Int p, Ppos l, Int j, Int s, Role r if
  possiblePredicate(p) & possibleArgument(a) & mst_link(j,a) & mst_link(j,s) & ppos(s,l) & s < a add[role(p,a,r)] * w_r_leftSiblingPos(r,l);

weight w_r_rigthSiblingPos: Role x Ppos -> Double;
factor: for Int a, Int p, Ppos l, Int j, Int s, Role r if
  possiblePredicate(p) & possibleArgument(a) & mst_link(j,a) & mst_link(j,s) & ppos(s,l) & s > a add[role(p,a,r)] * w_r_rigthSiblingPos(r,l);

weight w_r_predPos: Role x Ppos -> Double;
factor: for Int a, Int p, Ppos l,  Role r if
  possiblePredicate(p) & possibleArgument(a) & ppos(p,l) add[role(p,a,r)] * w_r_predPos(r,l);

weight w_r_relPath: Role x RelPath -> Double;
factor: for Int a, Int p, RelPath l, Role r if
  possiblePredicate(p) & possibleArgument(a) & relPath(p,a,l) add[role(p,a,r)] * w_r_relPath(r,l);

weight w_r_verbChainHasSubj: Role -> Double;
factor: for Int a, Int p, RelPath l, Role r if
  possiblePredicate(p) & possibleArgument(a) & verbChainHasSubj(p,a) add[role(p,a,r)] * w_r_verbChainHasSubj(r);

weight w_r_function: Role x MDependency  -> Double;
factor: for Int a, Int p, MDependency l, Role r if
  possiblePredicate(p) & possibleArgument(a) & mst_dep(_,a,l) add[role(p,a,r)] * w_r_function(r,l);

weight w_r_predRelToParent: Role x MDependency  -> Double;
factor: for Int a, Int p, MDependency l, Role r if
  possiblePredicate(p) & possibleArgument(a) & mst_dep(_,p,l) add[role(p,a,r)] * w_r_predRelToParent(r,l);

