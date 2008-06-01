include "types.pml";
include "model.pml";

//set instancesCacheSize = 5;
set corpusCacheSize = 20;

load weights from dump $1;

//set solver.model.initIntegers = true;
set solver.integer = true;
set solver.maxIterations = 50;
//set solver.model.solver.params = "/tmp/test.params";
set solver.model.solver.bbDepthLimit=-50;
set solver.model.solver.timeout = 1;
//set solver.model.solver.breakAtFirst = true;
//set solver.model.solver.writeLP=true;

load corpus from $2;

//save corpus(0-1000) to ram;

test to ram;