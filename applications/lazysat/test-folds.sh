#!/bin/bash

exp=pseudo

#for i in 3 4 6 7 8 9; do \
for i in 0; do \
#for i in 0 1 2 3 4 6 7 8 9; do \
echo $i
rm test.atoms;
rm weights.dmp;
#ln -s corpora/cora/folds/dummy.atoms test.atoms;
ln -s corpora/cora/folds/${i}of10.doublefold.db.atoms test.atoms;
ln -s weights/for-fold-$i.weights.dmp weights.dmp;
thebeast test.pml > results/$exp-on-fold-$i.results;
done;

