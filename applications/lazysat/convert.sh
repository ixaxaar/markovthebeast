#!/bin/bash

dir=$1

for db in $dir/db/*; do
java -Xmx500m -cp ../../classes/production thebeast.pml.corpora.AlchemyConverter \
  $dir/bibserv.mln \
  $db \
  $db.types.pml \
  $db.predicates.pml \
  $db.atoms
done;
