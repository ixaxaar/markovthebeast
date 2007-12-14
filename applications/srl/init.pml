include "types.clean.pml";
include "model-pairs.pml";
//include "model.pml";

set instancesCacheSize = 5;
set corpusCacheSize = 20;

//load corpus from "one-sentence.crp";
//load corpus from "corpora/small-train-set.crp";
//load corpus from "corpora/train-set.crp";
load corpus from $1;
//load corpus from "/disk/home/dendrite/s0349492/corpora/conll05/train-set.crp";
//load corpus from "/disk/home/dendrite/s0349492/corpora/conll05/small-train-set.crp";

save corpus (0-100) to ram;

set collector.cutoff = 1;

collect;

//print weights;

save weights to dump "/tmp/srl.clean.weights.dmp";

//inspect Labeller;

//print weights.w_path;

types to "types.pml";

save corpus to instances "/disk/home/dendrite/s0349492/tmp/srl.instances.dmp";
