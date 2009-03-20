//bias
weight w_role_bias: Role -> Double;
factor: for Int a, Int p, Role r if
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_bias(r);

set collector.all.w_role_bias = true;

//bias and pos tag of predicate
weight w_role_bias_ppos: Role x Ppos -> Double;
factor: for Int a, Int p, Role r, Ppos cp if ppos(p,cp) & 
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_bias_ppos(r,cp);

//set collector.all.w_role_bias_ppos = true;
weight w_role_bias_ppos_undef: Double;
factor: for Int a, Int p, Role r, Ppos cp if ppos(p,cp) & allargs(r) & undefined(w_role_bias_ppos(r,cp)) & 
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_bias_ppos_undef;



//lemma of argument
weight w_lemma_a: Lemma x Role -> Double;
factor: for Int a, Int p, Lemma l, Role r if lemma(a,l) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_lemma_a(l,r);
/*
//lemma of argument and cpos tag of predicate
weight w_lemma_a_cpos: Lemma x Role x Cpos-> Double;
factor: for Int a, Int p, Lemma l, Role r, Cpos cp if lemma(a,l) & cpos(p,cp) & 
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_lemma_a_cpos(l,r,cp);

*/
//lemma of argument and predicate
weight w_lemma_pa: Lemma x Lemma x Role -> Double;
factor: for Int a, Int p, Lemma la, Lemma lp, Role r if lemma(a,la) & lemma(p,lp) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_lemma_pa(lp,la,r);

//lemma of argument and predicate with distance
weight w_lemma_pa_dist: Lemma x Lemma x Role x Int -> Double;
factor: for Int a, Int p, Lemma la, Lemma lp, Role r if lemma(a,la) & lemma(p,lp) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_lemma_pa_dist(lp,la,r,bins(0,1,2,3,4,5,10,a-p));


//set collector.cutoff.w_lemma_pa = 0;

//lemma of argument with distance
weight w_lemma_a_dist: Lemma x Int x Role -> Double;
factor: for Int a, Int p, Lemma l, Role r if lemma(a,l) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_lemma_a_dist(l,bins(0,1,2,3,4,5,10,a-p),r);

//lemma of argument with distance & voice
weight w_lemma_a_dist_voice: Lemma x Int x Voice x Role -> Double;
factor: for Int a, Int p, Lemma l, Role r, Voice v if lemma(a,l) & voice(p,v) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_lemma_a_dist_voice(l,bins(0,1,2,3,4,5,10,a-p),v,r);

//lemma of predicate with distance & voice
//weight w_lemma_p_dist_voice: Lemma x Int x Voice x Role -> Double;
//factor: for Int a, Int p, Lemma l, Role r, Voice v if lemma(p,l) & voice(p,v) &
//  possiblePredicate(p) & possibleArgument(a) &
//  palmer(p,a)
//  add[role(p,a,r)] * w_lemma_p_dist_voice(l,bins(0,1,2,3,4,5,10,a-p),v,r);

//dep path frame between predicate and argument
weight w_role_dep_path_frame: MPathFrame x Role -> Double;
factor: for Int a, Int p, Role r, MPathFrame pathFrame if mst_path_frame(p,a,pathFrame) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame(pathFrame,r);

//unlabeled dep path frame between predicate and argument
weight w_role_dep_path_frame_unlabeled: MPathFrameUnlabeled x Role -> Double;
factor: for Int a, Int p, Role r, MPathFrameUnlabeled pathFrame if mst_path_frame_unlabeled(p,a,pathFrame) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_unlabeled(pathFrame,r);

//unlabeled dep path frame between predicate and argument & predicate lemma
weight w_role_dep_path_frame_unlabeled_lemma_p: MPathFrameUnlabeled x Role x Lemma -> Double;
factor: for Int a, Int p, Role r, Lemma lp, MPathFrameUnlabeled pathFrame
  if mst_path_frame_unlabeled(p,a,pathFrame) & lemma(p,lp) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_unlabeled_lemma_p(pathFrame,r,lp);

//unlabeled dep path frame between predicate and argument & predicate lemma
weight w_role_dep_path_frame_unlabeled_lemma_a: MPathFrameUnlabeled x Role x Lemma -> Double;
factor: for Int a, Int p, Role r, Lemma la, MPathFrameUnlabeled pathFrame
  if mst_path_frame_unlabeled(p,a,pathFrame) & lemma(a,la) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_unlabeled_lemma_a(pathFrame,r,la);


//dep path frame between predicate and argument combined with predicate lemma
weight w_role_dep_path_frame_lemma_p: MPathFrame x Lemma x Role -> Double;
factor: for Int a, Int p, Role r, MPathFrame pathFrame, Lemma l if mst_path_frame(p,a,pathFrame) & lemma(p,l) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_lemma_p(pathFrame,l,r);

