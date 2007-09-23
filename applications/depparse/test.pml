include "corpora/train.types.pml";                    

include "model.pml";

set instancesCacheSize = 5;

//load corpus from conll06 "test.conll";
load corpus from conll06 $2;

//load weights from dump "/tmp/depparse.weights.dmp";
load weights from dump $1;

//save corpus (400-800) to ram;

set solver.profile = true;
set solver.maxIterations = 100;
set solver.integer = true;
//set solver.model.initIntegers = true;
set solver.model.solver.bbDepthLimit=10;
//set solver.order.label_geq1 = 1;
//set solver.order.dep_link = 1;
set solver.order.overlap1 = 1;
set solver.order.overlap2 = 1;
set solver.order.nocycles = 2;

test to ram;

print solver.profiler;
