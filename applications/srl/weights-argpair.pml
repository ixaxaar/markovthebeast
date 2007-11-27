weight w_pair_bias: Argument x Argument -> Double-;
  factor: for Int c1, Int c2, Argument a1, Argument a2 if nooverlap(c1,c2)
  add [argpair(c1,c2,a1,a2)] * w_pair_bias(a1,a2);

set collector.all.w_pair_bias = true;

weight w_pair_voice: Voice x Argument x Argument -> Double;
factor: for Int c1, Int c2, Voice v, Argument a1, Argument a2
  if nooverlap(c1,c2) & pred(_,_,v)
  add [argpair(c1,c2,a1,a2)] * w_pair_voice(v,a1,a2);

weight w_pair_label: Label x Label x Argument x Argument -> Double;
factor: for Int c1, Int c2, Label label1, Label label2, Argument a1, Argument a2
  if nooverlap(c1,c2) & label(c1,label1) & label(c2,label2)
  add [argpair(c1,c2,a1,a2)] * w_pair_label(label1,label2,a1,a2);

weight w_pair_pred: Predicate x Argument x Argument -> Double;
factor: for Int c1, Int c2, Predicate p, Argument a1, Argument a2
  if nooverlap(c1,c2) & pred(_,p,_)
  add [argpair(c1,c2,a1,a2)] * w_pair_pred(p,a1,a2);

weight w_pair_position: Position x Position x Argument x Argument -> Double;
factor: for Int c1, Int c2, Position p1, Position p2, Argument a1, Argument a2
  if nooverlap(c1,c2) & position(c1,p1) & position(c2,p2)
  add [argpair(c1,c2,a1,a2)] * w_pair_position(p1,p2,a1,a2);

weight w_pair_frame: Frame x Frame x Argument x Argument -> Double;
factor: for Frame f1, Frame f2, Argument a1, Argument a2, Int c1, Int c2
  if nooverlap(c1,c2) & frame(c1,f1) & frame(c2,f2)
  add [argpair(c1,c2,a1,a2)] * w_pair_frame(f1,f2,a1,a2);

weight w_pair_subcat: Subcat x Argument x Argument -> Double;
factor: for  Subcat s, Argument a1, Argument a2, Int c1, Int c2
  if subcat(s) & nooverlap(c1,c2)
  add [argpair(c1,c2,a1,a2)] * w_pair_subcat(s,a1,a2);

weight w_pair_head: Word x Word x Argument x Argument -> Double;
factor: for Int c1, Int c2, Argument a1, Argument a2, Int t1, Int t2, Word h1, Word h2
  if nooverlap(c1,c2) & head(c1,t1) & word(t1,h1) & head(c2,t2) & word(t2,h2)
  add [argpair(c1,c2,a1,a2)] * w_pair_head(h1,h2,a1,a2);



