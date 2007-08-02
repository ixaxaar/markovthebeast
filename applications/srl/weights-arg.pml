weight w_bias: Argument -> Double-;
  factor: for Int c, Argument a if candidate(c) add [arg(c,a)] * w_bias(a);

weight w_path: Path x Argument -> Double;
factor: for Int c, Path p, Argument a
  if candidate(c) & path(c,p) add [arg(c,a)] * w_path(p,a);

weight w_pathpred: Path x Predicate x Argument -> Double;
factor: for Int c, Path p, Argument a, Predicate pr
  if candidate(c) & path(c,p) & pred(_,pr,_) add [arg(c,a)] * w_pathpred(p,pr,a);

weight w_label: Label x Argument -> Double;
factor: for Int c, Label label, Argument a
  if candidate(c) & label(c,label) add [arg(c,a)] * w_label(label, a);

weight w_parentlabel: Label x Argument -> Double;
factor: for Int c, Label label, Argument a
  if candidate(c) & parentlabel(c,label) add [arg(c,a)] * w_parentlabel(label, a);

weight w_voice: Voice x Argument -> Double;
factor: for Int c, Voice v, Argument a
  if candidate(c) & pred(_,_,v) add [arg(c,a)] * w_voice(v, a);

weight w_pred: Predicate x Argument -> Double;
factor: for Int c, Predicate p, Argument a
  if candidate(c) & pred(_,p,_) add [arg(c,a)] * w_pred(p,a);

weight w_labelpred: Label x Predicate x Argument -> Double;
factor: for Int c, Label label, Argument a, Predicate p
  if candidate(c) & label(c,label) & pred(_,p,_) add [arg(c,a)] * w_labelpred(label, p, a);

weight w_position: Position x Argument -> Double;
factor: for Int c, Position p, Argument a
  if candidate(c) & position(c,p) add [arg(c,a)] * w_position(p,a);

weight w_positionvoice: Position x Voice x Argument -> Double;
factor: for Int c, Position p, Voice v, Argument a
  if candidate(c) & position(c,p) & pred(_,_,v) add [arg(c,a)] * w_positionvoice(p,v,a);

weight w_positionpred: Position x Predicate x Argument -> Double;
factor: for Int c, Position p, Predicate pr, Argument a
  if candidate(c) & position(c,p) & pred(_,pr,_) add [arg(c,a)] * w_positionpred(p,pr,a);

weight w_positionpredvoice: Position x Predicate x Voice x Argument -> Double;
factor: for Int c, Position p, Predicate pr, Argument a, Voice v
  if candidate(c) & position(c,p) & pred(_,pr,v) add [arg(c,a)] * w_positionpredvoice(p,pr,v,a);

weight w_head: Word x Argument -> Double;
factor: for Int c, Argument a, Int t, Word h
  if candidate(c) & head(c,t) & word(t,h)
  add [arg(c,a)] * w_head(h,a);

weight w_headpred: Word x Predicate x Argument -> Double;
factor: for Int c, Argument a, Int t, Word h, Predicate p
  if candidate(c) & head(c,t) & word(t,h) & pred(_,p,_)
  add [arg(c,a)] * w_headpred(h,p,a);

weight w_parenthead: Word x Argument -> Double;
factor: for Int c, Argument a, Int t, Word h
  if candidate(c) & parenthead(c,t) & word(t,h)
  add [arg(c,a)] * w_parenthead(h,a);

//weight w_labelparenthead: Word x Label x Argument -> Double;
//factor: for Int c, Argument a, Int t, Word h, Label label
//  if candidate(c) & parenthead(c,t) & word(t,h) & label(c,label)
//  add [arg(c,a)] * w_labelparenthead(h,label,a);

weight w_rightlabel: Label x Argument -> Double;
factor: for Int c, Int s, Argument a, Label label
  if candidate(c) & sister(c,s,_) & label(s,label)
  add [arg(c,a)] * w_rightlabel(label,a);

weight w_leftlabel: Label x Argument -> Double;
factor: for Int c, Int s, Argument a, Label label
  if candidate(c) & sister(c,_,s) & label(s,label)
  add [arg(c,a)] * w_leftlabel(label,a);


weight w_chunkdistance: Int x Argument -> Double;
factor: for Int c, Argument a, Int d
  if candidate(c) & chunkdistance(c,d)
  add [arg(c,a)] * w_chunkdistance(d,a);

weight w_length: Int x Argument -> Double;
factor: for Int c, Argument a, Int b, Int e
  if candidate(c) & span(c,b,e)
  add [arg(c,a)] * w_length(e-b,a);

weight w_firstword: Word x Argument -> Double;
factor: for Int c, Argument a, Int b, Word w
  if candidate(c) & span(c,b,_) & word(b,w)
  add [arg(c,a)] * w_firstword(w,a);

weight w_firstpos: Pos x Argument -> Double;
factor: for Int c, Argument a, Int b, Pos p
  if candidate(c) & span(c,b,_) & pos(b,p)
  add [arg(c,a)] * w_firstpos(p,a);

weight w_lastword: Word x Argument -> Double;
factor: for Int c, Argument a, Int e, Word w
  if candidate(c) & span(c,_,e) & word(e,w)
  add [arg(c,a)] * w_lastword(w,a);

weight w_lastpos: Pos x Argument -> Double;
factor: for Int c, Argument a, Int e, Pos p
  if candidate(c) & span(c,_,e) & pos(e,p)
  add [arg(c,a)] * w_lastpos(p,a);

weight w_subcat: Subcat x Argument -> Double;
factor: for  Subcat s, Argument a, Int c
  if subcat(s) & candidate(c) add [arg(c,a)] * w_subcat(s,a);

weight w_frame: Frame x Argument -> Double;
factor: for Frame f, Argument a, Int c
  if candidate(c) & frame(c,f) add [arg(c,a)] * w_frame(f,a);

weight w_framepred: Frame x Predicate x Argument -> Double;
factor: for  Frame f, Argument a, Int c, Predicate p
  if candidate(c) & frame(c,f) & pred(_,p,_) add [arg(c,a)] * w_framepred(f,p,a);

weight w_pprighthead: Word x Argument -> Double;
factor: for Int c, Argument a, Int t, Word h
  if candidate(c) & pprightmosthead(c,t) & word(t,h)
  add [arg(c,a)] * w_pprighthead(h,a);

weight w_pprightheadpos: Pos x Argument -> Double;
factor: for Int c, Argument a, Int t, Pos h
  if candidate(c) & pprightmosthead(c,t) & pos(t,h)
  add [arg(c,a)] * w_pprightheadpos(h,a);
