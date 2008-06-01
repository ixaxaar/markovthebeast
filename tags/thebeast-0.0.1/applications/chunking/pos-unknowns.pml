//features for unknown words
weight w_case: Case x Pos -> Double;
factor:
  for Int i, Pos p, Case w_i, Word w
  if case(i,w_i) & word(i,w) & rare(w)
  add [pos(i,p)] * w_case(w_i,p);

weight w_prefix1: Prefix1 x Pos -> Double;
factor:
  for Int i, Pos p, Prefix1 w_i, Word w
  if prefix1(i,w_i) & word(i,w) & rare(w)
  add [pos(i,p)] * w_prefix1(w_i,p);

weight w_postfix1: Postfix1 x Pos -> Double;
factor:
  for Int i, Pos p, Postfix1 w_i, Word w
  if postfix1(i,w_i) & word(i,w) & rare(w)
  add [pos(i,p)] * w_postfix1(w_i,p);

weight w_prefix2: Prefix2 x Pos -> Double;
factor:
  for Int i, Pos p, Prefix2 w_i, Word w
  if prefix2(i,w_i) & word(i,w) & rare(w)
  add [pos(i,p)] * w_prefix2(w_i,p);

weight w_postfix2: Postfix2 x Pos -> Double;
factor:
  for Int i, Pos p, Postfix2 w_i, Word w
  if postfix2(i,w_i) & word(i,w) & rare(w)
  add [pos(i,p)] * w_postfix2(w_i,p);

weight w_prefix3: Prefix3 x Pos -> Double;
factor:
  for Int i, Pos p, Prefix3 w_i, Word w
  if prefix3(i,w_i) & word(i,w) & rare(w)
  add [pos(i,p)] * w_prefix3(w_i,p);

weight w_postfix3: Postfix3 x Pos -> Double;
factor:
  for Int i, Pos p, Postfix3 w_i, Word w
  if postfix3(i,w_i) & word(i,w) & rare(w)
  add [pos(i,p)] * w_postfix3(w_i,p);

weight w_prefix4: Prefix4 x Pos -> Double;
factor:
  for Int i, Pos p, Prefix4 w_i, Word w
  if prefix4(i,w_i) & word(i,w) & rare(w)
  add [pos(i,p)] * w_prefix4(w_i,p);

weight w_postfix4: Postfix4 x Pos -> Double;
factor:
  for Int i, Pos p, Postfix4 w_i, Word w
  if postfix4(i,w_i) & word(i,w) & rare(w)
  add [pos(i,p)] * w_postfix4(w_i,p);

weight w_cardinal: Cardinal x Pos -> Double;
factor:
  for Int i, Pos p, Cardinal w_i, Word w
  if cardinal(i,w_i) & word(i,w) & rare(w)
  add [pos(i,p)] * w_cardinal(w_i,p);

weight w_hyphen: Hyphen x Pos -> Double;
factor:
  for Int i, Pos p, Hyphen w_i, Word w
  if hyphen(i,w_i) & word(i,w) & rare(w)
  add [pos(i,p)] * w_hyphen(w_i,p);
