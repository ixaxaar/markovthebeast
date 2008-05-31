/* Predicate definitions */

// The word predicate maps token indices (Int=Integer) to words
predicate word: Int x Word;

// The pos predicate maps token indices to Part of Speech tags
predicate pos: Int x Pos;

// The role predicate role(p,a,r) indicates that the token a is an argument of token p with role r.
predicate role: Int x Int x Role;

// the unique predicate denotes roles which cannot appear more than once (like "A0")
predicate unique: Role;

/* Loading the MLN formulae (local and global ones) */
include "srl-global.pml";
include "srl-local.pml";

/* Defining which predicates are hidden, observed and global. Do not forget this! */
observed: word,pos;
hidden: role;
global: unique;