/*
weight w_pair_path: Path x Argument -> Double;
factor: for Int c, Path p, Argument a
  if nooverlap(c1,c2) & path(c,p) add [arg(c,a)] * w_pair_path(p,a);

weight w_pair_pathpred: Path x Predicate x Argument -> Double;
factor: for Int c, Path p, Argument a, Predicate pr
  if nooverlap(c1,c2) & path(c,p) & pred(_,pr,_) add [arg(c,a)] * w_pair_pathpred(p,pr,a);

weight w_pair_label: Label x Argument -> Double;
factor: for Int c, Label label, Argument a
  if nooverlap(c1,c2) & label(c,label) add [arg(c,a)] * w_pair_label(label, a);

weight w_pair_parentlabel: Label x Argument -> Double;
factor: for Int c, Label label, Argument a
  if nooverlap(c1,c2) & parentlabel(c,label) add [arg(c,a)] * w_pair_parentlabel(label, a);

weight w_pair_voice: Voice x Argument -> Double;
factor: for Int c, Voice v, Argument a
  if nooverlap(c1,c2) & pred(_,_,v) add [arg(c,a)] * w_pair_voice(v, a);

weight w_pair_pred: Predicate x Argument -> Double;
factor: for Int c, Predicate p, Argument a
  if nooverlap(c1,c2) & pred(_,p,_) add [arg(c,a)] * w_pair_pred(p,a);

weight w_pair_labelpred: Label x Predicate x Argument -> Double;
factor: for Int c, Label label, Argument a, Predicate p
  if nooverlap(c1,c2) & label(c,label) & pred(_,p,_) add [arg(c,a)] * w_pair_labelpred(label, p, a);

weight w_pair_position: Position x Argument -> Double;
factor: for Int c, Position p, Argument a
  if nooverlap(c1,c2) & position(c,p) add [arg(c,a)] * w_pair_position(p,a);

weight w_pair_positionvoice: Position x Voice x Argument -> Double;
factor: for Int c, Position p, Voice v, Argument a
  if nooverlap(c1,c2) & position(c,p) & pred(_,_,v) add [arg(c,a)] * w_pair_positionvoice(p,v,a);

weight w_pair_positionpred: Position x Predicate x Argument -> Double;
factor: for Int c, Position p, Predicate pr, Argument a
  if nooverlap(c1,c2) & position(c,p) & pred(_,pr,_) add [arg(c,a)] * w_pair_positionpred(p,pr,a);

weight w_pair_positionpredvoice: Position x Predicate x Voice x Argument -> Double;
factor: for Int c, Position p, Predicate pr, Argument a, Voice v
  if nooverlap(c1,c2) & position(c,p) & pred(_,pr,v) add [arg(c,a)] * w_pair_positionpredvoice(p,pr,v,a);

weight w_pair_head: Word x Argument -> Double;
factor: for Int c, Argument a, Int t, Word h
  if nooverlap(c1,c2) & head(c,t) & word(t,h)
  add [arg(c,a)] * w_pair_head(h,a);

weight w_pair_distance: Int x Argument -> Double;
factor: for Int c, Int d, Argument a
  if nooverlap(c1,c2) & distance(c,d)
  add [arg(c,a)] * w_pair_distance(d,a);

weight w_pair_headpred: Word x Predicate x Argument -> Double;
factor: for Int c, Argument a, Int t, Word h, Predicate p
  if nooverlap(c1,c2) & head(c,t) & word(t,h) & pred(_,p,_)
  add [arg(c,a)] * w_pair_headpred(h,p,a);

weight w_pair_parenthead: Word x Argument -> Double;
factor: for Int c, Argument a, Int t, Word h
  if nooverlap(c1,c2) & parenthead(c,t) & word(t,h)
  add [arg(c,a)] * w_pair_parenthead(h,a);

//weight w_pair_labelparenthead: Word x Label x Argument -> Double;
//factor: for Int c, Argument a, Int t, Word h, Label label
//  if nooverlap(c1,c2) & parenthead(c,t) & word(t,h) & label(c,label)
//  add [arg(c,a)] * w_pair_labelparenthead(h,label,a);

weight w_pair_rightlabel: Label x Argument -> Double;
factor: for Int c, Int s, Argument a, Label label
  if nooverlap(c1,c2) & sister(c,_,s) & label(s,label)
  add [arg(c,a)] * w_pair_rightlabel(label,a);

weight w_pair_leftlabel: Label x Argument -> Double;
factor: for Int c, Int s, Argument a, Label label
  if nooverlap(c1,c2) & sister(c,s,_) & label(s,label)
  add [arg(c,a)] * w_pair_leftlabel(label,a);


weight w_pair_chunkdistance: Int x Argument -> Double;
factor: for Int c, Argument a, Int d
  if nooverlap(c1,c2) & chunkdistance(c,d)
  add [arg(c,a)] * w_pair_chunkdistance(d,a);

weight w_pair_length: Int x Argument -> Double;
factor: for Int c, Argument a, Int b, Int e
  if nooverlap(c1,c2) & span(c,b,e)
  add [arg(c,a)] * w_pair_length(e-b,a);

weight w_pair_firstword: Word x Argument -> Double;
factor: for Int c, Argument a, Int b, Word w
  if nooverlap(c1,c2) & span(c,b,_) & word(b,w)
  add [arg(c,a)] * w_pair_firstword(w,a);

weight w_pair_firstpos: Pos x Argument -> Double;
factor: for Int c, Argument a, Int b, Pos p
  if nooverlap(c1,c2) & span(c,b,_) & pos(b,p)
  add [arg(c,a)] * w_pair_firstpos(p,a);

weight w_pair_lastword: Word x Argument -> Double;
factor: for Int c, Argument a, Int e, Word w
  if nooverlap(c1,c2) & span(c,_,e) & word(e,w)
  add [arg(c,a)] * w_pair_lastword(w,a);

weight w_pair_lastpos: Pos x Argument -> Double;
factor: for Int c, Argument a, Int e, Pos p
  if nooverlap(c1,c2) & span(c,_,e) & pos(e,p)
  add [arg(c,a)] * w_pair_lastpos(p,a);

weight w_pair_subcat: Subcat x Argument -> Double;
factor: for  Subcat s, Argument a, Int c
  if subcat(s) & nooverlap(c1,c2) add [arg(c,a)] * w_pair_subcat(s,a);

weight w_pair_frame: Frame x Argument -> Double;
factor: for Frame f, Argument a, Int c
  if nooverlap(c1,c2) & frame(c,f) add [arg(c,a)] * w_pair_frame(f,a);

//weight w_pair_shortframe: ShortFrame x Argument -> Double;
//factor: for ShortFrame f, Argument a, Int c
//  if nooverlap(c1,c2) & shortframe(c,f) add [arg(c,a)] * w_pair_shortframe(f,a);

weight w_pair_framepred: Frame x Predicate x Argument -> Double;
factor: for  Frame f, Argument a, Int c, Predicate p
  if nooverlap(c1,c2) & frame(c,f) & pred(_,p,_) add [arg(c,a)] * w_pair_framepred(f,p,a);

weight w_pair_pprighthead: Word x Argument -> Double;
factor: for Int c, Argument a, Int t, Word h
  if nooverlap(c1,c2) & pprightmosthead(c,t) & word(t,h)
  add [arg(c,a)] * w_pair_pprighthead(h,a);

weight w_pair_pprightheadpos: Pos x Argument -> Double;
factor: for Int c, Argument a, Int t, Pos h
  if nooverlap(c1,c2) & pprightmosthead(c,t) & pos(t,h)
  add [arg(c,a)] * w_pair_pprightheadpos(h,a);
*/