//dep path frame between predicate and argument combined with predicate and argument lemma
weight w_role_dep_path_frame_lemma_pa: MPathFrame x Lemma x Lemma x Role -> Double;
factor: for Int a, Int p, Role r, MPathFrame pathFrame, Lemma lp, Lemma la
  if mst_path_frame(p,a,pathFrame) & lemma(p,lp) & lemma(a,la) & 
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_lemma_pa(pathFrame,lp,la,r);

/*
//dep path frame between predicate and argument combined with predicate and argument postfix
weight w_role_dep_path_frame_postfix_pa: MPathFrame x Postfix x Postfix x Role -> Double;
factor: for Int a, Int p, Role r, MPathFrame pathFrame, Postfix lp, Postfix la
  if mst_path_frame(p,a,pathFrame) & postfix(p,lp) & postfix(a,la) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_postfix_pa(pathFrame,lp,la,r);
*/

//dep path frame between predicate and argument combined with argument lemma
weight w_role_dep_path_frame_lemma_a: MPathFrame x Lemma x Role -> Double;
factor: for Int a, Int p, Role r, MPathFrame pathFrame, Lemma l if mst_path_frame(p,a,pathFrame) & lemma(a,l) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_lemma_a(pathFrame,l,r);

//dep path frame between predicate and argument combined with predicate lemma and voice
weight w_role_dep_path_frame_lemma_p_voice: MPathFrame x Lemma x Role x Voice -> Double;
factor: for Int a, Int p, Role r, MPathFrame pathFrame, Lemma l, Voice v if mst_path_frame(p,a,pathFrame) &
  lemma(p,l) & voice(p,v) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_lemma_p_voice(pathFrame,l,r,v);

//dep path frame between predicate and argument combined with argument lemma and predicate voice
weight w_role_dep_path_frame_lemma_a_voice: MPathFrame x Lemma x Role x Voice -> Double;
factor: for Int a, Int p, Role r, MPathFrame pathFrame, Lemma l, Voice v if mst_path_frame(p,a,pathFrame) &
  lemma(a,l) & voice(p,v) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_lemma_a_voice(pathFrame,l,r,v);

//dep path frame between predicate and argument and voice
weight w_role_dep_path_frame_voice: MPathFrame x Role x Voice -> Double;
factor: for Int a, Int p, Role r, MPathFrame pathFrame, Voice v if mst_path_frame(p,a,pathFrame) & voice(p,v) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_voice(pathFrame,r,v);

//predicate pos and argument pos
weight l_role_pp_pa: Ppos x Ppos x Role -> Double;
factor: for Int p, Int a, Ppos p_p, Ppos p_a ,Role r if ppos(p,p_p) & ppos(a,p_a) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * l_role_pp_pa(p_p,p_a,r);

//path_frame_distance
weight w_role_dep_path_frame_dist: Int x Role -> Double;
factor: for Int a, Int p, Role r, Int d if path_frame_distance(p,a,d) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_dist(bins(0,1,2,3,4,5,10,a-p),r);


//path_frame_distance and voice
weight w_role_dep_path_frame_dist_voice: Int x Role x Voice -> Double;
factor: for Int a, Int p, Role r, Int d, Voice v if path_frame_distance(p,a,d) & voice(p,v) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_dist_voice(bins(0,1,2,3,4,5,10,a-p),r,v);

//path_frame_distance and predicate lemma
weight w_role_dep_path_frame_dist_lp: Int x Role x Lemma -> Double;
factor: for Int a, Int p, Role r, Int d, Lemma l if path_frame_distance(p,a,d) & lemma(p,l) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_dist_lp(bins(0,1,2,3,4,5,10,a-p),r,l);

//path_frame_distance and predicate pos
weight w_role_dep_path_frame_dist_pp: Int x Role x Ppos -> Double;
factor: for Int a, Int p, Role r, Int d, Ppos l if path_frame_distance(p,a,d) & ppos(p,l) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_dist_pp(bins(0,1,2,3,4,5,10,a-p),r,l);



//path_frame_distance and argument lemma
weight w_role_dep_path_frame_dist_la: Int x Role x Lemma -> Double;
factor: for Int a, Int p, Role r, Int d, Lemma l if path_frame_distance(p,a,d) & lemma(a,l) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add[role(p,a,r)] * w_role_dep_path_frame_dist_la(bins(0,1,2,3,4,5,10,a-p),r,l);


//predicate pos and argument pos & distance
weight l_role_pp_pa_dist: Ppos x Ppos x Role x Int -> Double;
factor: for Int p, Int a, Ppos p_p, Ppos p_a ,Role r if ppos(p,p_p) & ppos(a,p_a) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * l_role_pp_pa_dist(p_p,p_a,r,bins(0,1,2,3,4,5,10,a-p));

