#!/bin/bash

experiment=mws-1-10m

for i in 0 1 2 3 4 6 7 8 9; do \
#for i in 1; do \
#weights=weights/doublefold-$i.weights.dmp
weights=bibserv.weights
#mln=$weights.mln
mln=corpora/cora/cora.mln
db=corpora/cora/folds/filtered.${i}of10.doublefold.db
gold=corpora/cora/folds/${i}of10.doublefold.db.atoms
out=/tmp/$i.out

echo Fold $i
echo "Inference"

~/opt/alchemy/bin/infer -seed 0 -mwsMaxSteps 10000000 -i $mln -e $db -r $out \
 -q SameBib,SameTitle,SameAuthor,SameVenue -lazy -m > results/$experiment-$i.alchemy.output
echo "converting to atoms..."
java -Xmx500m -cp ../../classes/production thebeast.pml.corpora.AlchemyConverter \
  $mln \
  $out \
  $out.types.pml \
  $out.predicates.pml \
  $out.atoms
echo "evaluating atoms"
java -Xmx1000m -cp ../../classes/production/:../../lib/jline-0.9.9.jar:../../lib/java-cup-11a.jar:../../lib/lpsolve55j.jar:../../lib/ \
  thebeast.pml.CorpusEvaluation  \
  text model.pml $weights $gold $out.atoms > results/$experiment-$i.results
done;

