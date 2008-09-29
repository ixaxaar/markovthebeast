include "TYPES";
include "MODEL";

load corpus from "ATOMS";
save corpus (0-1000) to ram;

set collector.cutoff = 1;
collect;
save weights to dump "WEIGHTS";
save corpus to instances "INSTANCES";