/*
weight l_role_pp_pa_undef: Double;
factor: for Int p, Int a, Ppos p_p, Ppos p_a ,Role r if ppos(p,p_p) & ppos(a,p_a) & allargs(r) &
  undefined(l_role_pp_pa(p_p,p_a,r)) & 
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * l_role_pp_pa_undef;
*/

//set collector.all.l_role_pp_pa = true;  


//argument dep, predicate voice, distance
weight w_role_dep_a_voice_dist: MDependency x Voice x Int x Role -> Double;
factor: for Int p, Int a, MDependency d, Voice v, Role r if voice(p,v) & mst_dep(_,a,d) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_dep_a_voice_dist(d,v,bins(0,1,2,3,4,5,10,a-p),r);

//palmer prune
//weight w_role_palmer_role: Double;
//factor: for Int p, Int a, Role r if palmer(p,a) add [role(p,a,r)] * w_role_palmer_role;

//argument dep & distance
weight w_role_dep_a_dist: MDependency x Int x Role -> Double;
factor: for Int p, Int a, MDependency d, Role r if mst_dep(_,a,d) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_dep_a_dist(d,bins(0,1,2,3,4,5,10,a-p),r);

//argument pos & distance
weight w_role_ppos_a_dist: Ppos x Int x Role -> Double;
factor: for Int p, Int a, Ppos pa, Role r if ppos(a,pa) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_ppos_a_dist(pa,bins(0,1,2,3,4,5,10,a-p),r);

//argument dep & predicate dep & distance
weight w_role_dep_pa_dist: MDependency x MDependency x Int x Role -> Double;
factor: for Int p, Int a, MDependency da, MDependency dp, Role r if mst_dep(_,a,da) & mst_dep(_,p,dp) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_dep_pa_dist(dp,da,bins(0,1,2,3,4,5,10,a-p),r);

//argument dep & predicate dep
weight w_role_dep_pa: MDependency x MDependency x Role -> Double;
factor: for Int p, Int a, MDependency da, MDependency dp, Role r if mst_dep(_,a,da) & mst_dep(_,p,dp) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_dep_pa(dp,da,r);

//CPOS window feature
weight w_role_cpos1: Cpos x Cpos x Cpos x Cpos x Int x Role -> Double;
factor: for Int p, Int a, Cpos cp1, Cpos cp2, Cpos cp3, Cpos cp4, Role r
  if cpos(p,cp1) & cpos(p-1,cp2) & cpos(a,cp3) & cpos(a+1,cp4) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_cpos1(cp1,cp2,cp3,cp4,bins(0,1,2,3,4,5,10,a-p),r);

weight w_role_cpos2: Cpos x Cpos x Cpos x Cpos x Int x Role -> Double;
factor: for Int p, Int a, Cpos cp1, Cpos cp2, Cpos cp3, Cpos cp4, Role r
  if cpos(p,cp1) & cpos(p+1,cp2) & cpos(a,cp3) & cpos(a-1,cp4) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_cpos2(cp1,cp2,cp3,cp4,bins(0,1,2,3,4,5,10,a-p),r);

weight w_role_cpos3: Cpos x Cpos x Cpos x Cpos x Int x Role -> Double;
factor: for Int p, Int a, Cpos cp1, Cpos cp2, Cpos cp3, Cpos cp4, Role r
  if cpos(p,cp1) & cpos(p-1,cp2) & cpos(a,cp3) & cpos(a-1,cp4) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_cpos3(cp1,cp2,cp3,cp4,bins(0,1,2,3,4,5,10,a-p),r);

weight w_role_cpos4: Cpos x Cpos x Cpos x Cpos x Int x Role -> Double;
factor: for Int p, Int a, Cpos cp1, Cpos cp2, Cpos cp3, Cpos cp4, Role r
  if cpos(p,cp1) & cpos(p+1,cp2) & cpos(a,cp3) & cpos(a+1,cp4) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_cpos4(cp1,cp2,cp3,cp4,bins(0,1,2,3,4,5,10,a-p),r);

//this weight function does not seem to be instantiated properly: instantiated args look odd.
//same token features: lemma
//weight w_role_same_lemma: Lemma x Role -> Double;
//factor: for Int p, Lemma l, Role r if lemma(p,l) add [role(p,p,r)] * w_role_same_lemma(l,r);


