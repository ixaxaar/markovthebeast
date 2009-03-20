//global predicates
predicate properarg: Role;
predicate modifier: Role;
predicate carg: Role;
predicate rarg: Role;
predicate cargpair: Role x Role;
predicate rargpair: Role x Role;
predicate allargs: Role;

// MST predicates
observed:  isArgument, hasLabel, role, isPredicate,  word, slemma, gpos, sform, ppos,cpos,sppos, lemma, mst_path_length, 
mst_path_frame, 
mst_path_frame_unlabeled,
  mst_path,mst_frame,mst_link,mst_dep, mst_path_unlabeled, mst_frame_unlabeled, voice, mst_path_directed, palmer,
//  m_path_length, m_path_frame, m_path_frame_unlabeled,
//  m_path,m_frame,m_link,m_dep, m_path_unlabeled, m_frame_unlabeled, voice, m_path_directed, palmer,wnet,
  possibleArgument, possiblePredicate,path_frame_distance;
//postfix
global: properarg,rarg,rargpair,carg,cargpair,allargs;

//index: role(*,*,_);
index: mst_dep(*,*,_);
index: mst_dep(_,*,*);
index: mst_path_frame(*,*,_);
index: mst_path_frame_unlabeled(*,*,_);
index: mst_path_unlabeled(*,*,_);
index: palmer(*,*);
index: mst_frame(*,*);
index: mst_frame_unlabeled(*,*);
index: mst_path(*,*,_);
index: mst_path_directed(*,*,_);

//include "model1-role.pml";
//include "model1-hasLabel.pml";
//include "model1-isArgument.pml";
include "model1-frameLabel_predicate.pml";


//include "model1-dep.pml";
//include "model1-isPredicate.pml";
//include "model1-both.pml";
/*
weight w_possiblePred: Double;
factor: for Int p if word(p,_) & !possiblePredicate(p) add [isPredicate(p)] * w_possiblePred;

weight w_possibleArg: Double;
factor: for Int a if word(a,_) & !possibleArgument(a) add [isArgument(a)] * w_possibleArg;
*/

load global from "global.atoms";
