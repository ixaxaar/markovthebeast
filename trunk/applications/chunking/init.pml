types from conll00 "corpora/train.conll";
types to "corpora/train.types.pml";

include "conll00.pml";
include "chunking.pml";
include "tagging.pml";
include "joint.pml";

load corpus from conll00 "corpora/train.conll";

collect;

save weights to dump "/tmp/blank.weights.dmp";

save corpus to instances "/tmp/instances.dmp";
