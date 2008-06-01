/* these are (roughly) the "bigram features" mentioned in McDonald et al.
   Each comes in two flavors: without or with label (called a_l). And each of these can come in two more
   flavors: without or with a (binned) distance (called a_d)

   We call head attributes h_x and modifier attributes m_x */

//h_cpos, m_cpos (and derivations)
//min(l,h,m) & max(r,h,m)
weight cpos : Cpos x Cpos -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos)
  add [link(h,m)] * cpos(h_cpos,m_cpos);

//weight cpos_l : Cpos x Cpos x Dep-> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Dep label
//  if cpos(h,h_cpos) & cpos(m,m_cpos)
//  add [dep(h,m,label)] * cpos_l(h_cpos,m_cpos,label);

weight cpos_d : Cpos x Cpos x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos
  if cpos(h,h_cpos) & cpos(m,m_cpos)
  add [link(h,m)] * cpos_d(h_cpos,m_cpos, bins(1,2,3,4,5,10,h-m));

//weight cpos_l_d : Cpos x Cpos x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Dep label
//  if cpos(h,h_cpos) & cpos(m,m_cpos)
//  add [dep(h,m,label)] * cpos_l_d(h_cpos,m_cpos,label, bins(1,2,3,4,5,10,h-m));

//h_cpos, m_cpos, h_word (and derivations)
weight cpos_hw : Cpos x Cpos x Word -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Word h_word
  if cpos(h,h_cpos) & cpos(m,m_cpos) & word(h,h_word)
  add [link(h,m)] * cpos_hw(h_cpos,m_cpos, h_word);

//weight cpos_hw_l : Cpos x Cpos x Word x Dep-> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Dep label, Word h_word
//  if cpos(h,h_cpos) & cpos(m,m_cpos) & word(h,h_word)
//  add [dep(h,m,label)] * cpos_hw_l(h_cpos,m_cpos,h_word,label);

weight cpos_hw_d : Cpos x Cpos x Word x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Word h_word
  if cpos(h,h_cpos) & cpos(m,m_cpos) & word(h,h_word)
  add [link(h,m)] * cpos_hw_d(h_cpos,m_cpos, h_word, bins(1,2,3,4,5,10,h-m));

//weight cpos_hw_l_d : Cpos x Cpos x Word x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Dep label, Word h_word
//  if cpos(h,h_cpos) & cpos(m,m_cpos) & word(h,h_word)
//  add [dep(h,m,label)] * cpos_hw_l_d(h_cpos,m_cpos,h_word,label,bins(1,2,3,4,5,10,h-m));

//h_cpos, m_cpos, m_word (and derivations)
weight cpos_mw : Cpos x Cpos x Word -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Word m_word
  if cpos(h,h_cpos) & cpos(m,m_cpos) & word(m,m_word)
  add [link(h,m)] * cpos_mw(h_cpos,m_cpos, m_word);

//weight cpos_mw_l : Cpos x Cpos x Word x Dep-> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Dep label, Word m_word
//  if cpos(h,h_cpos) & cpos(m,m_cpos) & word(m,m_word)
//  add [dep(h,m,label)] * cpos_mw_l(h_cpos,m_cpos,m_word,label);

weight cpos_mw_d : Cpos x Cpos x Word x Int -> Double;
factor :
  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Word m_word
  if cpos(h,h_cpos) & cpos(m,m_cpos) & word(m,m_word)
  add [link(h,m)] * cpos_mw_d(h_cpos,m_cpos, m_word, bins(1,2,3,4,5,10,h-m));

//weight cpos_mw_l_d : Cpos x Cpos x Word x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Cpos h_cpos, Cpos m_cpos, Dep label, Word m_word
//  if cpos(h,h_cpos) & cpos(m,m_cpos) & word(m,m_word)
//  add [dep(h,m,label)] * cpos_mw_l_d(h_cpos,m_cpos,m_word,label,bins(1,2,3,4,5,10,h-m));

//h_word, m_cpos, m_word (and derivations)
weight cpos_mp : Word x Cpos x Word -> Double;
factor :
  for Int h, Int m, Word h_word, Cpos m_cpos, Word m_word
  if word(h,h_word) & cpos(m,m_cpos) & word(m,m_word)
  add [link(h,m)] * cpos_mp(h_word,m_cpos,m_word);

//weight cpos_mp_l : Word x Cpos x Word x Dep -> Double;
//factor :
//  for Int h, Int m, Word h_word, Cpos m_cpos, Word m_word, Dep label
//  if word(h,h_word) & cpos(m,m_cpos) & word(m,m_word)
//  add [dep(h,m,label)] * cpos_mp_l(h_word,m_cpos,m_word,label);

weight cpos_mp_d : Word x Cpos x Word x Int -> Double;
factor :
  for Int h, Int m, Word h_word, Cpos m_cpos, Word m_word
  if word(h,h_word) & cpos(m,m_cpos) & word(m,m_word)
  add [link(h,m)] * cpos_mp_d(h_word,m_cpos,m_word, bins(1,2,3,4,5,10,h-m));

