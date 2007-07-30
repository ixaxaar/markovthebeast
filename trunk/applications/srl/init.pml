include "types.clean.pml";
include "model.pml";

set instancesCacheSize = 5;
set corpusCacheSize = 20;

//load corpus from "one-sentence.crp";
load corpus from "corpora/small-train-set.crp";

save corpus (0-1000) to ram;

collect;

save weights to dump "/tmp/srl.clean.weights.dmp";

//inspect Labeller;

//print weights.w_path;

types to "types.pml";

save corpus to instances "/disk/home/dendrite/s0349492/tmp/srl.instances.dmp";
