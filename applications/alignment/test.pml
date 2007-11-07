include "types.pml";
include "model.pml";

//set instancesCacheSize = 5;
set corpusCacheSize = 20;

load weights from dump $1;

set solver.model.initIntegers = true;
set solver.model.solver.bbDepthLimit=200;

//set solver.order.tgtchunk = 1;
//set solver.order.srcchunk = 1;
//set solver.order.cluster = 1;

load corpus from $2;

test to ram;