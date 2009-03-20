//palmer prune feature
weight w_palmer_pruned: Double-;
factor: for Int p, Int a if word(p,_) & word(a,_) & !palmer(p,a) & possiblePredicate(p) & possibleArgument(a) add [hasLabel(p,a)] * w_palmer_pruned;

//palmer prune feature combined with pos of predicate
weight w_palmer_pruned_pos: Ppos -> Double-;
factor: for Int p, Int a, Ppos pp if ppos(p,pp) & word(a,_) & !palmer(p,a) & possiblePredicate(p) & possibleArgument(a) 
  add [hasLabel(p,a)] * w_palmer_pruned_pos(pp);

//dep label of both
weight w_dep_p_a: MDependency x MDependency -> Double;
factor: for Int a, Int p, MDependency d_a, MDependency d_p if mst_dep(_,a,d_a) & mst_dep(_,p,d_p) & possiblePredicate(p) & possibleArgument(a) 
  add[hasLabel(p,a)] * w_dep_p_a(d_p,d_a);

//dep label of both & voice
weight w_dep_p_a_voice: MDependency x MDependency x Voice -> Double;
factor: for Int a, Int p, MDependency d_a, MDependency d_p, Voice v if mst_dep(_,a,d_a) & mst_dep(_,p,d_p) & voice(p,v) & possiblePredicate(p) & possibleArgument(a) 
  add[hasLabel(p,a)] * w_dep_p_a_voice(d_p,d_a,v);

//dep label of predicate
weight w_dep_p: MDependency -> Double;
factor: for Int a, Int p, MDependency d_p if mst_dep(_,a,_) & mst_dep(_,p,d_p) & possiblePredicate(p) & possibleArgument(a) 
  add[hasLabel(p,a)] * w_dep_p(d_p);

//dep label of argument
weight w_dep_a: MDependency -> Double;
factor: for Int a, Int p, MDependency d_a if mst_dep(_,a,d_a) & mst_dep(_,p,_) & possiblePredicate(p) & possibleArgument(a)
  add[hasLabel(p,a)] * w_dep_a(d_a);

//dep path length between two tokens
weight w_path_length: Int -> Double;
factor: for Int a, Int p, Int length if word(a,_) & word(p,_) & mst_path_length(p,a,length) & possiblePredicate(p) & possibleArgument(a)
  add[hasLabel(p,a)] * w_path_length(bins(0,1,2,3,4,5,10,length));

//dep path frame between predicate and argument
weight w_dep_path_frame: MPathFrame -> Double;
factor: for Int a, Int p, MPathFrame pathFrame if mst_path_frame(p,a,pathFrame) & possiblePredicate(p) & possibleArgument(a)
  add[hasLabel(p,a)] * w_dep_path_frame(pathFrame);

//dep path frame between predicate and argument & voice
weight w_dep_path_frame_voice : MPathFrame x Voice -> Double;
factor: for Int a, Int p, MPathFrame pathFrame, Voice v if mst_path_frame(p,a,pathFrame) & voice(p,v) & possiblePredicate(p) & possibleArgument(a)
  add[hasLabel(p,a)] * w_dep_path_frame_voice(pathFrame,v);

//dep path frame between predicate and argument combined with predicate lemma
weight w_dep_path_frame_lemma_p: MPathFrame x Lemma -> Double;
factor: for Int a, Int p, MPathFrame pathFrame, Lemma l if mst_path_frame(p,a,pathFrame) & lemma(p,l) & possiblePredicate(p) & possibleArgument(a)
  add[hasLabel(p,a)] * w_dep_path_frame_lemma_p(pathFrame,l);

//dep path frame between predicate and argument combined with predicate wnet
//weight w_dep_path_frame_wnet_p: MPathFrame x WNet -> Double;
//factor: for Int a, Int p, MPathFrame pathFrame, WNet l if mst_path_frame(p,a,pathFrame) & wnet(p,l)
//  add[hasLabel(p,a)] * w_dep_path_frame_wnet_p(pathFrame,l);