/*
//same token features: ppos
weight w_role_same_ppos: Ppos x Role -> Double;
factor: for Int p, Ppos l, Role r if ppos(p,l) add [role(p,p,r)] * w_role_same_ppos(l,r);

//same token features: wnet
//weight w_role_same_wnet: WNet x Role -> Double;
//factor: for Int p, WNet l, Role r if wnet(p,l) add [role(p,p,r)] * w_role_same_wnet(l,r);

*/
//predicate pos & argument pos
//weight l_role_pp_pa: Ppos x Ppos x Role -> Double;
//factor: for Int p, Int a, Ppos p_p, Ppos p_a ,Role r if ppos(p,p_p) & ppos(a,p_a) &
//  palmer(p,a)
//  add [role(p,a,r)] * l_role_pp_pa(p_p,p_a,r);

//pp attachment features
weight w_role_pp_attach1: Ppos x Ppos x Role -> Double;
factor: for Role r, Int p, Int a, Int m, Ppos pm, Ppos pp if ppos(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & ppos(m,pm) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_pp_attach1(pp,pm,r);

//weight w_role_pp_attach2: Ppos x WNet x Role -> Double;
//factor: for Role r, Int p, Int a, Int m, WNet pm, Ppos pp if ppos(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & wnet(m,pm) &
//  possiblePredicate(p) & possibleArgument(a) &
//  palmer(p,a)
//  add [role(p,a,r)] * w_role_pp_attach2(pp,pm,r);

//weight w_role_pp_attach3: WNet x Ppos x Role -> Double;
//factor: for Role r, Int p, Int a, Int m, WNet pp, Ppos pm if wnet(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & ppos(m,pm)&
//  possiblePredicate(p) & possibleArgument(a) &
//  palmer(p,a)
//  add [role(p,a,r)] * w_role_pp_attach3(pp,pm,r);

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

//wnet based features
//predicate pos and argument wnet
//weight l_role_hasLabel_pp_wna: Ppos x WNet x Role -> Double;
//factor: for Int p, Int a, Ppos p_p, WNet l_a, Role r if ppos(p,p_p) & wnet(a,l_a) &
//  possiblePredicate(p) & possibleArgument(a) &
//  palmer(p,a)
//  add [role(p,a,r)] * l_role_hasLabel_pp_wna(p_p,l_a,r);

//set collector.all.l_role_hasLabel_pp_wna = true;

//argument wnet
//weight l_role_hasLabel_wna: WNet x Role -> Double;
//factor: for Int p, Int a, WNet l_a, Role r if wnet(a,l_a) &
//  possiblePredicate(p) & possibleArgument(a) &
//  palmer(p,a)
//  add [role(p,a,r)] * l_role_hasLabel_wna(l_a,r);

//set collector.all.l_role_hasLabel_wna = true;

//argument & predicate wnet
//weight l_role_hasLabel_wnpa: WNet x WNet x Role -> Double;
//factor: for Int p, Int a, WNet l_a, WNet l_p, Role r if wnet(a,l_a) & wnet(p,l_p) &
//  possiblePredicate(p) & possibleArgument(a) &
//  palmer(p,a)
//  add [role(p,a,r)] * l_role_hasLabel_wnpa(l_p,l_a,r);

//set collector.all.l_role_hasLabel_wnpa = true;

/*
weight l_role_hasLabel_wnpa_dist: WNet x WNet x Role x Int -> Double;
factor: for Int p, Int a, WNet l_a, WNet l_p, Role r if wnet(a,l_a) & wnet(p,l_p) &
  possiblePredicate(p) & possibleArgument(a) &
  palmer(p,a)
  add [role(p,a,r)] * l_role_hasLabel_wnpa_dist(l_p,l_a,r,bins(0,1,2,3,4,5,10,a-p));
*/
//set collector.all.l_role_hasLabel_wnpa_dist = true;

/*
//postfix based
weight w_role_postfix_pos_dist: Cpos x Cpos x Postfix x Postfix x Role x Int -> Double;
factor: for Int p, Int a, Postfix ppf, Postfix apf, Cpos acp, Cpos pcp, Role r
  if postfix(a,apf) & postfix(p,ppf) & cpos(a,acp) & cpos(p,pcp) &
  palmer(p,a)
  add [role(p,a,r)] * w_role_postfix_pos_dist(pcp,acp,ppf,apf,r, bins(0,1,2,3,4,5,10,a-p));

set collector.cutoff.w_role_postfix_pos_dist = 2;
*/


//postfix based, only nouns
//weight w_role_postfix_noun_dist: Postfix x Postfix x Role x Int -> Double;
//factor: for Int p, Int a, Postfix ppf, Postfix apf, Role r
//  if postfix(a,apf) & postfix(p,ppf) & cpos(a,"N") & cpos(p,"N") &
//  palmer(p,a)
//  add [role(p,a,r)] * w_role_postfix_noun_dist(ppf,apf,r, bins(0,1,2,3,4,5,10,a-p));

//set collector.cutoff.w_role_postfix_noun_dist = 2;


