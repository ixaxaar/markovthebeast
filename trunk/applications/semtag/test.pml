include "model.pml";

//factor: for Int t1, Int t2 if t1 < t2: !(slot(t1,"TOLOC") & slot(t2,"TOLOC"));

load corpus from "corpora/test.data";

load weights from dump "/tmp/epoch_12.dmp";

test to ram;