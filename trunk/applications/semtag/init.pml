include "model.pml";

load corpus from "corpora/train.data";
save corpus (0-1000) to ram;

collect;
save corpus to instances "/tmp/semtag.instances";
save weights to dump "/tmp/semtag.weights";
