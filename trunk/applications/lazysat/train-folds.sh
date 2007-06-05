#!/bin/bash

for i in 0 1 2 3 4 5 6 7 8 9; do \
rm train.atoms;
ln -s corpora/cora/folds/train-for-$i.atoms train.atoms;
thebeast init.pml;
thebeast train.pml;
cp /tmp/er.weights.dmp weights/doublefold-${i}.weights.dmp
done;

