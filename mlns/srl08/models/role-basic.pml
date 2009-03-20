// Local rules

//bias
//weight l_role_bias: Double;
//factor: for Int p, Int a ,Role r if lemma(p,_) & lemma(a,_) add [role(p,a,r)] * l_role_bias;

//distance
//weight l_role_dist: Int x Role -> Double-;
//factor: for Int p, Int a ,Role r if lemma(p,_) & lemma(a,_) add [role(p,a,r)] * l_role_dist(bins(0,1,2,3,4,5,10,a-p),r);

//predicate lemma and argument lemma
weight l_role_lp: Slemma x Role -> Double;
factor: for Int p, Int a, Slemma l_p, Slemma l_a ,Role r if slemma(p,l_p) & slemma(a,l_a)
  add [role(p,a,r)] * l_role_lp_la(l_p,r);

//predicate voice
weight w_voice_role: Voice x Role -> Double;
factor: for Int p, Int a, Voice v, Role r if word(a,_) & voice(p,v)
  add [role(p,a,r)] * w_voice_role(v,r);

//predicate voice
weight w_voice: Voice -> Double;
factor: for Int p, Int a, Voice v, Role r if word(a,_) & voice(p,v)
  add [role(p,a,r)] * w_voice(v);

weight l_role_lp_la: Slemma x Slemma x Role -> Double;
factor: for Int p, Int a, Slemma l_p, Slemma l_a ,Role r if slemma(p,l_p) & slemma(a,l_a)
  add [role(p,a,r)] * l_role_lp_la(l_p,l_a,r);

//predicate pos and argument pos
weight l_role_pp_pa: Ppos x Ppos x Role -> Double;
factor: for Int p, Int a, Ppos p_p, Ppos p_a ,Role r if ppos(p,p_p) & ppos(a,p_a)
  add [role(p,a,r)] * l_role_pp_pa(p_p,p_a,r);

weight l_role_pp_pa_voice: Ppos x Ppos x Voice x Role -> Double;
factor: for Int p, Int a, Ppos p_p, Ppos p_a ,Role r, Voice v if ppos(p,p_p) & ppos(a,p_a) & voice(p,v)
  add [role(p,a,r)] * l_role_pp_pa_voice(p_p,p_a,v,r);

//predicate pos and argument pos + distance
//weight l_role_pp_pa_dist: Ppos x Ppos x Int x Role -> Double;
//factor: for Int p, Int a, Ppos p_p, Ppos p_a ,Role r if ppos(p,p_p) & ppos(a,p_a)
//  add [role(p,a,r)] * l_role_pp_pa_dist(p_p,p_a,bins(0,1,2,3,4,5,10,a-p),r);

//predicate pos and pos at arg + 1
weight l_role_pp_paN1: Ppos x Ppos x Role -> Double;
factor: for Int p, Int a, Ppos p_p, Ppos p_a ,Role r if ppos(p,p_p) & ppos(a+1,p_a)
  add [role(p,a,r)] * l_role_pp_paN1(p_p,p_a,r);

//pred pos and pos at arg - 1
weight l_role_pp_paP1: Ppos x Ppos x Role -> Double;
factor: for Int p, Int a, Ppos p_p, Ppos p_a ,Role r if ppos(p,p_p) & ppos(a-1,p_a)
  add [role(p,a,r)] * l_role_pp_paP1(p_p,p_a,r);

//predicate lemma and arg pos
weight l_role_lp_pa: Slemma x Ppos x Role -> Double;
factor: for Int p, Int a, Slemma l_p, Ppos p_a ,Role r if slemma(p,l_p) & ppos(a,p_a)
  add [role(p,a,r)] * l_role_lp_pa(l_p,p_a,r);


//predicate pos and argument lemma
weight l_role_pp_la: Ppos x Slemma x Role -> Double;
factor: for Int p, Int a, Ppos p_p, Slemma l_a ,Role r if ppos(p,p_p) & slemma(a,l_a)
  add [role(p,a,r)] * l_role_pp_la(p_p,l_a,r);

