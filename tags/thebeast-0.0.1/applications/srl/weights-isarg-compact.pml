//weight w_isarg_bias: Double-;
//factor: for Int c if candidate(c) add [isarg(c)] * w_isarg_bias;

weight w_isarg_path: Path -> Double;
factor: for Int c, Path p
  if candidate(c) & path(c,p) add [isarg(c)] * w_isarg_path(p);

weight w_isarg_pathpred: Path x Predicate -> Double;
factor: for Int c, Path p, Predicate pr
  if candidate(c) & path(c,p) & pred(_,pr,_) add [isarg(c)] * w_isarg_pathpred(p,pr);

weight w_isarg_label: Label -> Double;
factor: for Int c, Label label
  if candidate(c) & label(c,label) add [isarg(c)] * w_isarg_label(label);

weight w_isarg_pred: Predicate -> Double;
factor: for Int c, Predicate p
  if candidate(c) & pred(_,p,_) add [isarg(c)] * w_isarg_pred(p);

weight w_isarg_labelpred: Label x Predicate -> Double;
factor: for Int c, Label label, Predicate p
  if candidate(c) & label(c,label) & pred(_,p,_) add [isarg(c)] * w_isarg_labelpred(label, p);

weight w_isarg_head: Word -> Double;
factor: for Int c, Int t, Word h
  if candidate(c) & head(c,t) & word(t,h)
  add [isarg(c)] * w_isarg_head(h);

weight w_isarg_chunkdistance: Int -> Double;
factor: for Int c, Int d
  if candidate(c) & chunkdistance(c,d)
  add [isarg(c)] * w_isarg_chunkdistance(d);

