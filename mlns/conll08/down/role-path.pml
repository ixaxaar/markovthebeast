//dep path between predicate and argument combined with argument pos
weight w_role_path_pa: MPath x Ppos x Role -> Double;
factor: for Int a, Int p, Role r, MPath path, Ppos pa if m_path(p,a,path) & ppos(a,pa) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_path_pa(path, pa, r);

//dep path between predicate and argument combined with argument cpos and predicate lemma
weight w_role_path_pa_lp: MPath x Cpos x Slemma x Role -> Double;
factor: for Int a, Int p, Role r, MPath path, Cpos pa, Slemma lp if m_path(p,a,path) & cpos(a,pa) & slemma(p,lp) & 
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_path_pa_lp(path, pa, lp, r);

//dep path between predicate and argument combined with argument lemma
weight w_role_path_la: MPath x Slemma x Role -> Double;
factor: for Int a, Int p, Role r, MPath path, Slemma la if m_path(p,a,path) & slemma(a,la) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_path_la(path, la, r);

//dep path between predicate and argument combined with argument & predicate lemma
weight w_role_path_la_lp: MPath x Slemma x Slemma x Role -> Double;
factor: for Int a, Int p, Role r, MPath path, Slemma la, Slemma lp if m_path(p,a,path) & slemma(a,la) & slemma(p,lp) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_path_la_lp(path, la,lp, r);

//dep path between predicate and argument combined with argument wnet & predicate lemma
weight w_role_path_wa_la_lp: MPath x WNet x Slemma x Slemma x Role -> Double;
factor: for Int a, Int p, Role r, MPath path, Slemma la, Slemma lp, WNet wa if
  m_path(p,a,path) & slemma(a,la) & slemma(p,lp) & wnet(a,wa) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_path_wa_la_lp(path, wa, la,lp, r);
