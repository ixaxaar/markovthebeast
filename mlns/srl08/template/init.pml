include "TYPES";
include "MODEL";

load corpus from "ATOMS";

set collector.cutoff = 1;
collect;

save corpus to instances "INSTANCES";
save weights to dump "WEIGHTS";

