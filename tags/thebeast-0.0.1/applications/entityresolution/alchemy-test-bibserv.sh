#!/bin/bash

experiment=bibserv-mws-nonlazy-1-1m-seeded-hard

#for i in 50 100 150 200 250 300 350 400 450 500; do \
for i in 50 100 150 200; do \
for j in 1 2 3 4 5; do
#for i in 3; do \
weights=bibserv.withneg.weights
#weights=bibserv.weights
mln=bibserv.withneg.mln
#mln=corpora/cora/cora.mln
db=/disk/home/dendrite/s0349492/corpora/bibserv/bibserv.$i.$j.db.filtered.db
gold=/disk/home/dendrite/s0349492/corpora/bibserv/bibserv.$i.$j.db.atoms
out=/tmp/$experiment-$i.$j.out
model=$out.model.pml

echo Fold $i
echo "Inference"
#> /tmp/$experiment-$i.$j.alchemy.output

~/opt/alchemy/bin/infer -seed 1 -mwsMaxSteps 1000000 -i $mln -e $db -r $out \
 -q SameBib,SameTitle,SameAuthor,SameVenue -m > /tmp/$experiment-$i.$j.alchemy.output
echo "converting to atoms..."
java -Xmx500m -cp ../../classes/production thebeast.util.alchemy.AlchemyConverter \
  $mln \
  $out \
  $out.types.pml \
  $out.predicates.pml \
  $out.atoms

rm $model
cat /disk/home/dendrite/s0349492/corpora/bibserv/bibserv.$i.$j.db.types.pml > $model
cat /disk/home/dendrite/s0349492/corpora/bibserv/bibserv.$i.$j.db.predicates.pml >>$model
cat hiddenobserved.pml >>$model
echo "evaluating atoms"
java -Xmx1000m -cp ../../classes/production/:../../lib/jline-0.9.9.jar:../../lib/java-cup-11a.jar:../../lib/lpsolve55j.jar:../../lib/ \
  thebeast.pml.CorpusEvaluation  \
  text $model $weights $gold $out.atoms > results/$experiment-$i.$j.results
done;
done;
