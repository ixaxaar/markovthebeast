// Local rules

// Factors to determin the frameLabel of an argument:
//      FrameLabel <- ['01','02','03'...]

//weight w_ip_fl_slemma: Slemma x Ppos  x FrameLabel -> Double+;
//factor[2]: for Int i, Int j, Slemma l1, Ppos l2, FrameLabel f if slemma(i,l1) & ppos(j,l2) add [hasLabel(i,j) => frameLabel(i,f)] * w_ip_fl_slemma(l1,l2,f);
