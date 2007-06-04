include "corpora/cora/cora.all.db.types.pml";
include "corpora/cora/cora.all.db.predicates.pml";

include "bibserv.factors.pml";

hidden: sameBib, sameAuthor, sameTitle, sameVenue;
observed: venue, title, author,
  titleScore100, titleScore80, titleScore60, titleScore40, titleScore20, titleScore0,
  authorScore100, authorScore80, authorScore60, authorScore40, authorScore20, authorScore0,
  venueScore100, venueScore80, venueScore60, venueScore40, venueScore20, venueScore0;

load corpus from "test.atoms";

load weights from "/tmp/er.inst.dmp";


save corpus to ram;
next;

set solver.model = "sat";
set solver.model.solver.maxFlips = 1000000;
set solver.maxIterations = 10;
set solver.model.solver.initRandom = false;
set solver.model.solver.updateRandom = false;
set solver.model.solver.maxRestarts = 1;
set solver.model.solver.seed = 0;
set solver.model.solver.greedyProb = 0.9;

solve;
print history;
print solver.profiler;
print solver.model.fractionals;
