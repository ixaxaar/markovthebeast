include "types.clean.pml";
include "model.pml";

load corpus from $1;

save corpus (0-100) to ram;

//set collector.cutoff = 1;

collect;

//print weights;

set instancesCacheSize = 5;
set corpusCacheSize = 20;


save weights to dump "/tmp/alignment.clean.weights.dmp";

types to "types.pml";

save corpus to instances "/tmp/alignment.instances.dmp";
