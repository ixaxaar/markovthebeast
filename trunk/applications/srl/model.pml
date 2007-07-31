predicate word: Int x Word;
predicate pos: Int x Pos;
predicate span: Int x Int x Int;
predicate label: Int x Labeller x Label;
predicate parentlabel: Int x Labeller x Label;
predicate head: Int x Labeller x Int;
predicate parenthead: Int x Labeller x Int;
predicate candidate: Int;
predicate arg: Int x Argument;
predicate path: Int x Labeller x Path;
predicate pred: Int x Predicate x Voice;
predicate subcat: Labeller x Subcat;
predicate position: Int x Position;
predicate frame: Int x Labeller x Frame;
predicate framepattern: Int x Labeller x FramePattern;
predicate chunkdistance: Int x Int;
predicate sister: Int x Labeller x Int x Int;

//index: span(*,*,_);

hidden: arg;
observed: word,pos,span,label,head,candidate,pred,path,subcat,position,
  frame,chunkdistance,framepattern,parentlabel,parenthead;

weight w_path: Path x Labeller x Argument -> Double;
factor: for Int c, Path p, Labeller labeller, Argument a
  if candidate(c) & path(c,labeller,p) add [arg(c,a)] * w_path(p,labeller,a);

weight w_label: Labeller x Label x Argument -> Double;
factor: for Int c, Label label, Labeller labeller, Argument a
  if candidate(c) & label(c,labeller,label) add [arg(c,a)] * w_label(labeller, label, a);

weight w_parentlabel: Labeller x Label x Argument -> Double;
factor: for Int c, Label label, Labeller labeller, Argument a
  if candidate(c) & parentlabel(c,labeller,label) add [arg(c,a)] * w_parentlabel(labeller, label, a);

weight w_voice: Voice x Argument -> Double;
factor: for Int c, Voice v, Argument a
  if candidate(c) & pred(_,_,v) add [arg(c,a)] * w_voice(v, a);

weight w_pred: Predicate x Argument -> Double;
factor: for Int c, Predicate p, Argument a
  if candidate(c) & pred(_,p,_) add [arg(c,a)] * w_pred(p,a);

weight w_labelpred: Labeller x Label x Predicate x Argument -> Double;
factor: for Int c, Label label, Labeller labeller, Argument a, Predicate p
  if candidate(c) & label(c,labeller,label) & pred(_,p,_) add [arg(c,a)] * w_labelpred(labeller, label, p, a);

weight w_position: Position x Argument -> Double;
factor: for Int c, Position p, Argument a
  if candidate(c) & position(c,p) add [arg(c,a)] * w_position(p,a);

weight w_positionvoice: Position x Voice x Argument -> Double;
factor: for Int c, Position p, Voice v, Argument a
  if candidate(c) & position(c,p) & pred(_,_,v) add [arg(c,a)] * w_positionvoice(p,v,a);

weight w_head: Word x Labeller x Argument -> Double;
factor: for Int c, Argument a, Int t, Word h, Labeller l
  if candidate(c) & head(c,l,t) & word(t,h)
  add [arg(c,a)] * w_head(h,l,a);

weight w_headpred: Word x Labeller x Predicate x Argument -> Double;
factor: for Int c, Argument a, Int t, Word h, Labeller l, Predicate p
  if candidate(c) & head(c,l,t) & word(t,h) & pred(_,p,_)
  add [arg(c,a)] * w_headpred(h,l,p,a);

weight w_parenthead: Word x Labeller x Argument -> Double;
factor: for Int c, Argument a, Int t, Word h, Labeller l
  if candidate(c) & parenthead(c,l,t) & word(t,h)
  add [arg(c,a)] * w_parenthead(h,l,a);


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

weight w_subcat: Labeller x Subcat x Argument -> Double;
factor: for Labeller l, Subcat s, Argument a, Int c
  if subcat(l,s) & candidate(c) add [arg(c,a)] * w_subcat(l,s,a);

weight w_frame: Labeller x Frame x Argument -> Double;
factor: for Labeller l, Frame f, Argument a, Int c
  if candidate(c) & frame(c,l,f) add [arg(c,a)] * w_frame(l,f,a);

weight w_framepred: Labeller x Frame x Predicate x Argument -> Double;
factor: for Labeller l, Frame f, Argument a, Int c, Predicate p
  if candidate(c) & frame(c,l,f) & pred(_,p,_) add [arg(c,a)] * w_framepred(l,f,p,a);


//no overlaps
factor: for Int c1, Int c2, Int b1, Int e1, Int b2, Int e2, Argument a1, Argument a2
  if span(c1,b1,e1) & span(c2,b2,e2) & b1 < b2 & e1 >= b2 : !(arg(c1,a1) & arg(c2,a2));

//not more than one argument for a candidate
factor: for Int c if candidate(c): |Argument a: arg(c,a)| <= 1;

//not duplicate arguments
factor: |Int c: arg(c,"V")| <= 1;
factor: |Int c: arg(c,"A0")| <= 1;
factor: |Int c: arg(c,"A1")| <= 1;
factor: |Int c: arg(c,"A2")| <= 1;
factor: |Int c: arg(c,"A3")| <= 1;
factor: |Int c: arg(c,"A4")| <= 1;

/*
factor: for Predicate p, Int t, Argument a if pred(t,p,Active)
  add [|Int c, Int b: arg(c,a) & span(c,b,_) & b > t | >= 1] * w_activeright(p,a);
*/