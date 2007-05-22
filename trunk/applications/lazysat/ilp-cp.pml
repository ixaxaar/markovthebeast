//include "corpora/bibserv/bibserv.types.pml";
//include "corpora/bibserv/bibserv.predicates.pml";

include "link.types.pml";
include "link.predicates.pml";


include "bibserv.factors.pml";

hidden: sameBib, sameAuthor, sameTitle, sameVenue;
observed: venue, title, author,
  titleScore100, titleScore80, titleScore60, titleScore40, titleScore20, titleScore0,
  authorScore100, authorScore80, authorScore60, authorScore40, authorScore20, authorScore0,
  venueScore100, venueScore80, venueScore60, venueScore40, venueScore20, venueScore0;

//load corpus from "corpora/bibserv/bibserv.150.1.crp";
load corpus from "link.atoms";

save corpus to ram;

load weights from "bibserv.weights";


next;

set solver.profile = true;
set solver.model.initIntegers = true;
set solver.timeout =  
solve;

print history;
print solver.profiler;

//set solver.profile = true;
//set solver.model.initIntegers = false;
//set solver.integer = true;
//solve;

//print history;
//print solver.profiler;



