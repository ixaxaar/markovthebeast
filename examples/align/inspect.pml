/* Load the types we generated based on the training corpus */
include "types.pml";

/* Load the SRL mln */
include "align.pml";

/* Loading the global atoms that hold in every possible world */
load global from "global.atoms";

/* Load the weights we allowed to be nonzero in the collection step (init.pml) */
load weights from dump "align.weights";

/* Load a test corpus */
load corpus from "test.atoms";

/* Just loading makes the corpus available for
   the processors within thebeast but not for random
   access through the shell. This is achieved using ... */
save corpus to ram;

/* Let's configure and check the solver */

// set the solve to enforce integer constraints (otherwise results might not be exact)
set solver.model.initIntegers = true;

// print the current solver configuration
print solver;

/* Now we want to manually see how our model does on the
   test data. */

// go to the next world in the corpus (the first in this case)
next;

// use the current model (the one we trained) and find the most likely world
// Note how this one is solved in a single iteration using only local formulae (since the lexicalized
// formula fires for each true alignment and the solution is already diagonal).
solve;

// print the resulting labels to the screen
print atoms.align;

// compare the results to the gold labels
print eval;

// do the same thing for the next world
// now cutting plane inference needs a few iterations because some elements are missing from the diagonal.
next; solve; print atoms.align; print eval;

// print the local features for ground atom align(1,1)
print solver.features.align(1,1);