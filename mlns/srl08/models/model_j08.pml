//global predicates
predicate properarg: Role;
predicate modifier: Role;
predicate carg: Role;
predicate rarg: Role;
predicate cargpair: Role x Role;
predicate rargpair: Role x Role;
predicate allargs: Role;

// Regular predicates
observed: word, slemma, gpos, sform, ppos, cpos, sppos,lemma,
// HIDDEN
//isPredicate,
//isArgument,
//hasLabel,
//frameLabel,
//role,
// MST info
mst_link,
mst_dep,
// Some heuristics
voice,
possiblePredicate,
possibleArgument,
// Johansson predicates
rightToken,
leftToken,
relPath,
verbChainHasSubj;
global: properarg,rarg,rargpair,carg,cargpair,allargs;

index: role(*,*,_);
index: mst_dep(*,*,_);
index: mst_dep(_,*,*);
index: mst_link(*,*);
index: relPath(_,*,*);
index: childDepSet(*,*);
index: childWordSet(*,*);
index: childWordDepSet(*,*);
index: childPosDepSet(*,*);
index: childPosSet(*,*);
//index: mst_path_frame(*,*,_);
//index: mst_path_frame_unlabeled(*,*,_);
//index: mst_path_unlabeled(*,*,_);
//index: palmer(*,*);
//index: mst_frame(*,*);
//index: mst_frame_unlabeled(*,*);
//index: mst_path(*,*,_);
//index: mst_path_directed(*,*,_);
//include "model1-dep.pml";

include "model_j08-role.pml";
include "model_j08-hasLabel.pml";
include "model_j08-isArgument.pml";
include "model_j08-isPredicate.pml";
include "model_j08-frameLabel.pml";

//include "model1-hasLabel.pml";
//include "model1-isPredicate.pml";
//include "model1-isArgument.pml";
//include "model1-frameLabel.pml";
//include "model1-both.pml";

load global from "global.atoms";
