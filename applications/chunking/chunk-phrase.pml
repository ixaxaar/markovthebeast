//position-based meta features
weight w_lexical_i: Word x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Word w_i
  if word(b,_) & word(e,_) & word(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_lexical_i(w_i, i - b, c);

weight w_case_i: Case x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Case c_i
  if word(b,_) & word(e,_) & case(i,c_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_case_i(c_i, i - b, c);

weight w_case_i: Case x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Case c_i
  if word(b,_) & word(e,_) & case(i,c_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_case_i(c_i, i - b, c);

weight w_prefix1_i: Case x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Case c_i
  if word(b,_) & word(e,_) & prefix1(i,c_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_prefix1_i(c_i, i - b, c);

weight w_prefix2_i: Case x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Case c_i
  if word(b,_) & word(e,_) & prefix2(i,c_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_prefix2_i(c_i, i - b, c);

weight w_prefix3_i: Case x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Case c_i
  if word(b,_) & word(e,_) & prefix3(i,c_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_prefix3_i(c_i, i - b, c);

weight w_postfix1_i: Case x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Case c_i
  if word(b,_) & word(e,_) & postfix1(i,c_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_postfix1_i(c_i, i - b, c);

weight w_postfix2_i: Case x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Case c_i
  if word(b,_) & word(e,_) & postfix2(i,c_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_postfix2_i(c_i, i - b, c);

weight w_postfix3_i: Case x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Case c_i
  if word(b,_) & word(e,_) & postfix3(i,c_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_postfix3_i(c_i, i - b, c);


