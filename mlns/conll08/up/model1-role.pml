/* index for faster processing */
//index: role(*,*,_);

/* what is hidden */
hidden: role;//,isPredicate,isArgument;

/* hard constraints */
include "role-hard.pml";

include "role-simple.pml";
include "role-path.pml";
include "role-pp-attach.pml";

/*
include "role-bias.pml";
include "role-new.pml";
include "role-pos-window.pml";
include "role-path-frame.pml";
include "role-pp-attach.pml";
*/
/* dependency based local formulas */
//include "role-dep.pml";

/* bias features */
//include "role-bias.pml";

/* local basic formulae */
//include "role-basic.pml";
