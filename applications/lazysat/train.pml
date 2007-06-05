include "corpora/cora/cora.all.db.types.pml";
include "corpora/cora/cora.all.db.predicates.pml";

include "bibserv.factors.pml";

hidden: sameBib, sameAuthor, sameTitle, sameVenue;
observed: venue, title, author,
  titleScore100, titleScore80, titleScore60, titleScore40, titleScore20, titleScore0,
  authorScore100, authorScore80, authorScore60, authorScore40, authorScore20, authorScore0,
  venueScore100, venueScore80, venueScore60, venueScore40, venueScore20, venueScore0;

load corpus from "train.atoms";

load weights from "bibserv.withneg.weights";

load instances from dump "/tmp/er.inst.dmp";

set learner.initWeights = true;
set learner.initWeight = 0.0;
//set learner.solver.ilp.solver = "osi";
//set learner.solver.ilp.solver.implementation = "clp";
//set learner.solver.ilp.solver.timeout = 100;
//set learner.solver.ilp.solver.bbDepthLimit = 5;
//set learner.solver.ilp.solver.implementation = "clp";
set learner.solver.maxIterations = 5;
//set learner.solver.ilp.initIntegers = true;
set learner.solver.integer = false;
set learner.update = "mira";
set learner.update.signs = true;
set learner.maxCandidates = 10;
set learner.average = true;
set learner.maxViolations = 10000000;
set learner.loss = "globalF1";
set learner.profile = true;
set learner.useGreedy = true;
set learner.solver.timeout=20000;
set learner.solver.model.solver.timeout=3;
set learner.profile = true;

set learner.solver.maxIterations = 0;

learn for 5 epochs;

//print learner.profiler;

set learner.initWeights = false;
set learner.solver.maxIterations = 5;

learn for 5 epochs;

save weights to dump "/tmp/er.weights.dmp";




