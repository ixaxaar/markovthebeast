/* Load the constant types */
include "srl-types.pml";

/* Load the SRL mln */
include "srl.pml";

/* Loading the global atoms that hold in every possible world */
load global from "global.atoms";

/* Load a collection of possible worlds for training */
load corpus from "train.atoms";

/* Collect the weights that are allowed to be nonzero */
collect;

/* Save these weights (Note that right now they are still all zero) */
save weights to dump "srl-clean.weights";

/* We need to remember the types we created from the training data
   for later */
types to "types.pml";

/* Save the corpus we loaded above as training instances to a temporary file.
   This is mandatory for training the weights. Often this will take some time
   because some preprocessing is done to speed up training later. You can also
   reuse this in later sessions. */
save corpus to instances "srl.instances";
