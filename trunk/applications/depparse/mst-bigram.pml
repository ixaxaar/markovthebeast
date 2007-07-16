/* these are (roughly) the "bigram features" mentioned in McDonald et al.
   Each comes in two flavors: without or with label (called a_l). And each of these can come in two more
   flavors: without or with a (binned) distance (called a_d)

   We call head attributes h_x and modifier attributes m_x */

//h_pos, m_pos (and derivations)
//min(l,h,m) & max(r,h,m)
weight pos : Pos x Pos -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos
  if pos(h,h_pos) & pos(m,m_pos) 
  add [link(h,m)] * pos(h_pos,m_pos);

weight pos_l : Pos x Pos x Dep-> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos)
  add [dep(h,m,label)] * pos_l(h_pos,m_pos,label);

weight pos_d : Pos x Pos x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos
  if pos(h,h_pos) & pos(m,m_pos)
  add [link(h,m)] * pos_d(h_pos,m_pos, bins(1,2,3,4,5,10,h-m));

weight pos_l_d : Pos x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Dep label
  if pos(h,h_pos) & pos(m,m_pos)
  add [dep(h,m,label)] * pos_l_d(h_pos,m_pos,label, bins(1,2,3,4,5,10,h-m));

//h_pos, m_pos, h_word (and derivations)
weight pos_hw : Pos x Pos x Word -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Word h_word
  if pos(h,h_pos) & pos(m,m_pos) & word(h,h_word)
  add [link(h,m)] * pos_hw(h_pos,m_pos, h_word);

weight pos_hw_l : Pos x Pos x Word x Dep-> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Dep label, Word h_word
  if pos(h,h_pos) & pos(m,m_pos) & word(h,h_word)
  add [dep(h,m,label)] * pos_hw_l(h_pos,m_pos,h_word,label);

weight pos_hw_d : Pos x Pos x Word x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Word h_word
  if pos(h,h_pos) & pos(m,m_pos) & word(h,h_word)
  add [link(h,m)] * pos_hw_d(h_pos,m_pos, h_word, bins(1,2,3,4,5,10,h-m));

weight pos_hw_l_d : Pos x Pos x Word x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Dep label, Word h_word
  if pos(h,h_pos) & pos(m,m_pos) & word(h,h_word)
  add [dep(h,m,label)] * pos_hw_l_d(h_pos,m_pos,h_word,label,bins(1,2,3,4,5,10,h-m));

//h_pos, m_pos, m_word (and derivations)
weight pos_mw : Pos x Pos x Word -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Word m_word
  if pos(h,h_pos) & pos(m,m_pos) & word(m,m_word)
  add [link(h,m)] * pos_mw(h_pos,m_pos, m_word);

weight pos_mw_l : Pos x Pos x Word x Dep-> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Dep label, Word m_word
  if pos(h,h_pos) & pos(m,m_pos) & word(m,m_word)
  add [dep(h,m,label)] * pos_mw_l(h_pos,m_pos,m_word,label);

weight pos_mw_d : Pos x Pos x Word x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Word m_word
  if pos(h,h_pos) & pos(m,m_pos) & word(m,m_word)
  add [link(h,m)] * pos_mw_d(h_pos,m_pos, m_word, bins(1,2,3,4,5,10,h-m));

weight pos_mw_l_d : Pos x Pos x Word x Dep x Int -> Double;
factor :
  for Int h, Int m, Pos h_pos, Pos m_pos, Dep label, Word m_word
  if pos(h,h_pos) & pos(m,m_pos) & word(m,m_word)
  add [dep(h,m,label)] * pos_mw_l_d(h_pos,m_pos,m_word,label,bins(1,2,3,4,5,10,h-m));

//h_word, m_pos, m_word (and derivations)
weight pos_mp : Word x Pos x Word -> Double;
factor :
  for Int h, Int m, Word h_word, Pos m_pos, Word m_word
  if word(h,h_word) & pos(m,m_pos) & word(m,m_word)
  add [link(h,m)] * pos_mp(h_word,m_pos,m_word);

weight pos_mp_l : Word x Pos x Word x Dep -> Double;
factor :
  for Int h, Int m, Word h_word, Pos m_pos, Word m_word, Dep label
  if word(h,h_word) & pos(m,m_pos) & word(m,m_word)
  add [dep(h,m,label)] * pos_mp_l(h_word,m_pos,m_word,label);

weight pos_mp_d : Word x Pos x Word x Int -> Double;
factor :
  for Int h, Int m, Word h_word, Pos m_pos, Word m_word
  if word(h,h_word) & pos(m,m_pos) & word(m,m_word)
  add [link(h,m)] * pos_mp_d(h_word,m_pos,m_word, bins(1,2,3,4,5,10,h-m));

