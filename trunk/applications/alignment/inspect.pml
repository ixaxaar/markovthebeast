include "types.pml";
include "model-pairs.pml";

//set instancesCacheSize = 5;
set corpusCacheSize = 20;

load weights from dump "/tmp/alignment.clean.weights.dmp";
//load weights from dump "/tmp/alignment.weights.dmp";
//load weights from dump "/tmp/epoch_4.dmp";

set solver.model.initIntegers = true;
//set solver.model.solver.params = "/tmp/test.params";
set solver.model.solver.bbDepthLimit=-50;
set solver.model.solver.timeout = 10;
//set solver.model.solver.breakAtFirst = true;
set solver.model.solver.writeLP=true;

load corpus from "corpora/chi-eng.1000.rest.atoms";
//load corpus from "corpora/chi-eng.1000.atoms";

save corpus(0-100) to ram;

set printer = "align";


