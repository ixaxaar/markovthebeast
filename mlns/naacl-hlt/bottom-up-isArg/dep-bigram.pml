//distance
/*
weight w_link_dist: Int -> Double;
factor:
  for Int h, Int m
  if word(h,_) & word(m,_)
  add [link(h,m)] * w_link_dist(bins(0,1,2,3,4,5,10,h-m));
*/

//word_head, word_modifier
weight w_link_wh_wm: Word x Word -> Double;
factor:
  for Int h, Int m, Word w_link_h, Word w_link_m
  if word(h,w_link_h) & word(m,w_link_m)
  add [link(h,m)] * w_link_wh_wm(w_link_h,w_link_m);

//word_head, word_modifier, dep, distance
weight w_link_wh_wmst_d_dist: Word x Word  x Int -> Double;
factor:
  for Int h, Int m, Word w_link_h, Word w_link_m
  if word(h,w_link_h) & word(m,w_link_m)
  add [link(h,m)] * w_link_wh_wmst_d_dist(w_link_h,w_link_m,bins(0,1,2,3,4,5,10,h-m));

//pos_head, pos_modifier
weight w_link_ph_pm: Ppos x Ppos -> Double;
factor:
  for Int h, Int m, Ppos p_h, Ppos p_m
  if ppos(h,p_h) & ppos(m,p_m)
  add [link(h,m)] * w_link_ph_pm(p_h,p_m);


//pos_head, pos_modifier, distance
weight w_link_ph_pmst_d_dist: Ppos x Ppos  x Int -> Double;
factor:
  for Int h, Int m, Ppos p_h, Ppos p_m
  if ppos(h,p_h) & ppos(m,p_m)
  add [link(h,m)] * w_link_ph_pmst_d_dist(p_h,p_m,bins(0,1,2,3,4,5,10,h-m));

/* with label */
//word_head, word_modifier, dep
weight w_dep_wh_wmst_d: Word x Word x Dependency -> Double;
factor:
  for Int h, Int m, Word w_h, Word w_m, Dependency d
  if word(h,w_h) & word(m,w_m)
  add [dep(h,m,d)] * w_dep_wh_wmst_d(w_h,w_m,d);

//pos_head, pos_modifier, dep
weight w_dep_ph_pmst_d: Ppos x Ppos x Dependency -> Double;
factor:
  for Int h, Int m, Ppos p_h, Ppos p_m, Dependency d
  if ppos(h,p_h) & ppos(m,p_m)
  add [dep(h,m,d)] * w_dep_ph_pmst_d(p_h,p_m,d);

//pos_head, pos_modifier, dep, distance
weight w_dep_ph_pmst_d_dist: Ppos x Ppos x Dependency x Int -> Double;
factor:
  for Int h, Int m, Ppos p_h, Ppos p_m, Dependency d
  if ppos(h,p_h) & ppos(m,p_m)
  add [dep(h,m,d)] * w_dep_ph_pmst_d_dist(p_h,p_m,d,bins(0,1,2,3,4,5,10,h-m));

/*
//distance
weight w_dist: Int -> Double;
factor:
  for Int h, Int m, Dependency d
  if word(h,_) & word(m,_)
  add [dep(h,m,d)] * w_dist(bins(0,1,2,3,4,5,10,h-m));

//word_head, word_modifier
weight w_dep_wh_wm: Word x Word -> Double;
factor:
  for Int h, Int m, Word w_h, Word w_m, Dependency d
  if word(h,w_h) & word(m,w_m)
  add [dep(h,m,d)] * w_dep_wh_wm(w_h,w_m);

//word_head, word_modifier, dep
weight w_dep_wh_wmst_d: Word x Word x Dependency -> Double;
factor:
  for Int h, Int m, Word w_h, Word w_m, Dependency d
  if word(h,w_h) & word(m,w_m)
  add [dep(h,m,d)] * w_dep_wh_wmst_d(w_h,w_m,d);

//word_head, word_modifier, dep, distance
weight w_dep_wh_wmst_d_dist: Word x Word x Dependency x Int -> Double;
factor:
  for Int h, Int m, Word w_h, Word w_m, Dependency d
  if word(h,w_h) & word(m,w_m)
  add [dep(h,m,d)] * w_dep_wh_wmst_d_dist(w_h,w_m,d,bins(0,1,2,3,4,5,10,h-m));

//pos_head, pos_modifier
weight w_dep_ph_pm: Ppos x Ppos -> Double;
factor:
  for Int h, Int m, Ppos p_h, Ppos p_m, Dependency d
  if ppos(h,p_h) & ppos(m,p_m)
  add [dep(h,m,d)] * w_dep_ph_pm(p_h,p_m);     

//pos_head, pos_modifier, dep
weight w_dep_ph_pmst_d: Ppos x Ppos x Dependency -> Double;
factor:
  for Int h, Int m, Ppos p_h, Ppos p_m, Dependency d
  if ppos(h,p_h) & ppos(m,p_m)
  add [dep(h,m,d)] * w_dep_ph_pmst_d(p_h,p_m,d);

//pos_head, pos_modifier, dep, distance
weight w_dep_ph_pmst_d_dist: Ppos x Ppos x Dependency x Int -> Double;
factor:
  for Int h, Int m, Ppos p_h, Ppos p_m, Dependency d
  if ppos(h,p_h) & ppos(m,p_m)
  add [dep(h,m,d)] * w_dep_ph_pmst_d_dist(p_h,p_m,d,bins(0,1,2,3,4,5,10,h-m));
*/