//dep path frame between predicate and argument combined with argument wnet
//weight w_dep_path_frame_wnet_a: MPathFrame x WNet -> Double;
//factor: for Int a, Int p, MPathFrame pathFrame, WNet l if mst_path_frame(p,a,pathFrame) & wnet(a,l)
//  add[hasLabel(p,a)] * w_dep_path_frame_wnet_a(pathFrame,l);

//unlabeled dep path frame between predicate and argument
weight w_dep_path_frame_unlabeled: MPathFrameUnlabeled -> Double;
factor: for Int a, Int p, MPathFrameUnlabeled pathFrame if mst_path_frame_unlabeled(p,a,pathFrame) & possiblePredicate(p) & possibleArgument(a)
  add[hasLabel(p,a)] * w_dep_path_frame_unlabeled(pathFrame);

//dep path between two tokens
weight l_hasLabel_path:  MPath -> Double;
factor: for Int a, Int p,  MPath path if lemma(a,_) & lemma(p,_) & mst_path(a,p,path) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_path(path);

//dep path between two tokens & voice
weight l_hasLabel_path_voice:  MPath x Voice -> Double;
factor: for Int a, Int p,  MPath path, Voice v if lemma(a,_) & lemma(p,_) & mst_path(a,p,path) & voice(p,v) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_path_voice(path,v);

//unlabeled dep path between two tokens
weight l_hasLabel_path_unlabeled:  MPathUnlabeled -> Double;
factor: for Int a, Int p,  MPathUnlabeled path if lemma(a,_) & lemma(p,_) & mst_path_unlabeled(a,p,path) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_path_unlabeled(path);

//unlabeled dep path between two tokens & voice
weight l_hasLabel_path_unlabeled_voice:  MPathUnlabeled x Voice -> Double;
factor: for Int a, Int p,  MPathUnlabeled path, Voice v if lemma(a,_) & lemma(p,_) & mst_path_unlabeled(a,p,path) & voice(p,v) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_path_unlabeled_voice(path,v);


//dep path and lemma of predicate
weight l_hasLabel_path_lemma_p:  MPath x Lemma -> Double;
factor: for Int a, Int p,  MPath path, Lemma l_hasLabel_p if lemma(a,_) & lemma(p,l_hasLabel_p) & mst_path(a,p,path) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_path_lemma_p(path,l_hasLabel_p);

//dep path and wnet of predicate
//weight l_hasLabel_path_wnet_p:  MPath x WNet -> Double;
//factor: for Int a, Int p,  MPath path, WNet l_hasLabel_p if wnet(a,_) & wnet(p,l_hasLabel_p) & mst_path(a,p,path)
//  add [hasLabel(p,a)] * l_hasLabel_path_wnet_p(path,l_hasLabel_p);

//unlabeled dep path and lemma of predicate
weight l_hasLabel_path_unlabeled_lemma_p:  MPathUnlabeled x Lemma -> Double;
factor: for Int a, Int p,  MPathUnlabeled path, Lemma l_hasLabel_p if lemma(a,_) & lemma(p,l_hasLabel_p) & mst_path_unlabeled(a,p,path) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_path_unlabeled_lemma_p(path,l_hasLabel_p);

//unlabeled dep path and wnet of predicate
//weight l_hasLabel_path_unlabeled_wnet_p:  MPathUnlabeled x WNet -> Double;
//factor: for Int a, Int p,  MPathUnlabeled path, WNet l_hasLabel_p if wnet(a,_) & wnet(p,l_hasLabel_p) & mst_path_unlabeled(a,p,path)
//  add [hasLabel(p,a)] * l_hasLabel_path_unlabeled_wnet_p(path,l_hasLabel_p);

