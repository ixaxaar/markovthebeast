/* Load the types we generated based on the training corpus */
include "types.pml";

/* Load the SRL mln */
include "srl.pml";

/* Loading the global atoms that hold in every possible world */
load global from "global.atoms";

/* Load the weights we allowed to be nonzero in the collection step (init.pml) */
load weights from dump "srl.weights";

/* Load a test corpus */
load corpus from "test.atoms";

/* Just loading makes the corpus available for
   the processors within thebeast but not for random
   access through the shell. This is achieved using ... */
save corpus to ram;

/* Now we want to manually see how our model does on the
   test data. */

// go to the next world in the corpus (the first in this case)
next;

// use the current model (the one we trained) and find the most likely world
solve;

// print the resulting labels to the screen
print atoms.role;

// compare the results to the gold labels
print eval;

// do the same thing for the next world
next; solve; print atoms.role; print eval;