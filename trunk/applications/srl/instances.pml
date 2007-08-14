include "types.pml";
include "model.pml";

set instancesCacheSize = 5;
set corpusCacheSize = 20;

//load corpus from "one-sentence.crp";
//load corpus from "corpora/small-train-set.crp";
//load corpus from "corpora/train-set.crp";
load corpus from "/disk/home/dendrite/s0349492/corpora/conll05/train-set.crp";
//load corpus from "/disk/home/dendrite/s0349492/corpora/conll05/small-train-set.crp";

//save corpus (0-10000) to ram;

//print weights;

load weights from dump "/tmp/srl.clean.weights.dmp";

//inspect Labeller;

//print weights.w_path;

save corpus to instances "/disk/home/dendrite/s0349492/tmp/srl.instances.dmp";