//dep path and lemma of argument
weight l_hasLabel_path_lemma_a:  MPath x Lemma -> Double;
factor: for Int a, Int p,  MPath path, Lemma l_hasLabel_a if lemma(a,l_hasLabel_a) & lemma(p,_) & mst_path(a,p,path) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_path_lemma_a(path,l_hasLabel_a);

//dep path and wnet of argument
//weight l_hasLabel_path_wnet_a:  MPath x WNet -> Double;
//factor: for Int a, Int p,  MPath path, WNet l_hasLabel_a if wnet(a,l_hasLabel_a) & wnet(p,_) & mst_path(a,p,path)
//  add [hasLabel(p,a)] * l_hasLabel_path_wnet_a(path,l_hasLabel_a);

//dep frame for predicate
weight l_hasLabel_frame_p: MFrame -> Double;
factor: for Int a, Int p,  MFrame frame if lemma(a,_) & lemma(p,_) & mst_frame(p,frame) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_frame_p(frame);

//unlabeled dep frame
weight l_hasLabel_frame_unlabeled_p:  MFrameUnlabeled -> Double;
factor: for Int a, Int p,  MFrameUnlabeled frame if lemma(a,_) & lemma(p,_) & mst_frame_unlabeled(p,frame) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_frame_unlabeled_p(frame);

//dep frame and lemma of predicate
weight l_hasLabel_frame_lemma_p:  MFrame x Lemma -> Double;
factor: for Int a, Int p,  MFrame frame, Lemma l_hasLabel_p if lemma(a,_) & lemma(p,l_hasLabel_p) & mst_frame(p,frame) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_frame_lemma_p(frame,l_hasLabel_p);

//dep frame and wnet of predicate
//weight l_hasLabel_frame_wnet_p:  MFrame x WNet -> Double;
//factor: for Int a, Int p,  MFrame frame, WNet l_hasLabel_p if wnet(a,_) & wnet(p,l_hasLabel_p) & mst_frame(p,frame)
//  add [hasLabel(p,a)] * l_hasLabel_frame_wnet_p(frame,l_hasLabel_p);

//unlabeled dep frame and lemma of predicate
weight l_hasLabel_frame_unlabeled_lemma_p:  MFrameUnlabeled x Lemma -> Double;
factor: for Int a, Int p,  MFrameUnlabeled frame, Lemma l_hasLabel_p if lemma(a,_) & lemma(p,l_hasLabel_p) & mst_frame_unlabeled(p,frame) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_frame_unlabeled_lemma_p(frame,l_hasLabel_p);

//dep frame of predicate and lemma of argument
weight l_hasLabel_frame_p_lemma_a:  MFrame x Lemma -> Double;
factor: for Int a, Int p,  MFrame frame, Lemma l_hasLabel_a if lemma(a,l_hasLabel_a) & lemma(p,_) & mst_frame(p,frame) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * l_hasLabel_frame_p_lemma_a(frame,l_hasLabel_a);

//dep frame of predicate and wnet of argument
//weight l_hasLabel_frame_p_wnet_a:  MFrame x WNet -> Double;
//factor: for Int a, Int p,  MFrame frame, WNet l_hasLabel_a if wnet(a,l_hasLabel_a) & wnet(p,_) & mst_frame(p,frame)
//  add [hasLabel(p,a)] * l_hasLabel_frame_p_wnet_a(frame,l_hasLabel_a);

//same token features: mst_frame
weight w_same_mst_frame: MFrame -> Double;
factor: for Int p, MFrame l if mst_frame(p,l)  & possiblePredicate(p) & possibleArgument(p) add [hasLabel(p,p)] * w_same_mst_frame(l);

//same token features: mst_frame_unlabeled
weight w_same_mst_frame_unlabeled: MFrameUnlabeled -> Double;
factor: for Int p, MFrameUnlabeled l if mst_frame_unlabeled(p,l)  & possiblePredicate(p) & possibleArgument(p) add [hasLabel(p,p)] * w_same_mst_frame_unlabeled(l);


