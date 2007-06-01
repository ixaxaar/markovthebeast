#!/bin/bash

dir=$1
mln=$2

for db in $dir/db/*; do
java -Xmx500m -cp ../../classes/production thebeast.pml.corpora.AlchemyConverter \
  $mln \
  $db \
  $db.types.pml \
  $db.predicates.pml \
  $db.atoms
done;
