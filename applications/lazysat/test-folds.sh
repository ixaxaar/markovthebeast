#!/bin/bash

exp=pseudo-mult-cp-mws-1-1k

#for i in 3 4 6 7 8 9; do \
#for i in 7; do \
for i in 0 1 2 3 4 5 6 7 8 9; do \
echo $i
rm test.atoms;
#rm weights.dmp;
rm txt.weights;
#ln -s corpora/cora/folds/dummy.atoms test.atoms;
ln -s corpora/cora/folds/corafold-${i}.db.atoms test.atoms;
ln -s weights/multiple-$i.weights txt.weights;
thebeast test.pml > results/$exp-on-fold-$i.results;
done;

