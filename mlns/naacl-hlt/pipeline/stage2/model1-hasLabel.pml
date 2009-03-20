predicate hasLabel: Int x Int;
index: hasLabel(*,*);
auxiliary: hasLabel;

hidden: hasLabel;

include "hasLabel-hard.pml";
include "hasLabel-basic.pml";
include "hasLabel-dep.pml";