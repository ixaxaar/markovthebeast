/* Definition of the types of constants:
   Instead of explicitely defining them we use the "..." notation to indicate that
   constants should be added when seen in the data. */

type Word: ...;
type Pos: ...;
type Role: ...;

/* Predicate definitions */

// The word predicate maps token indices (Int=Integer) to words
predicate word: Int x Word;

// The pos predicate maps token indices to Part of Speech tags
predicate pos: Int x Pos;

// The role predicate role(p,a,r) indicates that the token a is an argument of token p with role r.
predicate role: Int x Int x Role;

// the unique predicate denotes roles which cannot appear more than once (like "A0")
predicate unique: Role;

/* Defining which predicates are hidden, observed and global. Do not forget this! */
observed: word,pos;
hidden: role;
global: unique;

/* Loading the MLN formulae (local and global ones)
include "srl-global.pml";
include "srl-local.pml";

/* Loading the global atoms that hold in every possible world */
load global from "global.atoms";

/* Load a collection of possible worlds for training */
load corpus from "train.atoms";

/* Collect the formula instantiations that are allowed to have nonzero weights */
collect;

/* Print out what we collected */

print weights;

/* Save the corpus we loaded above as training instances to a temporary file.
   This is mandatory for training the weights. Often this will take some time
   because some preprocessing is done to speed up training later. */
save corpus to instances "/tmp/srl-example.instances";

/* Now do online learning for 10 epochs (by default this uses MIRA) */
learn for 10 epochs;

/* Print the new weights to the screen */
print weights;

/* Save them in binary form for later reuse */
save weights to dump "srl.weights";

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

/* Now we want to apply our MLN to all worlds in the test corpus
   and write out the result. Note that this will also print out
   some statistics. */
test to "system.atoms";






