// Local rules

// Factors to determin the frameLabel of an argument:
//      FrameLabel <- ['01','02','03'...]


weight w_ip_fl_slemma: Slemma x Ppos  x FrameLabel -> Double;
factor: for Int i, Int j, Slemma l1, Ppos l2, FrameLabel f if slemma(i,l1) & ppos(j,l2) & hasLabel(i,j) add [frameLabel(i,f)] * w_ip_fl_slemma(l1,l2,f);


weight w_ip_fl_role: Slemma x Role  x FrameLabel -> Double;
factor: for Int p, Int a, Slemma l1, Role r, FrameLabel f if slemma(p,l1) & role(p,a,r) add [frameLabel(p,f)] * w_ip_fl_role(l1,r,f);

