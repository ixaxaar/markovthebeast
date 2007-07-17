types from conll06 "train.conll";
types to "corpora/train.types.pml";

include "model.pml";

set instancesCacheSize = 5;
set corpusCacheSize = 20;

load corpus from conll06 "train.conll";

save corpus (0-1000) to ram;

collect;
//load weights from dump "/tmp/depparse.clean.weights.dmp";

save weights to dump "/tmp/depparse.clean.weights.1000.dmp";

//save corpus to instances "/tmp/depparse.instances.dmp";
save corpus to instances "/disk/home/dendrite/s0349492/tmp/depparse.instances.1000.dmp";

