//include "corpora/bibserv/bibserv.50.2.db.types.pml";
//include "corpora/bibserv/bibserv.50.2.db.predicates.pml";

include "types.pml";
include "corpora/cora/cora.all.db.predicates.pml";

include "bibserv.factors.pml";

hidden: sameBib, sameAuthor, sameTitle, sameVenue;
observed: venue, title, author,
  titleScore100, titleScore80, titleScore60, titleScore40, titleScore20, titleScore0,
  authorScore100, authorScore80, authorScore60, authorScore40, authorScore20, authorScore0,
  venueScore100, venueScore80, venueScore60, venueScore40, venueScore20, venueScore0;

//load corpus from "corpora/bibserv/bibserv.50.2.db.atoms";
//load corpus from "corpora/bibserv/bibserv.100.1.crp";
load corpus from "test.atoms";

save corpus to ram;

load weights from "txt.weights";

next;

//set solver.profile = true;
//set solver.integer = true;


set solver.model = "sat";
set solver.model.solver.seed = 1;
set solver.model.solver.maxRestarts = 1;
set solver.model.solver.updateRandom = false;
set solver.model.solver.maxFlips = 1000000;
set solver.model.solver.greedyProb = 0.9;
set solver.model.solver.debug = false;
set solver.groundAll = true;
set solver.model.singleCallMode = true;
set solver.maxIterations = 1;
//set solver.profiler = "live";


solve;

print history;
print solver.profiler;

/*
print atoms.sameBib;
print atoms.sameTitle;
print atoms.sameAuthor;
print atoms.sameVenue;
*/

//set solver.profile = true;
//set solver.model.initIntegers = false;
//set solver.integer = true;
//solve;

//print history;
//print solver.profiler;



