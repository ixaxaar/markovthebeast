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

/* Now we want to apply our MLN to all worlds in the test corpus
   and write out the result. Note that this will also print out
   some statistics. */
test to "system.atoms";
