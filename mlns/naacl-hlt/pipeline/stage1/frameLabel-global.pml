// Local rules

// Factors to determin the frameLabel of an argument:
//      FrameLabel <- ['01','02','03'...]


weight w_ip_fl_slemma: Slemma x Ppos  x FrameLabel -> Double+;
factor[6]: for Int p, Int a, Slemma l1, Ppos l2, FrameLabel f if slemma(p,l1) & ppos(a,l2) & possiblePredicate(p) & possibleArgument(a) add [hasLabel(p,a) => frameLabel(p,f)] * w_ip_fl_slemma(l1,l2,f);

