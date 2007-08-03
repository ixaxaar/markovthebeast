//weight w_isarg_bias: Double-;
//factor: for Int c if candidate(c) add [isarg(c)] * w_isarg_bias;

weight w_isarg_path: Path -> Double;
factor: for Int c, Path p
  if candidate(c) & path(c,p) add [isarg(c)] * w_isarg_path(p);

//weight w_isarg_pathlength: Int -> Double;
//factor: for Int c, Int l
//  if candidate(c) & pathlength(c,l) add [isarg(c)] * w_isarg_pathlength(l);

//weight w_isarg_pathlengthlabel: Int x Label -> Double;
//factor: for Int c, Int l, Label label
//  if candidate(c) & pathlength(c,l) & label(c,label) add [isarg(c)] * w_isarg_pathlengthlabel(l,label);

weight w_isarg_pathpred: Path x Predicate -> Double;
factor: for Int c, Path p, Predicate pr
  if candidate(c) & path(c,p) & pred(_,pr,_) add [isarg(c)] * w_isarg_pathpred(p,pr);

weight w_isarg_label: Label -> Double;
factor: for Int c, Label label
  if candidate(c) & label(c,label) add [isarg(c)] * w_isarg_label(label);

weight w_isarg_parentlabel: Label -> Double;
factor: for Int c, Label label
  if candidate(c) & parentlabel(c,label) add [isarg(c)] * w_isarg_parentlabel(label);

//weight w_isarg_voice: Voice -> Double;
//factor: for Int c, Voice v
//  if candidate(c) & pred(_,_,v) add [isarg(c)] * w_isarg_voice(v);

//weight w_isarg_pred: Predicate -> Double;
//factor: for Int c, Predicate p
//  if candidate(c) & pred(_,p,_) add [isarg(c)] * w_isarg_pred(p);

weight w_isarg_labelpred: Label x Predicate -> Double;
factor: for Int c, Label label, Predicate p
  if candidate(c) & label(c,label) & pred(_,p,_) add [isarg(c)] * w_isarg_labelpred(label, p);

weight w_isarg_position: Position -> Double;
factor: for Int c, Position p
  if candidate(c) & position(c,p) add [isarg(c)] * w_isarg_position(p);

weight w_isarg_positionvoice: Position x Voice -> Double;
factor: for Int c, Position p, Voice v
  if candidate(c) & position(c,p) & pred(_,_,v) add [isarg(c)] * w_isarg_positionvoice(p,v);

weight w_isarg_positionpred: Position x Predicate -> Double;
factor: for Int c, Position p, Predicate pr
  if candidate(c) & position(c,p) & pred(_,pr,_) add [isarg(c)] * w_isarg_positionpred(p,pr);

weight w_isarg_positionpredvoice: Position x Predicate x Voice -> Double;
factor: for Int c, Position p, Predicate pr, Voice v
  if candidate(c) & position(c,p) & pred(_,pr,v) add [isarg(c)] * w_isarg_positionpredvoice(p,pr,v);

weight w_isarg_head: Word -> Double;
factor: for Int c, Int t, Word h
  if candidate(c) & head(c,t) & word(t,h)
  add [isarg(c)] * w_isarg_head(h);

weight w_isarg_headpred: Word x Predicate -> Double;
factor: for Int c, Int t, Word h, Predicate p
  if candidate(c) & head(c,t) & word(t,h) & pred(_,p,_)
  add [isarg(c)] * w_isarg_headpred(h,p);

weight w_isarg_parenthead: Word -> Double;
factor: for Int c, Int t, Word h
  if candidate(c) & parenthead(c,t) & word(t,h)
  add [isarg(c)] * w_isarg_parenthead(h);

//weight w_isarg_labelparenthead: Word x Label -> Double;
//factor: for Int c, Int t, Word h, Label label
//  if candidate(c) & parenthead(c,t) & word(t,h) & label(c,label)
//  add [isarg(c)] * w_isarg_labelparenthead(h,label);

weight w_isarg_rightlabel: Label -> Double;
factor: for Int c, Int s, Label label
  if candidate(c) & sister(c,s,_) & label(s,label)
  add [isarg(c)] * w_isarg_rightlabel(label);

weight w_isarg_leftlabel: Label -> Double;
factor: for Int c, Int s, Label label
  if candidate(c) & sister(c,_,s) & label(s,label)
  add [isarg(c)] * w_isarg_leftlabel(label);


weight w_isarg_chunkdistance: Int -> Double;
factor: for Int c, Int d
  if candidate(c) & chunkdistance(c,d)
  add [isarg(c)] * w_isarg_chunkdistance(d);

weight w_isarg_distance: Int -> Double;
factor: for Int c, Int d
  if candidate(c) & distance(c,d)
  add [isarg(c)] * w_isarg_distance(d);

weight w_isarg_length: Int -> Double;
factor: for Int c, Int b, Int e
  if candidate(c) & span(c,b,e)
  add [isarg(c)] * w_isarg_length(e-b);

weight w_isarg_firstword: Word -> Double;
factor: for Int c, Int b, Word w
  if candidate(c) & span(c,b,_) & word(b,w)
  add [isarg(c)] * w_isarg_firstword(w);

weight w_isarg_firstpos: Pos -> Double;
factor: for Int c, Int b, Pos p
  if candidate(c) & span(c,b,_) & pos(b,p)
  add [isarg(c)] * w_isarg_firstpos(p);

weight w_isarg_lastword: Word -> Double;
factor: for Int c, Int e, Word w
  if candidate(c) & span(c,_,e) & word(e,w)
  add [isarg(c)] * w_isarg_lastword(w);

weight w_isarg_lastpos: Pos -> Double;
factor: for Int c, Int e, Pos p
  if candidate(c) & span(c,_,e) & pos(e,p)
  add [isarg(c)] * w_isarg_lastpos(p);

//weight w_isarg_subcat: Subcat -> Double;
//factor: for  Subcat s, Int c
//  if subcat(s) & candidate(c) add [isarg(c)] * w_isarg_subcat(s);

weight w_isarg_frame: Frame -> Double;
factor: for Frame f, Int c
  if candidate(c) & frame(c,f) add [isarg(c)] * w_isarg_frame(f);

weight w_isarg_shortframe: ShortFrame -> Double;
factor: for ShortFrame f, Int c
  if candidate(c) & shortframe(c,f) add [isarg(c)] * w_isarg_shortframe(f);

weight w_isarg_framepred: Frame x Predicate -> Double;
factor: for  Frame f, Int c, Predicate p
  if candidate(c) & frame(c,f) & pred(_,p,_) add [isarg(c)] * w_isarg_framepred(f,p);

weight w_isarg_pprighthead: Word -> Double;
factor: for Int c, Int t, Word h
  if candidate(c) & pprightmosthead(c,t) & word(t,h)
  add [isarg(c)] * w_isarg_pprighthead(h);

weight w_isarg_pprightheadpos: Pos -> Double;
factor: for Int c, Int t, Pos h
  if candidate(c) & pprightmosthead(c,t) & pos(t,h)
  add [isarg(c)] * w_isarg_pprightheadpos(h);
