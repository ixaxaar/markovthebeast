include "model.pml";

load global from "global.txt";

load corpus from "corpora/train.data";
save corpus (0-100) to ram;


collect;
save corpus to instances "/tmp/atis_2_3_train.instances.beast";
learn for 10 epochs;
save weights to dump "/tmp/atis_2_3_train.weights.beast";
