//palmer prune feature
weight w_palmer_pruned_role: Double-;
factor: for Int p, Int a, Role r if word(p,_) & word(a,_) & !palmer(p,a) add [role(p,a,r)] * w_palmer_pruned_role;

//palmer prune feature combined with pos of predicate
weight w_palmer_pruned_pos_role: Ppos -> Double-;
factor: for Int p, Int a, Ppos pp, Role r if ppos(p,pp) & word(a,_) & !palmer(p,a)
  add [role(p,a,r)] * w_palmer_pruned_pos_role(pp);

//dep label of both
weight w_role_dep_p_a: MDependency x MDependency x Role -> Double;
factor: for Int a, Int p, Role r, MDependency d_a, MDependency d_p if m_dep(_,a,d_a) & m_dep(_,p,d_p)
  add[role(p,a,r)] * w_role_dep_p_a(d_p,d_a,r);

//dep label of predicate
weight w_role_dep_p: MDependency x Role -> Double;
factor: for Int a, Int p, Role r, MDependency d_p if m_dep(_,a,_) & m_dep(_,p,d_p)
  add[role(p,a,r)] * w_role_dep_p(d_p,r);

//dep label of argument
weight w_role_dep_a: MDependency x Role -> Double;
factor: for Int a, Int p, Role r, MDependency d_a if m_dep(_,a,d_a) & m_dep(_,p,_)
  add[role(p,a,r)] * w_role_dep_a(d_a,r);

//dep path length between two tokens
weight w_role_path_length: Int x Role -> Double;
factor: for Int a, Int p, Role r, Int length if word(a,_) & word(p,_) & m_path_length(p,a,length)
  add[role(p,a,r)] * w_role_path_length(bins(0,1,2,3,4,5,10,length),r);

//dep path frame between predicate and argument
weight w_role_dep_path_frame: MPathFrame x Role -> Double;
factor: for Int a, Int p, Role r, MPathFrame pathFrame if m_path_frame(p,a,pathFrame)
  add[role(p,a,r)] * w_role_dep_path_frame(pathFrame,r);

//dep path frame between predicate and argument and voice
weight w_role_dep_path_frame_voice: MPathFrame x Role x Voice -> Double;
factor: for Int a, Int p, Role r, MPathFrame pathFrame, Voice v if m_path_frame(p,a,pathFrame) & voice(p,v)
  add[role(p,a,r)] * w_role_dep_path_frame_voice(pathFrame,r,v);

//dep path frame between predicate and argument combined with predicate lemma
weight w_role_dep_path_frame_lemma_p: MPathFrame x Lemma x Role -> Double;
factor: for Int a, Int p, Role r, MPathFrame pathFrame, Lemma l if m_path_frame(p,a,pathFrame) & lemma(p,l)
  add[role(p,a,r)] * w_role_dep_path_frame_lemma_p(pathFrame,l,r);

//dep path frame between predicate and argument combined with predicate lemma and voice
weight w_role_dep_path_frame_lemma_p_voice: MPathFrame x Lemma x Role x Voice -> Double;
factor: for Int a, Int p, Role r, MPathFrame pathFrame, Lemma l, Voice v if m_path_frame(p,a,pathFrame) & lemma(p,l) & voice(p,v)
  add[role(p,a,r)] * w_role_dep_path_frame_lemma_p_voice(pathFrame,l,r,v);

//unlabeled dep path frame between predicate and argument
weight w_role_dep_path_frame_unlabeled: MPathFrameUnlabeled x Role -> Double;
factor: for Int a, Int p, Role r, MPathFrameUnlabeled pathFrame if m_path_frame_unlabeled(p,a,pathFrame)
  add[role(p,a,r)] * w_role_dep_path_frame_unlabeled(pathFrame,r);


/*----------------*/

//dep path between two tokens
weight l_path: Role x MPath -> Double;
factor: for Int a, Int p, Role r, MPath path if lemma(a,_) & lemma(p,_) & m_path(a,p,path)
  add [role(p,a,r)] * l_path(r,path);

//dep path between two tokens and voice
weight l_path_voice: Role x MPath x Voice -> Double;
factor: for Int a, Int p, Role r, MPath path, Voice v if lemma(a,_) & lemma(p,_) & m_path(a,p,path) & voice(p,v)
  add [role(p,a,r)] * l_path_voice(r,path,v);

//unlabeled dep path
weight l_path_unlabeled: Role x MPathUnlabeled -> Double;
factor: for Int a, Int p, Role r, MPathUnlabeled path if lemma(a,_) & lemma(p,_) & m_path_unlabeled(a,p,path)
  add [role(p,a,r)] * l_path_unlabeled(r,path);

//dep path and lemma of predicate
weight l_path_lemma_p: Role x MPath x Lemma -> Double;
factor: for Int a, Int p, Role r, MPath path, Lemma l_p if lemma(a,_) & lemma(p,l_p) & m_path(a,p,path)
  add [role(p,a,r)] * l_path_lemma_p(r,path,l_p);

//unlabeled dep path and lemma of predicate
weight l_path_unlabeled_lemma_p: Role x MPathUnlabeled x Lemma -> Double;
factor: for Int a, Int p, Role r, MPathUnlabeled path, Lemma l_p if lemma(a,_) & lemma(p,l_p) & m_path_unlabeled(a,p,path)
  add [role(p,a,r)] * l_path_unlabeled_lemma_p(r,path,l_p);

//dep path and lemma of argument
weight l_path_lemma_a: Role x MPath x Lemma -> Double;
factor: for Int a, Int p, Role r, MPath path, Lemma l_a if lemma(a,l_a) & lemma(p,_) & m_path(a,p,path)
  add [role(p,a,r)] * l_path_lemma_a(r,path,l_a);

//dep frame for predicate
weight l_frame_p: Role x MFrame -> Double;
factor: for Int a, Int p, Role r, MFrame frame if lemma(a,_) & lemma(p,_) & m_frame(p,frame)
  add [role(p,a,r)] * l_frame_p(r,frame);

//dep frame and lemma of predicate
weight l_frame_lemma_p: Role x MFrame x Lemma -> Double;
factor: for Int a, Int p, Role r, MFrame frame, Lemma l_p if lemma(a,_) & lemma(p,l_p) & m_frame(p,frame)
  add [role(p,a,r)] * l_frame_lemma_p(r,frame,l_p);

//dep frame of predicate and lemma of argument
weight l_frame_p_lemma_a: Role x MFrame x Lemma -> Double;
factor: for Int a, Int p, Role r, MFrame frame, Lemma l_a if lemma(a,l_a) & lemma(p,_) & m_frame(p,frame)
  add [role(p,a,r)] * l_frame_p_lemma_a(r,frame,l_a);

/*
//dep frame of predicate and distance
weight l_frame_p_dist: Role x MFrame x Int -> Double;
factor: for Int a, Int p, Role r, MFrame frame if lemma(a,_) & lemma(p,_) & m_frame(p,frame)
  add [role(p,a,r)] * l_frame_p_dist(r,frame,bins(0,1,2,3,4,5,10,a-p));
*/
/*
//dep frame for argument
weight l_frame_a: Role x MFrame -> Double;
factor: for Int a, Int p, Role r, MFrame frame if lemma(a,_) & lemma(p,_) & m_frame(a,frame)
  add [role(p,a,r)] * l_frame_a(r,frame);
*/ 

