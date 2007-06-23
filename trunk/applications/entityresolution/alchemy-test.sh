#!/bin/bash

experiment=pseudo-mult-mws-5-1m-nonlazy

for i in 0 1 2 3 4 5 6 7 8 9; do \
##for i in 3; do \
weights=weights/multiple-$i.weights
#weights=bibserv.weights
mln=weights/hard-multiple-$i.mln
#mln=corpora/cora/cora.mln
db=corpora/cora/folds/stripped.corafold-$i.db
gold=corpora/cora/folds/corafold-$i.db.atoms
out=/tmp/$experiment-$i.out

echo Fold $i
echo "Inference"

~/opt/alchemy/bin/infer -seed 1 -mwsMaxSteps 1000000 -tries 5 -i $mln -e $db -r $out \
 -q SameBib,SameTitle,SameAuthor,SameVenue -m > results/$experiment-$i.alchemy.output
echo "converting to atoms..."
java -Xmx500m -cp ../../classes/production thebeast.util.alchemy.AlchemyConverter \
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

