/*
  features based only one token(head or modifer
 */

//h_pos
weight hpos : Pos -> Double;
factor:
  for Int h, Int m, Pos h_pos
  if pos(h,h_pos) & word(m,_)
  add [link(h,m)] * hpos(h_pos);

weight hpos_l : Pos x Dep -> Double;
factor:
  for Int h, Int m, Dep l, Pos h_pos
  if pos(h,h_pos) & word(m,_)
  add [dep(h,m,l)] * hpos_l(h_pos,l);

weight hpos_d : Pos x Int -> Double;
factor:
  for Int h, Int m, Pos h_pos
  if pos(h,h_pos) & word(m,_)
  add [link(h,m)] * hpos_d(h_pos,bins(1,2,3,4,5,10,h-m));

weight hpos_l_d : Pos x Dep x Int -> Double;
factor:
  for Int h, Int m, Dep l, Pos h_pos
  if pos(h,h_pos) & word(m,_)
  add [dep(h,m,l)] * hpos_l_d(h_pos,l,bins(1,2,3,4,5,10,h-m));

//m_pos
weight mpos : Pos -> Double;
factor:
  for Int h, Int m, Pos m_pos
  if pos(m,m_pos) & word(h,_)
  add [link(h,m)] * mpos(m_pos);

weight mpos_l : Pos x Dep -> Double;
factor:
  for Int h, Int m, Dep l, Pos m_pos
  if pos(m,m_pos) & word(h,_)
  add [dep(h,m,l)] * mpos_l(m_pos,l);

weight mpos_d : Pos x Int -> Double;
factor:
  for Int h, Int m, Pos m_pos
  if pos(m,m_pos) & word(h,_)
  add [link(h,m)] * mpos_d(m_pos,bins(1,2,3,4,5,10,h-m));

weight mpos_l_d : Pos x Dep x Int -> Double;
factor:
  for Int h, Int m, Dep l, Pos m_pos
  if pos(m,m_pos) & word(h,_)
  add [dep(h,m,l)] * mpos_l_d(m_pos,l,bins(1,2,3,4,5,10,h-m));

//h_word
weight hword : Word -> Double;
factor:
  for Int h, Int m, Word h_word
  if word(h,h_word) & word(m,_)
  add [link(h,m)] * hword(h_word);

weight hword_l : Word x Dep -> Double;
factor:
  for Int h, Int m, Dep l, Word h_word
  if word(h,h_word) & word(m,_)
  add [dep(h,m,l)] * hword_l(h_word,l);

weight hword_d : Word x Int -> Double;
factor:
  for Int h, Int m, Word h_word
  if word(h,h_word) & word(m,_)
  add [link(h,m)] * hword_d(h_word,bins(1,2,3,4,5,10,h-m));

weight hword_l_d : Word x Dep x Int -> Double;
factor:
  for Int h, Int m, Dep l, Word h_word
  if word(h,h_word) & word(m,_)
  add [dep(h,m,l)] * hword_l_d(h_word,l,bins(1,2,3,4,5,10,h-m));

//m_word
weight mword : Word -> Double;
factor:
  for Int h, Int m, Word m_word
  if word(m,m_word) & word(h,_)
  add [link(h,m)] * mword(m_word);

weight mword_l : Word x Dep -> Double;
factor:
  for Int h, Int m, Dep l, Word m_word
  if word(m,m_word) & word(h,_)
  add [dep(h,m,l)] * mword_l(m_word,l);

weight mword_d : Word x Int -> Double;
factor:
  for Int h, Int m, Word m_word
  if word(m,m_word) & word(h,_)
  add [link(h,m)] * mword_d(m_word,bins(1,2,3,4,5,10,h-m));

weight mword_l_d : Word x Dep x Int -> Double;
factor:
  for Int h, Int m, Dep l, Word m_word
  if word(m,m_word) & word(h,_)
  add [dep(h,m,l)] * mword_l_d(m_word,l,bins(1,2,3,4,5,10,h-m));

//h_pos h_word
weight hposword : Pos x Word -> Double;
factor:
  for Int h, Int m, Pos h_pos, Word h_word
  if pos(h,h_pos) & word(m,_) & word(h,h_word)
  add [link(h,m)] * hposword(h_pos, h_word);

weight hposword_l : Pos x Word x Dep -> Double;
factor:
  for Int h, Int m, Dep l, Pos h_pos, Word h_word
  if pos(h,h_pos) & word(m,_) & word(h,h_word)
  add [dep(h,m,l)] * hposword_l(h_pos, h_word, l);

weight hposword_d : Pos x Word x Int -> Double;
factor:
  for Int h, Int m, Pos h_pos, Word h_word
  if pos(h,h_pos) & word(m,_) & word(h,h_word)
  add [link(h,m)] * hposword_d(h_pos,h_word,bins(1,2,3,4,5,10,h-m));

weight hposword_l_d : Pos x Word x Dep x Int -> Double;
factor:
  for Int h, Int m, Dep l, Pos h_pos, Word h_word
  if pos(h,h_pos) & word(m,_) & word(h,h_word)
  add [dep(h,m,l)] * hposword_l_d(h_pos, h_word, l,bins(1,2,3,4,5,10,h-m));

//m_pos m_word
weight mposword : Pos x Word -> Double;
factor:
  for Int h, Int m, Pos m_pos, Word m_word
  if pos(m,m_pos) & word(h,_) & word(m,m_word)
  add [link(h,m)] * mposword(m_pos, m_word);

weight mposword_l : Pos x Word x Dep -> Double;
factor:
  for Int h, Int m, Dep l, Pos m_pos, Word m_word
  if pos(m,m_pos) & word(h,_) & word(m,m_word)
  add [dep(h,m,l)] * mposword_l(m_pos, m_word, l);

weight mposword_d : Pos x Word x Int -> Double;
factor:
  for Int h, Int m, Pos m_pos, Word m_word
  if pos(m,m_pos) & word(h,_) & word(m,m_word)
  add [link(h,m)] * mposword_d(m_pos,m_word,bins(1,2,3,4,5,10,h-m));

weight mposword_l_d : Pos x Word x Dep x Int -> Double;
factor:
  for Int h, Int m, Dep l, Pos m_pos, Word m_word
  if pos(m,m_pos) & word(h,_) & word(m,m_word)
  add [dep(h,m,l)] * mposword_l_d(m_pos, m_word, l,bins(1,2,3,4,5,10,h-m));
