/* index for faster processing */
//index: role(*,*,_);

/* what is hidden */
hidden: role;//,isPredicate,isArgument;

/* hard constraints */
include "role-hard.pml";

include "role-simple.pml";
include "role-path.pml";


