//include "corpora/bibserv/bibserv.types.pml";
//include "corpora/bibserv/bibserv.predicates.pml";


//include "corpora/cora/fold7of10.fold.types.pml";
//include "corpora/cora/db/cora.100.5.db.types.pml";
//include "corpora/cora/fold0of10.fold.predicates.pml";
//include "/tmp/bibserv/bibserv.100.1.db.types.pml";
//include "/tmp/bibserv/bibserv.100.1.db.predicates.pml";

include "corpora/cora/cora.all.db.types.pml";
include "corpora/cora/cora.all.db.predicates.pml";


include "bibserv.factors.pml";

hidden: sameBib, sameAuthor, sameTitle, sameVenue;
observed: venue, title, author,
  titleScore100, titleScore80, titleScore60, titleScore40, titleScore20, titleScore0,
  authorScore100, authorScore80, authorScore60, authorScore40, authorScore20, authorScore0,
  venueScore100, venueScore80, venueScore60, venueScore40, venueScore20, venueScore0;

//load corpus from "corpora/bibserv/bibserv.100.1.crp";
//load corpus from "/tmp/bibserv/bibserv.100.1.db.atoms";

//load corpus from "corpora/cora/fold0of10.cat.rest.atoms";
load corpus from "corpora/cora/fold9of10.fold.atoms";
//load corpus from "corpora/cora/db/cora.100.5.db.atoms";

//load weights from "bibserv.weights";
load weights from "test.weights";

//save corpus to instances "/tmp/er.inst.dmp";

//load instances from dump "er.inst.dmp";

//set learner.initWeights = true;
//set learner.initWeight = 0.0;
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
set learner.solver.model.solver.timeout=10;

//learn for 10 epochs;

//print weights;


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
//print formulas;




//load weights from "bibserv.weights";
//set solver.profile = true;


//next;

//set solver.ilp.solver = "osi";
//set solver.ilp.solver.implementation = "clp";
//set solver.model.initIntegers = true;
//set solver.model = "sat";
//set solver.model.solver.maxFlips = 10000;
//set solver.maxIterations = 5;
//set solver.integer = true;

//solve;


//print history;
//print solver.profiler;
//print solver.model.fractionals;

/*
set solver.profile = true;
set solver.model.initIntegers = false;
set solver.integer = true;
solve;

print history;
print solver.profiler;
*/


