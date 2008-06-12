/* Load the types we generated based on the training corpus */
include "types.pml";

/* Load the align mln */
include "align.pml";

/* Loading the global atoms that hold in every possible world */
load global from "global.atoms";

/* Load the weights we allowed to be nonzero in the collection step (init.pml) */
load weights from dump "align-clean.weights";

/* Load the preprocessed training corpus */
load instances from dump "align.instances";

/* Now do online learning for 10 epochs (by default this uses MIRA) */
learn for 10 epochs;

/* Save them in binary form for later reuse */
save weights to dump "align.weights";

