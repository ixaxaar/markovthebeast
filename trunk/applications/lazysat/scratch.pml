include "corpora/bibserv/bibserv.types.pml";
include "corpora/bibserv/bibserv.predicates.pml";

//include "/tmp/bibserv/bibserv.100.1.db.types.pml";
//include "/tmp/bibserv/bibserv.100.1.db.predicates.pml";


include "bibserv.factors.pml";

hidden: sameBib, sameAuthor, sameTitle, sameVenue;
observed: venue, title, author,
  titleScore100, titleScore80, titleScore60, titleScore40, titleScore20, titleScore0,
  authorScore100, authorScore80, authorScore60, authorScore40, authorScore20, authorScore0,
  venueScore100, venueScore80, venueScore60, venueScore40, venueScore20, venueScore0;

load corpus from "corpora/bibserv/bibserv.100.1.crp";
//load corpus from "/tmp/bibserv/bibserv.100.1.db.atoms";

save corpus to ram;

load weights from "bibserv.weights";
set solver.profile = true;


next;

//set solver.model.initIntegers = true;
set solver.model = "sat";
set solver.model.solver.maxFlips = 10000;
set solver.maxIterations = 5;
solve;


print history;
print solver.profiler;

/*
set solver.profile = true;
set solver.model.initIntegers = false;
set solver.integer = true;
solve;

print history;
print solver.profiler;
*/