//weight cpos_mp_l_d : Word x Cpos x Word x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Word h_word, Cpos m_cpos, Word m_word, Dep label
//  if word(h,h_word) & cpos(m,m_cpos) & word(m,m_word)
//  add [dep(h,m,label)] * cpos_mp_l_d(h_word,m_cpos,m_word,label,bins(1,2,3,4,5,10,h-m));

//h_word, h_cpos, m_word (and derivations)
weight cpos_hp : Word x Cpos x Word -> Double;
factor :
  for Int h, Int m, Word h_word, Cpos h_cpos, Word m_word
  if word(h,h_word) & cpos(h,h_cpos) & word(m,m_word)
  add [link(h,m)] * cpos_hp(h_word,h_cpos,m_word);

//weight cpos_hp_l : Word x Cpos x Word x Dep -> Double;
//factor :
//  for Int h, Int m, Word h_word, Cpos h_cpos, Word m_word, Dep label
//  if word(h,h_word) & cpos(h,h_cpos) & word(m,m_word)
//  add [dep(h,m,label)] * cpos_hp_l(h_word,h_cpos,m_word,label);

weight cpos_hp_d : Word x Cpos x Word x Int -> Double;
factor :
  for Int h, Int m, Word h_word, Cpos h_cpos, Word m_word
  if word(h,h_word) & cpos(h,h_cpos) & word(m,m_word)
  add [link(h,m)] * cpos_hp_d(h_word,h_cpos,m_word, bins(1,2,3,4,5,10,h-m));

//weight cpos_hp_l_d : Word x Cpos x Word x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Word h_word, Cpos h_cpos, Word m_word, Dep label
//  if word(h,h_word) & cpos(h,h_cpos) & word(m,m_word)
//  add [dep(h,m,label)] * cpos_hp_l_d(h_word,h_cpos,m_word,label,bins(1,2,3,4,5,10,h-m));


//h_word, m_word (and derivations)
weight word : Word x Word -> Double;
factor :
  for Int h, Int m, Word h_word, Word m_word
  if word(h,h_word) & word(m,m_word)
  add [link(h,m)] * word(h_word,m_word);

//weight word_l : Word x Word x Dep-> Double;
//factor :
//  for Int h, Int m, Word h_word, Word m_word, Dep label
//  if word(h,h_word) & word(m,m_word)
//  add [dep(h,m,label)] * word_l(h_word,m_word,label);

weight word_d : Word x Word x Int -> Double;
factor :
  for Int h, Int m, Word h_word, Word m_word
  if word(h,h_word) & word(m,m_word)
  add [link(h,m)] * word_d(h_word,m_word, bins(1,2,3,4,5,10,h-m));

//weight word_l_d : Word x Word x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Word h_word, Word m_word, Dep label
//  if word(h,h_word) & word(m,m_word)
//  add [dep(h,m,label)] * word_l_d(h_word,m_word,label, bins(1,2,3,4,5,10,h-m));


//h_word, h_cpos, m_word, m_cpos (and derivations)
weight all: Word x Cpos x Word x Cpos -> Double;
factor :
  for Int h, Int m, Word h_word, Cpos h_cpos, Word m_word, Cpos m_cpos
  if word(h,h_word) & word(m,m_word) & cpos(h,h_cpos) & cpos(m,m_cpos)
  add [link(h,m)] * all(h_word,h_cpos,m_word,m_cpos);

//weight all_l: Word x Cpos x Word x Cpos x Dep -> Double;
//factor :
//  for Int h, Int m, Word h_word, Cpos h_cpos, Word m_word, Cpos m_cpos, Dep label
//  if word(h,h_word) & word(m,m_word) & cpos(h,h_cpos) & cpos(m,m_cpos)
//  add [dep(h,m,label)] * all_l(h_word,h_cpos,m_word,m_cpos,label);

weight all_d: Word x Cpos x Word x Cpos x Int -> Double;
factor :
  for Int h, Int m, Word h_word, Cpos h_cpos, Word m_word, Cpos m_cpos
  if word(h,h_word) & word(m,m_word) & cpos(h,h_cpos) & cpos(m,m_cpos)
  add [link(h,m)] * all_d(h_word,h_cpos,m_word,m_cpos, bins(1,2,3,4,5,10,h-m));

//weight all_l_d: Word x Cpos x Word x Cpos x Dep x Int -> Double;
//factor :
//  for Int h, Int m, Word h_word, Cpos h_cpos, Word m_word, Cpos m_cpos, Dep label
//  if word(h,h_word) & word(m,m_word) & cpos(h,h_cpos) & cpos(m,m_cpos)
//  add [dep(h,m,label)] * all_l_d(h_word,h_cpos,m_word,m_cpos,label,bins(1,2,3,4,5,10,h-m));


