
// Johansson 2008 (Shared task)

weight w_fl_predWord: Word x FrameLabel -> Double;
factor: for Int p, Word l, FrameLabel f if
  possiblePredicate(p) & word(p,l) add[frameLabel(p,f)] * w_fl_predWord(l,f);

weight w_fl_predLemma: Lemma x FrameLabel -> Double;
factor: for Int p, Lemma l, FrameLabel f if
  possiblePredicate(p) & lemma(p,l) add[frameLabel(p,f)] * w_fl_predLemma(l,f);

weight w_fl_predParentWord: Word x FrameLabel -> Double;
factor: for  Int p, Int j, Word l, FrameLabel f if
  possiblePredicate(p) & mst_link(j,p) & word(j,l) add[frameLabel(p,f)] * w_fl_predParentWord(l,f);

//weight w_fl_predParentLemma: Lemma x FrameLabel -> Double;
//factor: for  Int p, Int j, Lemma l, FrameLabel f if
//  possiblePredicate(p) & mst_link(j,p) & lemma(j,l) add[frameLabel(p,f)] * w_fl_predParentLemma(l,f);

weight w_fl_predParentPos: Ppos x FrameLabel -> Double;
factor: for Int p, Int j, Ppos l, FrameLabel f if
  possiblePredicate(p) & mst_link(j,p) & ppos(j,l) add[frameLabel(p,f)] * w_fl_predParentPos(l,f);

weight w_fl_childDepSet: DepSet x FrameLabel -> Double;
factor: for Int p, DepSet l, FrameLabel f if
  possiblePredicate(p) & childDepSet(p,l) add[frameLabel(p,f)] * w_fl_childDepSet(l,f);

weight w_fl_childWordSet: WordSet x FrameLabel -> Double;
factor: for Int p, WordSet l, FrameLabel f if
  possiblePredicate(p) & childWordSet(p,l) add[frameLabel(p,f)] * w_fl_childWordSet(l,f);

weight w_fl_childWordDepSet: WordDepSet x FrameLabel -> Double;
factor: for Int p, WordDepSet l, FrameLabel f if
  possiblePredicate(p) & childWordDepSet(p,l) add[frameLabel(p,f)] * w_fl_childWordDepSet(l,f);

weight w_fl_childPosSet: PosSet x FrameLabel -> Double;
factor: for Int p, PosSet l, FrameLabel f if
  possiblePredicate(p) & childPosSet(p,l) add[frameLabel(p,f)] * w_fl_childPosSet(l,f);

weight w_fl_childPosDepSet: PosDepSet x FrameLabel -> Double;
factor: for Int p, PosDepSet l, FrameLabel f if
  possiblePredicate(p) & childPosDepSet(p,l) add[frameLabel(p,f)] * w_fl_childPosDepSet(l,f);

weight w_fl_depSubCat: SubCat x FrameLabel -> Double;
factor: for Int p, SubCat l, FrameLabel f if
  possiblePredicate(p) & depSubCat(p,l) add[frameLabel(p,f)] * w_fl_depSubCat(l,f);

weight w_fl_predRelToParent: MDependency  x FrameLabel -> Double;
factor: for Int p, MDependency l, FrameLabel f if
  possiblePredicate(p) & mst_dep(_,p,l) add[frameLabel(p,f)] * w_fl_predRelToParent(l,f);


