
// Johansson 2008 (Shared task)

weight w_ip_predWord: Word -> Double;
factor: for Int p, Word l if
  possiblePredicate(p) & word(p,l) add[isPredicate(p)] * w_ip_predWord(l);

weight w_ip_predLemma: Lemma -> Double;
factor: for Int p, Lemma l if
  possiblePredicate(p) & lemma(p,l) add[isPredicate(p)] * w_ip_predLemma(l);

weight w_ip_predParentWord: Word -> Double;
factor: for  Int p, Int j, Word l if
  possiblePredicate(p) & mst_link(j,p) & word(j,l) add[isPredicate(p)] * w_ip_predParentWord(l);

//weight w_ip_predParentLemma: Lemma -> Double;
//factor: for  Int p, Int j, Lemma l if
//  possiblePredicate(p) & mst_link(j,p) & lemma(j,l) add[isPredicate(p)] * w_ip_predParentLemma(l);

weight w_ip_predParentPos: Ppos -> Double;
factor: for Int p, Int j, Ppos l if
  possiblePredicate(p) & mst_link(j,p) & ppos(j,l) add[isPredicate(p)] * w_ip_predParentPos(l);

weight w_ip_childDepSet: DepSet -> Double;
factor: for Int p, DepSet l if
  possiblePredicate(p) & childDepSet(p,l) add[isPredicate(p)] * w_ip_childDepSet(l);

weight w_ip_childWordSet: WordSet -> Double;
factor: for Int p, WordSet l if
  possiblePredicate(p) & childWordSet(p,l) add[isPredicate(p)] * w_ip_childWordSet(l);

weight w_ip_childWordDepSet: WordDepSet -> Double;
factor: for Int p, WordDepSet l if
  possiblePredicate(p) & childWordDepSet(p,l) add[isPredicate(p)] * w_ip_childWordDepSet(l);

weight w_ip_childPosSet: PosSet -> Double;
factor: for Int p, PosSet l if
  possiblePredicate(p) & childPosSet(p,l) add[isPredicate(p)] * w_ip_childPosSet(l);

weight w_ip_childPosDepSet: PosDepSet -> Double;
factor: for Int p, PosDepSet l if
  possiblePredicate(p) & childPosDepSet(p,l) add[isPredicate(p)] * w_ip_childPosDepSet(l);

weight w_ip_depSubCat: SubCat -> Double;
factor: for Int p, SubCat l if
  possiblePredicate(p) & depSubCat(p,l) add[isPredicate(p)] * w_ip_depSubCat(l);

weight w_ip_predRelToParent: MDependency  -> Double;
factor: for Int p, MDependency l if
  possiblePredicate(p) & mst_dep(_,p,l) add[isPredicate(p)] * w_ip_predRelToParent(l);


