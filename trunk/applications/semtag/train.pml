include "model.pml";

load weights from dump "/tmp/semtag.weights";
load instances from "/tmp/semtag.instances";
learn for 20 epochs;
save weights to dump "/tmp/semtag.trained.weights";
