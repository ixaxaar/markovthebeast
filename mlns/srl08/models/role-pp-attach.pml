// features that relate

/*
weight w_role_attach: MDependency x MDependency x Role -> Double;
factor: for Int p, Int a, Role r, Int m, MDependency dpa, MDependency dam if mst_dep(p,a,dpa) & mst_dep(a,m,dam) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_attach(dpa,dam,r);

weight w_role_attach_lp: MDependency x MDependency x Lemma x Role -> Double;
factor: for Int p, Int a, Role r, Int m, MDependency dpa, MDependency dam, Lemma lp if mst_dep(p,a,dpa) & mst_dep(a,m,dam) & lemma(p,lp) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_attach_lp(dpa,dam,lp,r);

weight w_role_attach_la: MDependency x MDependency x Lemma x Role -> Double;
factor: for Int p, Int a, Role r, Int m, MDependency dpa, MDependency dam, Lemma la if mst_dep(p,a,dpa) & mst_dep(a,m,dam) & lemma(a,la) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_attach_la(dpa,dam,la,r);

weight w_role_attach_pa: MDependency x MDependency x Ppos x Role -> Double;
factor: for Int p, Int a, Role r, Int m, MDependency dpa, MDependency dam, Ppos pa if mst_dep(p,a,dpa) & mst_dep(a,m,dam) & ppos(a,pa) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_attach_pa(dpa,dam,pa,r);

weight w_role_attach_wa: MDependency x MDependency x WNet x Role -> Double;
factor: for Int p, Int a, Role r, Int m, MDependency dpa, MDependency dam, WNet wa if mst_dep(p,a,dpa) & mst_dep(a,m,dam) & wnet(a,wa) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_attach_wa(dpa,dam,wa,r);
*/
/*
weight w_role_pp_attach_wmst_pm: Cpos x WNet x Role -> Double;
factor: for Role r, Int p, Int a, Int m, WNet wm, Cpos pm, MPath path if cpos(m,pm) & ppos(a,"IN") & mst_dep(a,m,_) & wnet(m,wm) &
  mst_path(p,a,path) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_pp_attach_wmst_pm(pm,wm,r);

weight w_role_pp_attach1: Ppos x Ppos x Role -> Double;
factor: for Role r, Int p, Int a, Int m, Ppos pm, Ppos pp if ppos(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & ppos(m,pm) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_pp_attach1(pp,pm,r);

weight w_role_pp_attach2: Ppos x WNet x Role -> Double;
factor: for Role r, Int p, Int a, Int m, WNet pm, Ppos pp if ppos(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & wnet(m,pm) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_pp_attach2(pp,pm,r);

weight w_role_pp_attach3: WNet x Ppos x Role -> Double;
factor: for Role r, Int p, Int a, Int m, WNet pp, Ppos pm if wnet(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & ppos(m,pm)&
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_pp_attach3(pp,pm,r);

weight w_role_pp_attach4: Ppos x Lemma x Role -> Double;
factor: for Role r, Int p, Int a, Int m, Lemma pm, Ppos pp if ppos(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & lemma(m,pm)&
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_pp_attach4(pp,pm,r);

weight w_role_pp_attach5: Lemma x Ppos x Role -> Double;
factor: for Role r, Int p, Int a, Int m, Ppos pm, Lemma pp if lemma(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & ppos(m,pm)&
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_pp_attach5(pp,pm,r);
*/