//pp attachment features
weight w_pp_attach1: Ppos x Ppos -> Double;
factor: for Int p, Int a, Int m, Ppos pm, Ppos pp if ppos(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & ppos(m,pm) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * w_pp_attach1(pp,pm);

//weight w_pp_attach2: Ppos x WNet -> Double;
//factor: for Int p, Int a, Int m, WNet pm, Ppos pp if ppos(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & wnet(m,pm)
//  add [hasLabel(p,a)] * w_pp_attach2(pp,pm);

//weight w_pp_attach3: WNet x Ppos -> Double;
//factor: for Int p, Int a, Int m, WNet pp, Ppos pm if wnet(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & ppos(m,pm)
//  add [hasLabel(p,a)] * w_pp_attach3(pp,pm);

weight w_pp_attach4: Ppos x Lemma -> Double;
factor: for Int p, Int a, Int m, Lemma pm, Ppos pp if ppos(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & lemma(m,pm) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * w_pp_attach4(pp,pm);

weight w_pp_attach5: Lemma x Ppos -> Double;
factor: for Int p, Int a, Int m, Ppos pm, Lemma pp if lemma(p,pp) & ppos(a,"IN") & mst_dep(a,m,_) & ppos(m,pm) & possiblePredicate(p) & possibleArgument(a)
  add [hasLabel(p,a)] * w_pp_attach5(pp,pm);

/*
//generic attachment features
weight w_hasLabel_attach: MDependency x MDependency -> Double;
factor: for Int p, Int a, Int m, MDependency dpa, MDependency dam if mst_dep(p,a,dpa) & mst_dep(a,m,dam)
  add [hasLabel(p,a)] * w_hasLabel_attach(dpa,dam);

weight w_hasLabel_attach_lp: MDependency x MDependency x Lemma -> Double;
factor: for Int p, Int a, Int m, MDependency dpa, MDependency dam, Lemma lp if mst_dep(p,a,dpa) & mst_dep(a,m,dam) & lemma(p,lp)
  add [hasLabel(p,a)] * w_hasLabel_attach_lp(dpa,dam,lp);

weight w_hasLabel_attach_la: MDependency x MDependency x Lemma -> Double;
factor: for Int p, Int a, Int m, MDependency dpa, MDependency dam, Lemma la if mst_dep(p,a,dpa) & mst_dep(a,m,dam) & lemma(a,la)
  add [hasLabel(p,a)] * w_hasLabel_attach_la(dpa,dam,la);

weight w_hasLabel_attach_pa: MDependency x MDependency x Ppos -> Double;
factor: for Int p, Int a, Int m, MDependency dpa, MDependency dam, Ppos pa if mst_dep(p,a,dpa) & mst_dep(a,m,dam) & ppos(a,pa)
  add [hasLabel(p,a)] * w_hasLabel_attach_pa(dpa,dam,pa);

weight w_hasLabel_attach_wa: MDependency x MDependency x WNet -> Double;
factor: for Int p, Int a, Int m, MDependency dpa, MDependency dam, WNet wa if mst_dep(p,a,dpa) & mst_dep(a,m,dam) & wnet(a,wa) 
  add [hasLabel(p,a)] * w_hasLabel_attach_wa(dpa,dam,wa);
*/

/*
//dep frame of predicate and distance
weight l_hasLabel_frame_p_dist:  MFrame x Int -> Double;
factor: for Int a, Int p,  MFrame frame if lemma(a,_) & lemma(p,_) & mst_frame(p,frame)
  add [hasLabel(p,a)] * l_hasLabel_frame_p_dist(frame,bins(0,1,2,3,4,5,10,a-p));
*/
/*
//dep frame for argument
weight l_hasLabel_frame_a:  MFrame -> Double;
factor: for Int a, Int p,  MFrame frame if lemma(a,_) & lemma(p,_) & mst_frame(a,frame)
  add [hasLabel(p,a)] * l_hasLabel_frame_a(frame);
*/ 