weight pos_mp_l_d : Word x Pos x Word x Dep x Int -> Double;
factor :
  for Int h, Int m, Word h_word, Pos m_pos, Word m_word, Dep label
  if word(h,h_word) & pos(m,m_pos) & word(m,m_word)
  add [dep(h,m,label)] * pos_mp_l_d(h_word,m_pos,m_word,label,bins(1,2,3,4,5,10,h-m));

//h_word, h_pos, m_word (and derivations)
weight pos_hp : Word x Pos x Word -> Double;
factor :
  for Int h, Int m, Word h_word, Pos h_pos, Word m_word
  if word(h,h_word) & pos(h,h_pos) & word(m,m_word)
  add [link(h,m)] * pos_hp(h_word,h_pos,m_word);

weight pos_hp_l : Word x Pos x Word x Dep -> Double;
factor :
  for Int h, Int m, Word h_word, Pos h_pos, Word m_word, Dep label
  if word(h,h_word) & pos(h,h_pos) & word(m,m_word)
  add [dep(h,m,label)] * pos_hp_l(h_word,h_pos,m_word,label);

weight pos_hp_d : Word x Pos x Word x Int -> Double;
factor :
  for Int h, Int m, Word h_word, Pos h_pos, Word m_word
  if word(h,h_word) & pos(h,h_pos) & word(m,m_word)
  add [link(h,m)] * pos_hp_d(h_word,h_pos,m_word, bins(1,2,3,4,5,10,h-m));

weight pos_hp_l_d : Word x Pos x Word x Dep x Int -> Double;
factor :
  for Int h, Int m, Word h_word, Pos h_pos, Word m_word, Dep label
  if word(h,h_word) & pos(h,h_pos) & word(m,m_word)
  add [dep(h,m,label)] * pos_hp_l_d(h_word,h_pos,m_word,label,bins(1,2,3,4,5,10,h-m));


//h_word, m_word (and derivations)
weight word : Word x Word -> Double;
factor :
  for Int h, Int m, Word h_word, Word m_word
  if word(h,h_word) & word(m,m_word)
  add [link(h,m)] * word(h_word,m_word);

weight word_l : Word x Word x Dep-> Double;
factor :
  for Int h, Int m, Word h_word, Word m_word, Dep label
  if word(h,h_word) & word(m,m_word)
  add [dep(h,m,label)] * word_l(h_word,m_word,label);

weight word_d : Word x Word x Int -> Double;
factor :
  for Int h, Int m, Word h_word, Word m_word
  if word(h,h_word) & word(m,m_word)
  add [link(h,m)] * word_d(h_word,m_word, bins(1,2,3,4,5,10,h-m));

weight word_l_d : Word x Word x Dep x Int -> Double;
factor :
  for Int h, Int m, Word h_word, Word m_word, Dep label
  if word(h,h_word) & word(m,m_word)
  add [dep(h,m,label)] * word_l_d(h_word,m_word,label, bins(1,2,3,4,5,10,h-m));


//h_word, h_pos, m_word, m_pos (and derivations)
weight all: Word x Pos x Word x Pos -> Double;
factor :
  for Int h, Int m, Word h_word, Pos h_pos, Word m_word, Pos m_pos
  if word(h,h_word) & word(m,m_word) & pos(h,h_pos) & pos(m,m_pos)
  add [link(h,m)] * all(h_word,h_pos,m_word,m_pos);

weight all_l: Word x Pos x Word x Pos x Dep -> Double;
factor :
  for Int h, Int m, Word h_word, Pos h_pos, Word m_word, Pos m_pos, Dep label
  if word(h,h_word) & word(m,m_word) & pos(h,h_pos) & pos(m,m_pos)
  add [dep(h,m,label)] * all_l(h_word,h_pos,m_word,m_pos,label);

weight all_d: Word x Pos x Word x Pos x Int -> Double;
factor :
  for Int h, Int m, Word h_word, Pos h_pos, Word m_word, Pos m_pos
  if word(h,h_word) & word(m,m_word) & pos(h,h_pos) & pos(m,m_pos)
  add [link(h,m)] * all_d(h_word,h_pos,m_word,m_pos, bins(1,2,3,4,5,10,h-m));

weight all_l_d: Word x Pos x Word x Pos x Dep x Int -> Double;
factor :
  for Int h, Int m, Word h_word, Pos h_pos, Word m_word, Pos m_pos, Dep label
  if word(h,h_word) & word(m,m_word) & pos(h,h_pos) & pos(m,m_pos)
  add [dep(h,m,label)] * all_l_d(h_word,h_pos,m_word,m_pos,label,bins(1,2,3,4,5,10,h-m));


