types from conll06 "corpora/train.conll";
types to "corpora/train.types.pml";

include "model.pml";

set instancesCacheSize = 10;

load corpus from conll06 "corpora/train.conll";

save corpus (0-100) to ram;

collect;

save weights to dump "/tmp/depparse.weights.dmp";

save corpus to instances "/tmp/depparse.instances.dmp";

